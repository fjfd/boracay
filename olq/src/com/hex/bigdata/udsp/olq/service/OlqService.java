package com.hex.bigdata.udsp.olq.service;

import com.hex.bigdata.udsp.common.model.ComExcelParam;
import com.hex.bigdata.udsp.common.util.ExcelCopyUtils;
import com.hex.goframe.service.BaseService;
import com.hex.goframe.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA
 * Author: tomnic.wang
 * DATE:2017/10/12
 * TIME:19:22
 */
@Service
public class OlqService extends BaseService {

    private static Logger logger = LogManager.getLogger (OlqApplicationService.class);

    public HSSFWorkbook setWorkbookSheet(HSSFWorkbook workbook, Map<String, String> map) {
        String templateFile = ExcelCopyUtils.templatePath + FileUtil.getFileSeparator () + "downLoadTemplate_allServiceInfo.xls";
        // 获取模板文件第一个Sheet对象
        POIFSFileSystem sourceFile = null;
        HSSFWorkbook sourceWork = null;
        HSSFSheet sourceSheet = null;
        try {
            sourceFile = new POIFSFileSystem (new FileInputStream (templateFile));
            sourceWork = new HSSFWorkbook (sourceFile);
            // 联机查询模板为第2个sheet
            sourceSheet = sourceWork.getSheetAt (1);
        } catch (IOException e) {
            e.printStackTrace ();
        }

        List<ComExcelParam> comExcelParams = new ArrayList<ComExcelParam> ();
        comExcelParams.add (new ComExcelParam (2, 1, "serviceName"));
        comExcelParams.add (new ComExcelParam (2, 3, "serviceDescribe"));
        comExcelParams.add (new ComExcelParam (3, 1, "maxSyncNum"));
        comExcelParams.add (new ComExcelParam (3, 3, "maxAsyncNum"));
        comExcelParams.add (new ComExcelParam (3, 5, "maxSyncWaitNum"));
        comExcelParams.add (new ComExcelParam (3, 7, "maxAsyncWaitNum"));
        comExcelParams.add (new ComExcelParam (4, 1, "userId"));
        comExcelParams.add (new ComExcelParam (4, 3, "userName"));

        HSSFSheet sheet = workbook.createSheet (map.get ("serviceName"));

        //将前面样式内容复制到下载表中
        int i = 0;
        for (; i < 11; i++) {
            try {
                ExcelCopyUtils.copyRow (sheet.createRow (i), sourceSheet.getRow (i), sheet.createDrawingPatriarch (), workbook);
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }

        for (ComExcelParam comExcelParam : comExcelParams) {
            try {
                ExcelCopyUtils.setCellValue (sheet, comExcelParam.getRowNum (), comExcelParam.getCellNum (), map.get (comExcelParam.getName ()));
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }

        return workbook;
    }
}
