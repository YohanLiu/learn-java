package com.yohan.javabasic.java.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class ExcelMergeWithStyle {
    private static final String BASE_DIR = System.getProperty("user.dir") + "/learn-java-basic" +"/src/main" +
            "/resources/excel/merge/";

    public static void main(String[] args) throws IOException {
        // 要合并的 Excel 文件路径
        List<String> excelPaths = Arrays.asList(
                BASE_DIR + "2025-08-18-4476-大车原调涨回退.xlsx",
                BASE_DIR + "2025-08-18-4476-大车原调降回退.xlsx"
        );

        XSSFWorkbook mergedWorkbook = new XSSFWorkbook();
        XSSFSheet mergedSheet = mergedWorkbook.createSheet("Merged");

        int currentRowNum = 0;

        for (String path : excelPaths) {
            try (InputStream is = new FileInputStream(path);
                 XSSFWorkbook srcWorkbook = new XSSFWorkbook(is)) {

                XSSFSheet srcSheet = srcWorkbook.getSheetAt(0);

                // 复制列宽
                for (int col = 0; col < srcSheet.getRow(0).getLastCellNum(); col++) {
                    mergedSheet.setColumnWidth(col, srcSheet.getColumnWidth(col));
                }

                // 复制行
                for (int i = 0; i <= srcSheet.getLastRowNum(); i++) {
                    XSSFRow srcRow = srcSheet.getRow(i);
                    XSSFRow destRow = mergedSheet.createRow(currentRowNum++);

                    if (srcRow != null) {

                        for (int j = 0; j < srcRow.getLastCellNum(); j++) {
                            XSSFCell srcCell = srcRow.getCell(j);
                            XSSFCell destCell = destRow.createCell(j);

                            if (srcCell != null) {
                                copyCellValue(srcCell, destCell);
                                copyCellStyleWithFontColor(mergedWorkbook, srcCell, destCell);
                            }
                        }
                    }
                }

                // 复制合并单元格
                for (int i = 0; i < srcSheet.getNumMergedRegions(); i++) {
                    CellRangeAddress mergedRegion = srcSheet.getMergedRegion(i);
                    CellRangeAddress newRegion = new CellRangeAddress(
                            mergedRegion.getFirstRow() + (currentRowNum - srcSheet.getLastRowNum() - 1),
                            mergedRegion.getLastRow() + (currentRowNum - srcSheet.getLastRowNum() - 1),
                            mergedRegion.getFirstColumn(),
                            mergedRegion.getLastColumn()
                    );
                    mergedSheet.addMergedRegion(newRegion);
                }

                // 空行分隔
                currentRowNum++;
            }
        }

        try (OutputStream os = new FileOutputStream(BASE_DIR + "merged_exact_style.xlsx")) {
            mergedWorkbook.write(os);
        }
        mergedWorkbook.close();
        System.out.println("合并完成，样式已保留");
    }

    private static void copyCellValue(Cell srcCell, Cell destCell) {
        switch (srcCell.getCellType()) {
            case STRING:
                // 检查是否为富文本
                if (srcCell instanceof XSSFCell) {
                    XSSFCell xssfSrcCell = (XSSFCell) srcCell;
                    if (xssfSrcCell.getRichStringCellValue() != null) {
                        destCell.setCellValue(xssfSrcCell.getRichStringCellValue());
                        return;
                    }
                }
                destCell.setCellValue(srcCell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(srcCell)) {
                    destCell.setCellValue(srcCell.getDateCellValue());
                } else {
                    destCell.setCellValue(srcCell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                destCell.setCellValue(srcCell.getBooleanCellValue());
                break;
            case FORMULA:
                destCell.setCellFormula(srcCell.getCellFormula());
                break;
            case BLANK:
                destCell.setBlank();
                break;
            default:
                destCell.setCellValue(srcCell.toString());
        }
    }

    private static void copyCellStyleWithFontColor(XSSFWorkbook destWb,
                                                   XSSFCell srcCell, XSSFCell destCell) {
        XSSFCellStyle srcStyle = srcCell.getCellStyle();
        XSSFCellStyle newStyle = destWb.createCellStyle();
        newStyle.cloneStyleFrom(srcStyle);

        destCell.setCellStyle(newStyle);
    }
}