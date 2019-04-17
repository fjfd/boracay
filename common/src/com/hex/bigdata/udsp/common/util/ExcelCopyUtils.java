/**
 * Copyright @ 2010-2015 Hex company All rights reserved
 * 系统名称：DCP
 * 模块名称：
 *
 * @version版本信息：V1.0
 * @author:jiewang
 */
package com.hex.bigdata.udsp.common.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现excel的sheet复制，复制的内容包括单元的内容、样式、注释
 *
 * @author Junjie.M
 */
public class ExcelCopyUtils {
    private static final String TEMPLATE_FILE_PATH = "goframe/udsp/templates";

    public static String templatePath;

    static {
        try {
            templatePath = ResourceUtils.getFile("classpath:" + TEMPLATE_FILE_PATH).getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝Sheet，并拷贝样式
     *
     * @param targetSheet
     * @param sourceSheet
     * @throws Exception
     */
    public static void copySheet(HSSFSheet targetSheet, HSSFSheet sourceSheet, HSSFWorkbook targetWork) throws Exception {
        //参数是否为 null
        if (targetSheet == null || sourceSheet == null) {
            throw new Exception(
                    "targetSheet、sourceSheet都不能为空");
        }
        copySheet(targetSheet, sourceSheet, true, targetWork);
    }

    /**
     * 拷贝Sheet，并拷贝样式
     *
     * @param sheetName
     * @param sourceSheet
     * @param targetWork
     * @param sourceWork
     * @throws Exception
     */
    public static HSSFSheet copySheet(String sheetName, HSSFSheet sourceSheet,
                                      HSSFWorkbook targetWork, HSSFWorkbook sourceWork) throws Exception {
        HSSFSheet targetSheet = targetWork.createSheet(sheetName);
        // 拷贝Sheet模板
        ExcelCopyUtils.copySheet(sheetName, sourceSheet, targetWork,
                sourceWork);
        return targetSheet;
    }

    /**
     * 拷贝Sheet
     *
     * @param targetSheet
     * @param sourceSheet
     * @param copyStyle   是否拷贝样式
     * @throws Exception
     */
    public static void copySheet(HSSFSheet targetSheet, HSSFSheet sourceSheet, boolean copyStyle, HSSFWorkbook targetWork)
            throws Exception {
        //参数是否为 null
        if (targetSheet == null || sourceSheet == null) {
            throw new Exception(
                    "targetSheet、sourceSheet都不能为空");
        }

        // 复制源表中的行
        int maxColumnNum = 0;
        Map<Integer, HSSFCellStyle> styleMap = copyStyle ? new HashMap<Integer, HSSFCellStyle>()
                : null;
        HSSFPatriarch targetPatriarch = targetSheet.createDrawingPatriarch(); // 用于复制注释
        //遍历 第一行 到 最后一行 的数据
        for (int i = sourceSheet.getFirstRowNum(), l = sourceSheet
                .getLastRowNum(); i <= l; i++) {
            HSSFRow sourceRow = sourceSheet.getRow(i);
            HSSFRow targetRow = targetSheet.createRow(i);
            if (sourceRow != null) {
                // 拷贝Row
                copyRow(targetRow, sourceRow,
                        targetPatriarch, targetWork);
                if (sourceRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = sourceRow.getLastCellNum();
                }
            }
        }
        // 复制源表中的合并单元格
        mergerRegion(targetSheet, sourceSheet);
        // 设置目标Sheet的列宽
        for (int i = 0; i <= maxColumnNum; i++) {
            targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
        }
    }

    /**
     * 复制源表中的合并单元格
     *
     * @param targetSheet
     * @param sourceSheet
     * @throws Exception
     */
    public static void mergerRegion(HSSFSheet targetSheet, HSSFSheet sourceSheet)
            throws Exception {
        if (targetSheet == null || sourceSheet == null) {
            throw new Exception("targetSheet、sourceSheet都不能为空");
        }
        // 复制源表中的合并单元格
        for (int i = 0, l = sourceSheet.getNumMergedRegions(); i < l; i++) {
            CellRangeAddress sourceRange = sourceSheet.getMergedRegion(i);
            CellRangeAddress targetRange = new CellRangeAddress(sourceRange
                    .getFirstRow(), sourceRange.getFirstRow(), sourceRange
                    .getFirstColumn(), sourceRange.getLastColumn());
            targetSheet.addMergedRegion(targetRange);
        }
    }

    /**
     * 拷贝Row
     *
     * @param sourceRow
     * @param targetRow
     * @param targetPatriarch
     * @throws Exception
     */
    public static void copyRow(HSSFRow targetRow, HSSFRow sourceRow,
                               HSSFPatriarch targetPatriarch, HSSFWorkbook targetWork)
            throws Exception {
        //参数是否为null
        if (targetRow == null || sourceRow == null) {
            return;
        }

        // 设置行高
        targetRow.setHeight(sourceRow.getHeight());
        //遍历 第一行 到 最后一行 的数据
        for (int i = sourceRow.getFirstCellNum(), l = sourceRow.getLastCellNum(); i <= l; i++) {
            HSSFCell sourceCell = sourceRow.getCell(i);
            HSSFCell targetCell = targetRow.getCell(i);
            if (sourceCell != null) {
                if (targetCell == null) {
                    targetCell = targetRow.createCell(i);
                }
                // 拷贝单元格，包括内容和样式
                copyCell(targetCell, sourceCell, targetWork);
                // 拷贝单元格注释
                if (targetPatriarch != null) {
                    copyComment(targetCell, sourceCell, targetPatriarch);
                }
            }
        }
    }

    /**
     * 拷贝单元格注释
     *
     * @param targetCell
     * @param sourceCell
     * @param targetPatriarch
     * @throws Exception
     */
    public static void copyComment(HSSFCell targetCell, HSSFCell sourceCell,
                                   HSSFPatriarch targetPatriarch) throws Exception {
        if (targetCell == null || sourceCell == null || targetPatriarch == null) {
            throw new Exception("targetCell、sourceCell、targetPatriarch都不能为空");
        }

        // 处理单元格注释
        HSSFComment sourceComment = sourceCell.getCellComment();
        //sourceComment 是否为null
        if (sourceComment != null) {
            HSSFComment targetComment = targetPatriarch
                    .createComment(new HSSFClientAnchor());
            targetComment.setAnchor(sourceComment.getAnchor());
            targetComment.setColumn(sourceComment.getColumn());
            targetComment.setFillColor(sourceComment.getFillColor());
            targetComment.setHorizontalAlignment(sourceComment
                    .getHorizontalAlignment());
            targetComment.setLineStyle(sourceComment.getLineStyle());
            targetComment.setLineStyleColor(sourceComment.getLineStyleColor());
            targetComment.setLineWidth(sourceComment.getLineWidth());
            targetComment.setMarginBottom(sourceComment.getMarginBottom());
            targetComment.setMarginLeft(sourceComment.getMarginLeft());
            targetComment.setMarginTop(sourceComment.getMarginTop());
            targetComment.setMarginRight(sourceComment.getMarginRight());
            targetComment.setNoFill(sourceComment.isNoFill());
            targetComment.setRow(sourceComment.getRow());
            // Shape type can not be changed in HSSFComment
            // targetComment.setShapeType(sourceComment.getShapeType());
            targetComment.setString(sourceComment.getString());
            targetComment.setVerticalAlignment(sourceComment
                    .getVerticalAlignment());
            targetComment.setVisible(sourceComment.isVisible());
            targetCell.setCellComment(targetComment);
        }
    }

    /**
     * 拷贝单元格，包括内容和样式
     *
     * @param targetCell
     * @param sourceCell
     * @throws Exception
     */
    public static void copyCell(HSSFCell targetCell, HSSFCell sourceCell, HSSFWorkbook targetWork) throws Exception {
        //参数是否为null
        if (targetCell == null || sourceCell == null) {
            throw new Exception("targetRow、sourceRow都不能为空");
        }
        HSSFCellStyle targetCellStyle = targetWork.createCellStyle();
        targetCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
        targetCell.setCellStyle(targetCellStyle);

        // 处理单元格内容
        switch (sourceCell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                targetCell.setCellValue(sourceCell.getRichStringCellValue());
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                targetCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                targetCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                targetCell.setCellFormula(sourceCell.getCellFormula());
                break;
            default:
                break;
        }
    }

    public static void setCellValue(HSSFSheet sheet, int rowNum, int cellNum, String cellValue) {
        if (cellValue == null) {
            cellValue = "";
        }
        HSSFRow row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        HSSFCell cell = row.getCell(cellNum);
        if (cell == null) {
            cell = row.createCell(cellNum);
        }
        cell.setCellValue(cellValue);
    }

    /**
     * 复制源表中的合并单元格
     *
     * @param targetSheet
     * @param sourceSheet
     * @throws Exception
     */
    public static void mergerRegion(HSSFSheet targetSheet, HSSFSheet sourceSheet, int rowNum)
            throws Exception {
        if (targetSheet == null || sourceSheet == null) {
            throw new Exception("targetSheet、sourceSheet都不能为空");
        }
        // 复制源表中的合并单元格
        for (int i = 0, l = sourceSheet.getNumMergedRegions(); i < l; i++) {
            CellRangeAddress sourceRange = sourceSheet.getMergedRegion(i);
            CellRangeAddress targetRange = new CellRangeAddress(sourceRange
                    .getFirstRow(), sourceRange.getFirstRow(), sourceRange
                    .getFirstColumn(), sourceRange.getLastColumn());
            targetSheet.addMergedRegion(targetRange);
        }
    }
}
