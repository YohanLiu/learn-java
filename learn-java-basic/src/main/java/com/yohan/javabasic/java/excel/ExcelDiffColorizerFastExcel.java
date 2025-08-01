package com.yohan.javabasic.java.excel;


import cn.idev.excel.EasyExcel;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.ContentStyle;
import cn.idev.excel.enums.BooleanEnum;
import cn.idev.excel.write.handler.CellWriteHandler;
import cn.idev.excel.write.handler.context.CellWriteHandlerContext;
import cn.idev.excel.write.merge.OnceAbsoluteMergeStrategy;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import cn.idev.excel.write.style.HorizontalCellStyleStrategy;
import com.google.common.math.DoubleMath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ExcelDiffColorizerFastExcel {

    // 颜色阈值（保持不变）
    private static final double THRESHOLD_GREEN = 0.01;
    private static final double THRESHOLD_YELLOW = 0.1;

    /**
     * 差异颜色枚举
     */
    @AllArgsConstructor
    @Getter
    public enum DiffColorEnum {
        /**
         * 黑色 - 用于空值或无效值
         */
        BLACK(new XSSFColor(new byte[]{(byte) 0, (byte) 0, (byte) 0}, null)),
        
        /**
         * 绿色 - 差异很小
         */
        GREEN(new XSSFColor(new byte[]{(byte) 0, (byte) 180, (byte) 0}, null)),
        
        /**
         * 深黄色 - 中等差异
         */
        DARK_YELLOW(new XSSFColor(new byte[]{(byte) 209, (byte) 139, (byte) 12}, null)),
        
        /**
         * 红色 - 差异很大
         */
        RED(new XSSFColor(new byte[]{(byte) 255, (byte) 50, (byte) 50}, null));

        private XSSFColor color;
    }

    private static final int COLUMN_INDEX_EXPECTED = 2;
    private static final int COLUMN_INDEX_ACTUAL = 4;
    private static final int TOTAL_COLUMN = 5;

    private static final String BASE_DIR = System.getProperty("user.dir") + "/learn-java-basic" +"/src/main" +
            "/resources/excel/";

    public static final Map<Integer, String> itemMap = Map.of(1, "有没有调", 2, "方向--有没有调对", 3, "幅度--有没有调对");
    public static final Map<Integer, String> remarkMap = Map.of(1, "测算覆盖变化 OD 数量(去重)：6,141\n测算覆盖变化 OD 数量(去重)/实际生效OD数：6,141/0=null%", 2, "产生少量降幅 占比:0.05%\n- 原因：调价前命中起步价兜底，老起步价兜底换标为真实起步价兜底，起步价兜底弹回普通里程\n- 分布：\n调降：7单\n平均降幅:-8.43%\n最大降幅:-18.09%\n最小降幅:-5.51%", 3, "由于统计周期导致单量偏差\n最大降幅：-27.273%\n最大降幅不一致原因：调价前命中起步价兜底，调价后命中真实起步价兜底，老起步价兜底换标为真实起步价兜底\n最小降幅：-0.015%\n最小降幅不一致原因：调价后命中单公里兜底\n\n降幅超过-20%, 数量:1, 占比:0.01%\n\n不一致率：572/14,405 = 3.97%\n-该调的没调（单量）：42\n-该调的没调（占比）：0.294%\n-降幅过大（单量）：9\n-降幅过大（占比）：0.065%\n-降幅过小（单量）：522\n-降幅过小（占比）：3.615%\n\n不一致原因：起步价兜底、单公里兜底\n");

    public static void main(String[] args) throws IOException {
        String inputPath = BASE_DIR + "最终预期模版样式.xlsx";
        String outputPath = BASE_DIR + "fast excel 着色后的结果.xlsx";

        // 1. 读取Excel数据
        List<List<String>> excelData = readExcelToList(inputPath);
        if (excelData.isEmpty()) {
            log.error("未读取到有效数据！");
            return;
        }

        // 2. 计算差异
        Map<String, Double> diffMap = calculateDiffMap(excelData);
        if (diffMap.isEmpty()) {
            log.error("未计算到有效差异！");
            return;
        }

        // 3. 生成EasyExcel数据
        List<VerifyConclusionExcelBO> dataList = prepareEasyExcelData(excelData, diffMap);

        // 4. EasyExcel写入到字节数组并且生成Excel文件
        genExcelByByteArray(dataList, outputPath);

        // 4. EasyExcel直接生成 excel
        //genExcel(outputPath, dataList);
    }

    private static void genExcel(String outputPath, List<VerifyConclusionExcelBO> dataList) {
        EasyExcel.write(outputPath, VerifyConclusionExcelBO.class)
                .inMemory(true)
                .registerWriteHandler(new RichTextCellWriteHandler(dataList))
                .registerWriteHandler(new StyleHandler())
                .registerWriteHandler(new OnceAbsoluteMergeStrategy(1, 3, 0, 0)) // 合并第一列（索引为0）
                .sheet("差异着色结果")
                .doWrite(dataList);

        System.out.println("EasyExcel结果生成成功：" + outputPath);
    }

    private static void genExcelByByteArray(List<VerifyConclusionExcelBO> dataList, String outputPath) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, VerifyConclusionExcelBO.class)
                .inMemory(true)
                .registerWriteHandler(new RichTextCellWriteHandler(dataList))
                .registerWriteHandler(new StyleHandler())
                .registerWriteHandler(new OnceAbsoluteMergeStrategy(1, 3, 0, 0)) // 合并第一列（索引为0）
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
    public static class VerifyConclusionExcelBO {
        @ExcelProperty("核验动作")
        @ColumnWidth(15)
        private String action = "核验动作";

        @ExcelProperty("核验项目")
        @ColumnWidth(20)
        private String item;

        @ExcelProperty("核验结果")
        @ColumnWidth(12)
        private String result = "√";

        @ExcelProperty("运营输入标准")
        @ColumnWidth(70)
        private String expected;

        @ExcelProperty("技术核验结果")
        @ColumnWidth(50)
        private String actual;

        @ExcelProperty("备注")
        @ColumnWidth(60)
        private String remark = "备注";

        // 存储着色范围信息
        @ExcelIgnore
        private List<ColorRangeBO> expectedColorRangeBOS = new ArrayList<>();

        @ExcelIgnore
        private List<ColorRangeBO> actualColorRangeBOS = new ArrayList<>();

        @ExcelIgnore
        private ColorRangeBO resultColorRangesBO;

        @ExcelIgnore
        private ColorRangeBO remarkColorRangesBO;
    }

    public static ColorRangeBO checkRemarkColor(String content, Pattern pattern, double overRatio, int group) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            int startIndex = matcher.start();
            int endIndex = matcher.end();

            // 提取百分比数字并解析
            String percentStr = matcher.group(group); // 这里是百分比的 group
            double percent = Double.parseDouble(percentStr);
            boolean isOverPercent = percent > overRatio;

            if (isOverPercent) {
                return new ColorRangeBO(startIndex, endIndex, DiffColorEnum.RED.getColor());
            } else {
                return new ColorRangeBO(startIndex, endIndex, DiffColorEnum.DARK_YELLOW.getColor());
            }


        } else {
            return new ColorRangeBO(0, content.length(), DiffColorEnum.BLACK.getColor()); // 未找到匹配
        }
    }

    public static class RichTextCellWriteHandler implements CellWriteHandler {
        private final List<VerifyConclusionExcelBO> dataList;

        public RichTextCellWriteHandler(List<VerifyConclusionExcelBO> dataList) {
            this.dataList = dataList;
        }

        @Override
        public void afterCellDispose(CellWriteHandlerContext context) {
            if (Boolean.TRUE.equals(context.getHead())) {
                return;
            }
            int col = context.getColumnIndex();
            int rowIdx = context.getRelativeRowIndex();
            VerifyConclusionExcelBO data = dataList.get(rowIdx);
            Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
            Cell cell = context.getCell();
            
            if (col == 0) {
                XSSFRichTextString rich = new XSSFRichTextString(data.getAction());
                Font font = workbook.createFont();
                font.setFontName("宋体");
                font.setFontHeightInPoints((short)14);
                font.setBold(true);
                rich.applyFont(font);
                cell.setCellValue(rich);
            }
            else if (col == 1) {
                XSSFRichTextString rich = new XSSFRichTextString(data.getItem());
                Font font = workbook.createFont();
                font.setFontName("宋体");
                font.setFontHeightInPoints((short)12);
                font.setBold(true);
                rich.applyFont(font);
                cell.setCellValue(rich);
            }
            else if (col == 2) {
                XSSFRichTextString rich = new XSSFRichTextString(data.getResult());
                ColorRangeBO resultColorRangesBO = data.getResultColorRangesBO();
                Font font = workbook.createFont();
                font.setFontName("宋体");
                font.setFontHeightInPoints((short)18);
                font.setBold(true);
                // 支持自定义RGB颜色
                ((XSSFFont) font).setColor(resultColorRangesBO.getCustomColor());
                rich.applyFont(resultColorRangesBO.getStart(), resultColorRangesBO.getEnd(), font);

                cell.setCellValue(rich);
            } else if (col == 3) { // 预期列
                XSSFRichTextString rich = new XSSFRichTextString(data.getExpected());
                for (ColorRangeBO range : data.getExpectedColorRangeBOS()) {
                    Font font = workbook.createFont();
                    font.setFontName("宋体");
                    font.setFontHeightInPoints((short)10);
                    // 支持自定义RGB颜色
                    ((XSSFFont) font).setColor(range.getCustomColor());
                    rich.applyFont(range.getStart(), range.getEnd(), font);
                }

                cell.setCellValue(rich);
            } else if (col == 4) { // 实际列
                XSSFRichTextString rich = new XSSFRichTextString(data.getActual());
                for (ColorRangeBO range : data.getActualColorRangeBOS()) {
                    Font font = workbook.createFont();
                    font.setFontName("宋体");
                    font.setFontHeightInPoints((short)10);
                    // 支持自定义RGB颜色
                    ((XSSFFont) font).setColor(range.getCustomColor());
                    rich.applyFont(range.getStart(), range.getEnd(), font);
                }
                cell.setCellValue(rich);
            } else if (col == 5) {
                XSSFRichTextString rich = new XSSFRichTextString(data.getRemark());
                ColorRangeBO remarkColorRangesBO = data.getRemarkColorRangesBO();
                Font font = workbook.createFont();
                font.setFontName("宋体");
                font.setFontHeightInPoints((short)10);
                // 支持自定义RGB颜色
                ((XSSFFont) font).setColor(remarkColorRangesBO.getCustomColor());
                rich.applyFont(remarkColorRangesBO.getStart(), remarkColorRangesBO.getEnd(), font);
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

        @Override
        protected void setContentCellStyle(CellWriteHandlerContext context) {
            // 调用父类方法设置基本样式
            super.setContentCellStyle(context);

            // 获取当前列索引
            int colIndex = context.getColumnIndex();

            // 设置特定列的对齐方式
            if (colIndex >= 0 && colIndex <= 2) {
                // 0,1,2列居中对齐
                context.getFirstCellData().getWriteCellStyle().setVerticalAlignment(VerticalAlignment.CENTER);
                context.getFirstCellData().getWriteCellStyle().setHorizontalAlignment(HorizontalAlignment.CENTER);
            } else if (colIndex >= 3 && colIndex <= 5) {
                // 3,4,5列左上对齐
                context.getFirstCellData().getWriteCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
                context.getFirstCellData().getWriteCellStyle().setHorizontalAlignment(HorizontalAlignment.LEFT);
            }
        }
    }

    // 准备EasyExcel数据（核心逻辑）
    private static List<VerifyConclusionExcelBO> prepareEasyExcelData(
            List<List<String>> excelData,
            Map<String, Double> diffMap
    ) {
        List<VerifyConclusionExcelBO> dataList = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {

            int end = i + 1;

            if (i == 3) {
                end = excelData.size();
            }

            VerifyConclusionExcelBO result = new VerifyConclusionExcelBO();
            // 构建实际数据（带着色信息）
            ExcelDataResultBO expectedResult =  buildActualData(excelData, diffMap, COLUMN_INDEX_EXPECTED, i, end);
            result.setExpected(expectedResult.getText());
            result.setExpectedColorRangeBOS(expectedResult.getColorRangeBOS());

            // 构建实际数据（带着色信息）
            ExcelDataResultBO actualResult = buildActualData(excelData, diffMap, COLUMN_INDEX_ACTUAL, i, end);
            result.setActual(actualResult.getText());
            List<ColorRangeBO> actualColorRangeBOS = actualResult.getColorRangeBOS();
            result.setActualColorRangeBOS(actualColorRangeBOS);

            result.setItem(itemMap.get(i));

            // 根据colorRanges集合设置resultColorRanges的颜色，优先级为红色、深黄色、绿色
            XSSFColor resultColor = DiffColorEnum.GREEN.getColor();
            for (ColorRangeBO actualColorRangeBO : actualColorRangeBOS) {
                if (actualColorRangeBO.getCustomColor() == DiffColorEnum.RED.getColor()) {
                    resultColor = DiffColorEnum.RED.getColor();
                    break;
                }

                if (actualColorRangeBO.getCustomColor() == DiffColorEnum.DARK_YELLOW.getColor()) {
                    resultColor = DiffColorEnum.DARK_YELLOW.getColor();
                }
            }

            String remark = remarkMap.get(i);
            result.setRemark(remark);
            if (i == 1) {
                result.setRemarkColorRangesBO(new ColorRangeBO(0, remark.length(), DiffColorEnum.BLACK.getColor()));
            } else if (i == 2) {
                ColorRangeBO remarkColor = checkRemarkColor(remark, Pattern.compile("占比:(\\d+(\\.\\d+)?)%"), 10.0, 1);
                result.setRemarkColorRangesBO(new ColorRangeBO(0, remark.length(), remarkColor.getCustomColor()));

            } else if (i == 3) {
                ColorRangeBO remarkColor = checkRemarkColor(remark, Pattern.compile("不一致率：\\d{1,3}(,?\\d{3})*/\\d{1," +
                        "3}" +
                        "(," +
                        "?\\d{3})* =" +
                        " " +
                        "(\\d+\\.\\d+)" +
                        "%"), 10.0, 3);
                result.setRemarkColorRangesBO(remarkColor);
            }

            if (result.getRemarkColorRangesBO().getCustomColor() == DiffColorEnum.RED.getColor()) {
                resultColor = DiffColorEnum.RED.getColor();
            }

            if (result.getRemarkColorRangesBO().getCustomColor() == DiffColorEnum.DARK_YELLOW.getColor()) {
                resultColor = DiffColorEnum.DARK_YELLOW.getColor();
            }

            result.setResultColorRangesBO(new ColorRangeBO(0, result.getResult().length(), resultColor));

            dataList.add(result);
        }

        return dataList;
    }

    // 构建实际数据文本和着色信息
    private static ExcelDataResultBO buildActualData(
            List<List<String>> excelData,
            Map<String, Double> diffMap,
            int valueColumnIndex, int start, int end
    ) {
        StringBuilder sb = new StringBuilder();
        String vehicleName = "";
        int currentPos = 0;
        List<ColorRangeBO> colorRangeBOS = new ArrayList<>();

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
            if (!DoubleMath.isMathematicalInteger(Double.parseDouble(value))) {
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
            Double diffPercent = diffMap.getOrDefault(key, null);
            
            // 使用自定义RGB颜色而不是IndexedColors
            XSSFColor customColor = getCustomColorByDiff(diffPercent);
            colorRangeBOS.add(new ColorRangeBO(numStart, numEnd, customColor));

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

        return new ExcelDataResultBO(sb.toString().trim(), colorRangeBOS);
    }

    private static String dealNumber(BigDecimal number) {
        number = number.stripTrailingZeros();

        // 格式化为带千位分隔符的字符串
        DecimalFormat df = new DecimalFormat("#,###.##");
        return df.format(number);
    }

    // 着色范围内部类
    @Data
    private static class ColorRangeBO {
        private final int start;
        private final int end;
        // 添加自定义RGB颜色字段
        private final XSSFColor customColor;
        
        // 新增支持自定义RGB颜色的构造函数
        public ColorRangeBO(int start, int end, XSSFColor customColor) {
            this.start = start;
            this.end = end;
            this.customColor = customColor;
        }
    }

    // 实际数据结果封装
    @Data
    private static class ExcelDataResultBO {
        private final String text;
        private final List<ColorRangeBO> colorRangeBOS;

        public ExcelDataResultBO(String text, List<ColorRangeBO> colorRangeBOS) {
            this.text = text;
            this.colorRangeBOS = colorRangeBOS;
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
            String indicator1 = row.get(3);
            if (indicator.isEmpty() || indicator1.isEmpty()) {
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
            diffMap.put(vehicleName + "_" + indicator1, diffPercent);
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
            log.error("无效数值：" + value);
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
        if(!DoubleMath.isMathematicalInteger(expected)&& !DoubleMath.isMathematicalInteger(actual)){
            return Math.abs(expected-actual);
        }
        if (expected == 0) {
            return actual == 0 ? 0.0 : 100.0; // 预期为0，实际非0→差异100%
        }
        return ((actual - expected) / expected) * 100;
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
     * 根据差异百分比获取自定义RGB颜色
     * @param diffPercent 差异百分比
     * @return XSSFColor 自定义RGB颜色
     */
    private static XSSFColor getCustomColorByDiff(Double diffPercent) {
        if (diffPercent == null) {
            // 返回黑色 (R=0, G=0, B=0)
            return DiffColorEnum.BLACK.getColor();
        }
        double absDiff = Math.abs(diffPercent);
        if (absDiff < THRESHOLD_GREEN) {
            // 返回绿色 (R=0, G=180, B=0) 表示差异很小
            return DiffColorEnum.GREEN.getColor();
        }
        if (absDiff <= THRESHOLD_YELLOW) {
            // 返回深黄色 (R=255, G=165, B=0) 表示中等差异
            return DiffColorEnum.DARK_YELLOW.getColor();
        }
        // 返回红色 (R=255, G=50, B=50) 表示差异很大
        return DiffColorEnum.RED.getColor();
    }

}