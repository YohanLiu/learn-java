package com.yohan.javabasic.java.excel;


import cn.idev.excel.EasyExcel;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.ContentStyle;
import cn.idev.excel.enums.BooleanEnum;
import cn.idev.excel.write.handler.CellWriteHandler;
import cn.idev.excel.write.handler.context.CellWriteHandlerContext;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import cn.idev.excel.write.style.HorizontalCellStyleStrategy;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelDiffColorizerFastExcel {

    // 颜色阈值（保持不变）
    private static final double THRESHOLD_GREEN = 0.01;
    private static final double THRESHOLD_YELLOW = 0.1;

    private static final int COLUMN_INDEX_EXPECTED = 2;
    private static final int COLUMN_INDEX_ACTUAL = 4;
    private static final int TOTAL_COLUMN = 5;

    private static final String BASE_DIR = System.getProperty("user.dir") + "/learn-java-basic" +"/src/main" +
            "/resources/";

    public static void main(String[] args) throws IOException {
        String inputPath = BASE_DIR + "最终预期模版样式.xlsx";
        String outputPath = BASE_DIR + "fast excel 着色后的结果.xlsx";

        // 1. 读取Excel数据
        List<List<String>> excelData = readExcelToList(inputPath);
        if (excelData.isEmpty()) {
            System.err.println("未读取到有效数据！");
            return;
        }

        // 2. 计算差异
        Map<String, Double> diffMap = calculateDiffMap(excelData);
        if (diffMap.isEmpty()) {
            System.err.println("未计算到有效差异！");
            return;
        }

        // 3. 生成EasyExcel数据
        List<ExcelDiffData> dataList = prepareEasyExcelData(excelData, diffMap);

        // 4. EasyExcel写入到字节数组并且生成Excel文件
        genExcelByByteArray(dataList, outputPath);

        // 4. EasyExcel直接生成 excel
        //genExcel(outputPath, dataList);
    }

    private static void genExcel(String outputPath, List<ExcelDiffData> dataList) {
        EasyExcel.write(outputPath, ExcelDiffData.class)
                .inMemory(true)
                .registerWriteHandler(new RichTextCellWriteHandler(dataList))
                .registerWriteHandler(new StyleHandler())
                .sheet("差异着色结果")
                .doWrite(dataList);

        System.out.println("EasyExcel结果生成成功：" + outputPath);
    }

    private static void genExcelByByteArray(List<ExcelDiffData> dataList, String outputPath) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, ExcelDiffData.class)
                .inMemory(true)
                .registerWriteHandler(new RichTextCellWriteHandler(dataList))
                .registerWriteHandler(new StyleHandler())
                .sheet("差异着色结果")
                .doWrite(dataList);

        // 获取字节数组
        byte[] excelBytes = outputStream.toByteArray();
        System.out.println("Excel已转换为字节数组，大小为: " + excelBytes.length + " 字节");

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath)) {
            fileOutputStream.write(excelBytes);
        }
        System.out.println("EasyExcel结果生成成功：" + outputPath);
    }

    // 定义EasyExcel数据模型

    @Data
    @ContentStyle(
            wrapped = BooleanEnum.TRUE)
    public static class ExcelDiffData {
        @ExcelProperty("运营输入标准")
        @ColumnWidth(80)
        private String expected;

        @ExcelProperty("技术核验结果")
        @ColumnWidth(80)
        private String actual;

        // 存储着色范围信息
        @ExcelIgnore
        private List<ColorRange> expectedColorRanges = new ArrayList<>();

        @ExcelIgnore
        private List<ColorRange> actualColorRanges = new ArrayList<>();
    }

    public static class RichTextCellWriteHandler implements CellWriteHandler {
        private final List<ExcelDiffData> dataList;

        public RichTextCellWriteHandler(List<ExcelDiffData> dataList) {
            this.dataList = dataList;
        }

        @Override
        public void afterCellDispose(CellWriteHandlerContext context) {
            if (Boolean.TRUE.equals(context.getHead())) {
                return;
            }
            int col = context.getColumnIndex();
            int rowIdx = context.getRelativeRowIndex();
            ExcelDiffData data = dataList.get(rowIdx);
            Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
            Cell cell = context.getCell();
            if (col == 0) { // 预期列
                XSSFRichTextString rich = new XSSFRichTextString(data.getExpected());
                for (ColorRange range : data.getExpectedColorRanges()) {
                    Font font = workbook.createFont();
                    font.setFontName("宋体");
                    font.setFontHeightInPoints((short)10);
                    // 支持自定义RGB颜色
                    if (range.hasCustomColor() && font instanceof XSSFFont) {
                        ((XSSFFont) font).setColor(range.getCustomColor());
                    } else {
                        font.setColor(range.getColor().getIndex());
                    }
                    rich.applyFont(range.getStart(), range.getEnd(), font);
                }

                cell.setCellValue(rich);
            } else if (col == 1) { // 实际列
                XSSFRichTextString rich = new XSSFRichTextString(data.getActual());
                for (ColorRange range : data.getActualColorRanges()) {
                    Font font = workbook.createFont();
                    font.setFontName("宋体");
                    font.setFontHeightInPoints((short)10);
                    // 支持自定义RGB颜色
                    if (range.hasCustomColor() && font instanceof XSSFFont) {
                        ((XSSFFont) font).setColor(range.getCustomColor());
                    } else {
                        font.setColor(range.getColor().getIndex());
                    }
                    rich.applyFont(range.getStart(), range.getEnd(), font);
                }
                cell.setCellValue(rich);
            }
        }
    }


    // 样式处理器（设置自动换行/居中等）
    public static class StyleHandler extends HorizontalCellStyleStrategy {
        public StyleHandler() {
            super(headStyle(), contentStyle());
        }

        private static WriteCellStyle headStyle() {
            WriteCellStyle style = new WriteCellStyle();
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setHorizontalAlignment(HorizontalAlignment.CENTER);

            WriteFont font = new WriteFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 12);
            style.setWriteFont(font);
            return style;
        }

        private static WriteCellStyle contentStyle() {
            WriteCellStyle style = new WriteCellStyle();
            style.setVerticalAlignment(VerticalAlignment.TOP);
            style.setHorizontalAlignment(HorizontalAlignment.LEFT);
            style.setWrapped(true);  // 自动换行

            // 添加边框线设置
            style.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            style.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            style.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            style.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            
            // 设置边框颜色
            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

            WriteFont font = new WriteFont();
            font.setFontHeightInPoints((short) 10);
            style.setWriteFont(font);
            return style;
        }
    }

    // 准备EasyExcel数据（核心逻辑）
    private static List<ExcelDiffData> prepareEasyExcelData(
            List<List<String>> excelData,
            Map<String, Double> diffMap
    ) {
        List<ExcelDiffData> dataList = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {

            int end = i + 1;

            if (i == 3) {
                end = excelData.size();
            }

            ExcelDiffData result = new ExcelDiffData();
            // 构建实际数据（带着色信息）
            ActualDataResult expectedResult =  buildActualData(excelData, diffMap, COLUMN_INDEX_EXPECTED, i, end);
            result.setExpected(expectedResult.getText());
            result.setExpectedColorRanges(expectedResult.getColorRanges());

            // 构建实际数据（带着色信息）
            ActualDataResult actualResult = buildActualData(excelData, diffMap, COLUMN_INDEX_ACTUAL, i, end);
            result.setActual(actualResult.getText());
            result.setActualColorRanges(actualResult.getColorRanges());
            dataList.add(result);
        }

        return dataList;
    }

    // 构建实际数据文本和着色信息
    private static ActualDataResult buildActualData(
            List<List<String>> excelData,
            Map<String, Double> diffMap,
            int valueColumnIndex, int start, int end
    ) {
        StringBuilder sb = new StringBuilder();
        String vehicleName = "";
        int currentPos = 0;
        List<ColorRange> colorRanges = new ArrayList<>();

        int i = 1;

        for (int rowIndex = start; rowIndex < end; rowIndex++) {
            List<String> row = excelData.get(rowIndex);
            if (row.size() < TOTAL_COLUMN) {
                continue;
            }

            // 1. 处理车型
            String model = row.get(0).trim();
            if (!model.isEmpty()) {
                vehicleName = model;
                if (!sb.isEmpty()) {
                    sb.append("\n\n");
                }
                sb.append(i++).append("、").append(vehicleName).append("：\n");
                currentPos = sb.length();
            }

            // 2. 处理指标
            int metricsIndex = valueColumnIndex - 1;
            String indicator = row.get(metricsIndex).trim();
            String value = row.get(valueColumnIndex).trim();
            if (indicator.isEmpty() || value.isEmpty()) continue;

            // 3. 格式化数值
            if (!isInteger(Double.parseDouble(value))) {
                value = dealNumber(new BigDecimal(value).multiply(BigDecimal.valueOf(100))) + "%";
            } else {
                value = dealNumber(new BigDecimal(value));
            }

            // 4. 拼接文本
            String part = indicator + "：" + value;

            sb.append(part);


            int partLength = part.length();

            // 5. 记录着色范围
            int numStart = currentPos + indicator.length() + 1;
            int numEnd = currentPos + partLength;

            String key = vehicleName + "_" + indicator;
            Double diffPercent = diffMap.getOrDefault(key, 0.0);
            
            // 使用自定义RGB颜色而不是IndexedColors
            XSSFColor customColor = getCustomColorByDiff(diffPercent);
            colorRanges.add(new ColorRange(numStart, numEnd, customColor));

            // 6. 更新位置
            currentPos += partLength;
            if (!vehicleName.isEmpty() && rowIndex + 1 < end && hasNextIndicator(rowIndex, excelData, metricsIndex, valueColumnIndex)) {
                sb.append("，");
                currentPos += 1;
            }

            //  7. 换行的处理
            if (vehicleName.isEmpty()) {
                sb.append("\n");
                currentPos += 1;
            }
        }

        return new ActualDataResult(sb.toString().trim(), colorRanges);
    }

    private static String dealNumber(BigDecimal number) {
        number = number.stripTrailingZeros();

        // 格式化为带千位分隔符的字符串
        DecimalFormat df = new DecimalFormat("#,###.##");
        return df.format(number);
    }

    // 着色范围内部类
    @Data
    private static class ColorRange {
        private final int start;
        private final int end;
        private final IndexedColors color;
        // 添加自定义RGB颜色字段
        private final XSSFColor customColor;

        // 原来的构造函数保持不变（为了兼容性）
        public ColorRange(int start, int end, IndexedColors color) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.customColor = null;
        }
        
        // 新增支持自定义RGB颜色的构造函数
        public ColorRange(int start, int end, XSSFColor customColor) {
            this.start = start;
            this.end = end;
            this.color = null;
            this.customColor = customColor;
        }
        
        // 判断是否使用自定义颜色
        public boolean hasCustomColor() {
            return customColor != null;
        }
    }

    // 实际数据结果封装
    @Data
    private static class ActualDataResult {
        private final String text;
        private final List<ColorRange> colorRanges;

        public ActualDataResult(String text, List<ColorRange> colorRanges) {
            this.text = text;
            this.colorRanges = colorRanges;
        }
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
        String vehicleName = ""; // 记录当前车型（处理合并单元格）

        // 跳过表头（假设第0行是表头）
        for (int rowIndex = 1; rowIndex < excelData.size(); rowIndex++) {
            List<String> row = excelData.get(rowIndex);
            if (row.isEmpty()) {
                continue;
            }

            // 1. 处理车型列（第0列）：合并单元格的情况
            String model = row.get(0);
            if (!model.isEmpty()) {
                vehicleName = model; // 更新当前车型
            }
            // 跳过无效行（车型或指标为空）
            if (row.size() < TOTAL_COLUMN) {
                continue; // 至少需要4列（车型、指标、预期、实际）
            }
            String indicator = row.get(1);
            if (indicator.isEmpty()) {
                continue;
            }

            // 2. 转换预期（第2列）和实际（第4列）为数字
            Double expected = convertToNumber(row.get(COLUMN_INDEX_EXPECTED));
            Double actual =convertToNumber(row.get(COLUMN_INDEX_ACTUAL));
            if (expected == null || actual == null) {
                continue;
            }

            // 3. 计算差异百分比（单量：(实际-预期)/预期×100）
            Double diffPercent = calculateDiffPercent(expected, actual);

            // 生成键（车型_指标）
            diffMap.put(vehicleName + "_" + indicator, diffPercent);
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
    /**
     * 判断当前行是否有下一个指标（避免末尾添加逗号）
     * @param currentRowIndex 当前行索引
     * @param excelData 原始Excel数据
     * @return boolean 是否有下一个指标
     */
    private static boolean hasNextIndicator(int currentRowIndex, List<List<String>> excelData, int metricsIndex, int valueColumnIndex) {
        if (currentRowIndex >= excelData.size() - 1) {
            return false; // 最后一行，无下一个指标
        }
        List<String> nextRow = excelData.get(currentRowIndex + 1);
        // 下一行的车型列（第0列）为空（属于当前车型）且指标列（第1列）不为空→有下一个指标并且指标数值不为空
        return nextRow.size() >= TOTAL_COLUMN && nextRow.get(0).isEmpty() && !nextRow.get(metricsIndex).isEmpty() && !nextRow.get(valueColumnIndex).isEmpty();
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
            return IndexedColors.LIGHT_ORANGE; // 1%-10% 黄色
        }
        return IndexedColors.RED; // >10% 红色
    }

    /**
     * 根据差异百分比获取自定义RGB颜色
     * @param diffPercent 差异百分比
     * @return XSSFColor 自定义RGB颜色
     */
    private static XSSFColor getCustomColorByDiff(Double diffPercent) {
        if (diffPercent == null) {
            // 返回黑色 (R=0, G=0, B=0)
            return new XSSFColor(new byte[]{(byte) 0, (byte) 0, (byte) 0}, null);
        }
        double absDiff = Math.abs(diffPercent);
        if (absDiff < THRESHOLD_GREEN) {
            // 返回绿色 (R=0, G=180, B=0) 表示差异很小
            return new XSSFColor(new byte[]{(byte) 0, (byte) 180, (byte) 0}, null);
        }
        if (absDiff <= THRESHOLD_YELLOW) {
            // 返回深黄色 (R=255, G=165, B=0) 表示中等差异
            return new XSSFColor(new byte[]{(byte) 209, (byte) 139, (byte) 12}, null);
        }
        // 返回红色 (R=255, G=50, B=50) 表示差异很大
        return new XSSFColor(new byte[]{(byte) 255, (byte) 50, (byte) 50}, null);
    }

}