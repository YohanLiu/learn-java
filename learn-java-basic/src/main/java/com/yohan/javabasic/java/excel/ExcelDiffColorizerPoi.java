package com.yohan.javabasic.java.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

public class ExcelDiffColorizerPoi {

    // 颜色阈值（可根据业务调整）
    private static final double THRESHOLD_GREEN = 0.01;  // <1% 绿色
    private static final double THRESHOLD_YELLOW = 0.1; // 1%-10% 黄色

    private static final String BASE_DIR = System.getProperty("user.dir") + "/learn-java-basic" +"/src/main" +
            "/resources/excel/";

    public static void main(String[] args) throws IOException {
        // 1. 配置路径（修改为你的实际路径）
        String inputPath = BASE_DIR + "预期模版样式.xlsx";
        String outputPath = BASE_DIR + "poi 实现着色后的结果.xlsx";

        // 2. 读取Excel为List<List<String>>（保留原始结构）
        List<List<String>> excelData = readExcelToList(inputPath);
        if (excelData.isEmpty()) {
            System.err.println("未读取到有效数据！");
            return;
        }

        // 3. 计算差异百分比（车型_指标→差异%）
        Map<String, Double> diffMap = calculateDiffMap(excelData);
        if (diffMap.isEmpty()) {
            System.err.println("未计算到有效差异！");
            return;
        }

        // 4. 生成结果Excel（包含预期/实际数据着色）
        generateResultExcel(excelData, diffMap, outputPath);
        System.out.println("结果Excel生成成功：" + outputPath);
    }

    // ****************************** 步骤1：读取Excel为List<List<String>> ******************************
    /**
     * 读取Excel文件，将每行数据转换为List<String>，保留原始结构（包括合并单元格的空值）
     * @param filePath Excel文件路径
     * @return List<List<String>> 每行数据的列表
     */
    private static List<List<String>> readExcelToList(String filePath) throws IOException {
        List<List<String>> excelData = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // 读取第一个Sheet
            if (sheet == null) {
                return excelData;
            }

            // 遍历所有行（从第0行开始，包括表头）
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    excelData.add(Collections.emptyList());
                    continue;
                }

                List<String> rowData = new ArrayList<>();
                // 遍历当前行的所有列（取最大列数，避免遗漏）
                int maxColumn = row.getLastCellNum();
                for (int colIndex = 0; colIndex < maxColumn; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String cellValue = getCellValue(cell);
                    rowData.add(cellValue);
                }
                excelData.add(rowData);
            }
        }
        return excelData;
    }

    /**
     * 获取单元格的值（处理不同类型：字符串、数字、百分比等）
     * @param cell 单元格对象
     * @return String 单元格的文本值
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // 处理日期（避免转换为数字）
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 处理数字（保留原始格式，如154→"154"，-6.30→"-6.30"）
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // 处理公式结果（取计算后的值）
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e) {
                    return cell.getStringCellValue();
                }
            default:
                return "";
        }
    }

    // ****************************** 步骤2：计算差异百分比（车型_指标→差异%） ******************************
    /**
     * 计算每个车型+指标的差异百分比，存到Map中（键：车型_指标，值：差异%）
     * @param excelData 原始Excel数据（List<List<String>>）
     * @return Map<String, Double> 差异百分比映射
     */
    private static Map<String, Double> calculateDiffMap(List<List<String>> excelData) {
        Map<String, Double> diffMap = new HashMap<>();
        String currentModel = ""; // 记录当前车型（处理合并单元格）

        // 跳过表头（假设第0行是表头）
        for (int rowIndex = 1; rowIndex < excelData.size(); rowIndex++) {
            List<String> row = excelData.get(rowIndex);
            if (row.isEmpty()) {
                continue;
            }

            // 1. 处理车型列（第0列）：合并单元格的情况
            String model = row.get(0);
            if (!model.isEmpty()) {
                currentModel = model; // 更新当前车型
            }
            // 跳过无效行（车型或指标为空）
            if (currentModel.isEmpty() || row.size() < 4) {
                continue; // 至少需要4列（车型、指标、预期、实际）
            }
            String indicator = row.get(1);
            if (indicator.isEmpty()) {
                continue;
            }

            // 2. 转换预期（第2列）和实际（第3列）为数字
            Double expected = convertToNumber(row.get(2));
            Double actual =convertToNumber(row.get(3));
            if (expected == null || actual == null) {
                continue;
            }

            // 3. 计算差异百分比（单量：(实际-预期)/预期×100）
            Double diffPercent = calculateDiffPercent(expected, actual);



            // 生成键（车型_指标）
            String key = currentModel + "_" + indicator;
            diffMap.put(key, diffPercent);
        }
        return diffMap;
    }

    /**
     * 将字符串转换为数字（支持百分比、逗号分隔）
     * @param value 单元格字符串值（如"154"、"-6.30%"、"1,5411"）
     * @return Double 转换后的数字，无效则返回null
     */
    private static Double convertToNumber(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // 去除百分比、逗号和空格
        String cleanValue = value.replace("%", "").replace(",", "").trim();
        try {
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            System.err.println("无效数值：" + value);
            return null;
        }
    }

    /**
     * 计算差异百分比（公式：(实际-预期)/预期×100）
     * @param expected 预期值
     * @param actual 实际值
     * @return Double 差异百分比，预期为0时特殊处理
     */
    private static Double calculateDiffPercent(Double expected, Double actual) {
        if(!isInteger(expected)&& !isInteger(actual)){
            return Math.abs(expected-actual);
        }
        if (expected == 0) {
            return actual == 0 ? 0.0 : 100.0; // 预期为0，实际非0→差异100%
        }
        return ((actual - expected) / expected) * 100;
    }
    public static boolean isInteger(double value) {
        return value == (long) value;
    }
    // ****************************** 步骤3：生成结果Excel（包含预期/实际数据着色） ******************************
    /**
     * 生成结果Excel，包含预期数据（未着色）和实际数据（差异着色）
     * @param excelData 原始Excel数据
     * @param diffMap 差异百分比映射
     * @param outputPath 结果文件路径
     */
    private static void generateResultExcel(List<List<String>> excelData, Map<String, Double> diffMap, String outputPath) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("差异着色结果");

            // 1. 初始化字体（统一样式，避免重复创建）
            XSSFFont greenFont = createFont(workbook, IndexedColors.GREEN); // <1% 绿色
            XSSFFont yellowFont = createFont(workbook, IndexedColors.ORANGE); // 1%-10% 黄色
            XSSFFont redFont = createFont(workbook, IndexedColors.RED); // >10% 红色
            XSSFFont defaultFont = createFont(workbook, IndexedColors.BLACK); // 默认黑色

            // 2. 表头行（第0行）
            XSSFRow headerRow = sheet.createRow(0);
            setHeaderCell(headerRow, 0, "预期数据（差异着色）", defaultFont, workbook);
            setHeaderCell(headerRow, 1, "实际数据（差异着色）", defaultFont, workbook);

            // 3. 数据行（第1行）
            XSSFRow dataRow = sheet.createRow(1);
            // 3.1 预期数据（直接拼接字符串）
            XSSFRichTextString expectRichText = buildColoredRichText(excelData, diffMap,2, workbook, greenFont, yellowFont, redFont, defaultFont);
            XSSFCell expectCell = dataRow.createCell(0);
            expectCell.setCellValue(expectRichText);
            setWrapCellStyle(expectCell, defaultFont, workbook);

            // 3.2 实际数据（差异着色，富文本）

            XSSFRichTextString actualRichText = buildColoredRichText(excelData, diffMap, 3,workbook, greenFont, yellowFont, redFont, defaultFont);

            XSSFCell actualCell = dataRow.createCell(1);
            expectCell.setCellValue(expectRichText);
            actualCell.setCellValue(actualRichText);
            setWrapCellStyle(actualCell, defaultFont, workbook);

            // 4. 调整列宽（自动适应内容）
            sheet.setColumnWidth(0, 40 * 256); // 预期数据列宽（40字符）
            sheet.setColumnWidth(1, 50 * 256); // 实际数据列宽（50字符）

            // 5. 保存文件
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }
        }
    }

    /**
     * 构建未着色的预期数据字符串（如"5米2：单量=154，调幅=-6.30%；6米2：单量=15411，调幅=-6.30%"）
     * @param excelData 原始Excel数据
     * @return String 预期数据字符串
     */

    /**
     * 构建差异着色的实际数据富文本（如"5米2：单量=152（黄色），调幅=-6.30%（红色）"）
     * @param excelData 原始Excel数据
     * @param diffMap 差异百分比映射
     * @param workbook 最终Workbook（用于创建字体）
     * @param greenFont 绿色字体
     * @param yellowFont 黄色字体
     * @param redFont 红色字体
     * @param defaultFont 默认字体
     * @return XSSFRichTextString 着色后的实际数据富文本
     */
    private static XSSFRichTextString buildColoredRichText(
            List<List<String>> excelData,
            Map<String, Double> diffMap,
            int valueColumnIndex, // 取值列索引（2=预期，3=实际）
            XSSFWorkbook workbook,
            XSSFFont greenFont,
            XSSFFont yellowFont,
            XSSFFont redFont,
            XSSFFont defaultFont
    ) {
        StringBuilder sb = new StringBuilder();
        String currentModel = ""; // 当前车型（处理合并单元格）
        int currentPos = 0; // 字符串当前位置（用于计算数字着色范围）
        List<ColorRange> colorRanges = new ArrayList<>(); // 记录数字范围及颜色

        // 跳过表头（第0行）
        for (int rowIndex = 1; rowIndex < excelData.size(); rowIndex++) {
            List<String> row = excelData.get(rowIndex);
            // 至少需要4列（车型、指标、预期、实际）
            if (row.size() < 4) {
                continue;
            }

            // 1. 处理车型列（第0列，合并单元格）
            String model = row.get(0).trim();
            if (!model.isEmpty()) {
                currentModel = model;
                // 换车型时添加换行符（分隔不同车型）
                if (sb.length() > 0) {
                    sb.append("\n\n");
                }
                sb.append(currentModel).append("：\n");
                currentPos = sb.length(); // 更新当前位置
            }

            // 2. 处理指标（第1列）和数值（指定列）
            String indicator = row.get(1).trim();
            String value = row.get(valueColumnIndex).trim(); // 取预期/实际值
            if (indicator.isEmpty() || value.isEmpty()) {
                continue;
            }

            // 3. 拼接文本（如"单量=154"）
            if(!isInteger(Double.parseDouble(value))){
                value = String.valueOf(Double.parseDouble(value)*100) + "%";
            }
            String part = indicator + ":" + value;
            sb.append(part);
            int partLength = part.length();

            // 4. 计算数字范围（指标=值中的数字部分，如"154"的起始/结束位置）
            // "指标="后的起始位置（如"单量=154"中的"154"起始于3）
            int numStart = currentPos + indicator.length() + 1;
            // 数字结束位置（如"单量=154"中的"154"结束于6）
            int numEnd = currentPos + partLength;

            // 5. 获取差异（车型_指标），默认0%（避免空指针）
            String key = currentModel + "_" + indicator;
            Double diffPercent = diffMap.getOrDefault(key, 0.0); // 差异默认0%
            IndexedColors color = getColorByDiff(diffPercent); // 根据差异获取颜色

            // 6. 记录颜色范围（用于后续着色）
            colorRanges.add(new ColorRange(numStart, numEnd, color));

            // 7. 更新当前位置（处理分隔符）
            currentPos += partLength;
            if (hasNextIndicator(rowIndex, excelData)) {
                sb.append("，");
                currentPos += 1; // 逗号占1位
            }
        }

        // 8. 构建富文本并应用颜色
        XSSFRichTextString richText = new XSSFRichTextString(sb.toString().trim());
        for (ColorRange range : colorRanges) {
            XSSFFont font = getFontByColor(range.color, greenFont, yellowFont, redFont, defaultFont);
            richText.applyFont(range.start, range.end, font);
        }

        return richText;
    }

    // ****************************** 辅助方法 ******************************
    /**
     * 设置表头单元格样式（居中、加粗）
     * @param row 表头行
     * @param colIndex 列索引
     * @param value 表头文本
     * @param font 字体
     * @param workbook Workbook对象
     */
    private static void setHeaderCell(XSSFRow row, int colIndex, String value, XSSFFont font, XSSFWorkbook workbook) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 实心填充
        cell.setCellStyle(style);
    }

    /**
     * 设置数据单元格样式（自动换行、左对齐）
     * @param cell 数据单元格
     * @param font 字体
     * @param workbook Workbook对象
     */
    private static void setWrapCellStyle(XSSFCell cell, XSSFFont font, XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setWrapText(true); // 自动换行
        style.setAlignment(HorizontalAlignment.LEFT); // 水平左对齐
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        cell.setCellStyle(style);
    }

    /**
     * 创建字体（统一样式：宋体，11）
     * @param workbook Workbook对象
     * @param color 字体颜色
     * @return XSSFFont 字体对象
     */
    private static XSSFFont createFont(XSSFWorkbook workbook, IndexedColors color) {
        XSSFFont font = workbook.createFont();
        // 显式指定字体名称（避免默认字体不一致）
        font.setFontName("宋体");
        // 统一字号为10号（标准大小）
        font.setFontHeightInPoints((short) 10);
        // 设置颜色
        font.setColor(color.getIndex());
        return font;
    }

    /**
     * 根据差异百分比获取颜色（绝对值判断）
     * @param diffPercent 差异百分比
     * @return IndexedColors 颜色枚举
     */
    private static IndexedColors getColorByDiff(Double diffPercent) {
        if (diffPercent == null) {
            return IndexedColors.BLACK; // 无效差异→黑色
        }
        double absDiff = Math.abs(diffPercent);
        if (absDiff < THRESHOLD_GREEN) {
            return IndexedColors.GREEN; // <1% 绿色
        }
        if (absDiff <= THRESHOLD_YELLOW) {
            return IndexedColors.ORANGE; // 1%-10% 黄色
        }
        return IndexedColors.RED; // >10% 红色
    }

    /**
     * 根据颜色获取对应的字体
     * @param color 颜色枚举
     * @param greenFont 绿色字体
     * @param yellowFont 黄色字体
     * @param redFont 红色字体
     * @param defaultFont 默认字体
     * @return XSSFFont 对应的字体
     */
    private static XSSFFont getFontByColor(IndexedColors color, XSSFFont greenFont, XSSFFont yellowFont, XSSFFont redFont, XSSFFont defaultFont) {
        switch (color) {
            case GREEN: return greenFont;
            case ORANGE: return yellowFont;
            case RED: return redFont;
            default: return defaultFont;
        }
    }

    /**
     * 判断当前行是否有下一个指标（避免末尾添加逗号）
     * @param currentRowIndex 当前行索引
     * @param excelData 原始Excel数据
     * @return boolean 是否有下一个指标
     */
    private static boolean hasNextIndicator(int currentRowIndex, List<List<String>> excelData) {
        if (currentRowIndex >= excelData.size() - 1) {
            return false; // 最后一行，无下一个指标
        }
        List<String> nextRow = excelData.get(currentRowIndex + 1);
        // 下一行的车型列（第0列）为空（属于当前车型）且指标列（第1列）不为空→有下一个指标
        return nextRow.size() >= 2 && nextRow.get(0).isEmpty() && !nextRow.get(1).isEmpty();
    }

    // ****************************** 内部类：记录颜色范围（数字位置+颜色） ******************************
    /**
     * 记录富文本中数字的位置及对应的颜色
     */
    private static class ColorRange {
        int start; // 数字起始索引（含）
        int end; // 数字结束索引（不含）
        IndexedColors color; // 颜色枚举

        public ColorRange(int start, int end, IndexedColors color) {
            this.start = start;
            this.end = end;
            this.color = color;
        }
    }
}