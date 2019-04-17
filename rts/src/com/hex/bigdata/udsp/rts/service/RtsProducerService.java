package com.hex.bigdata.udsp.rts.service;

import com.hex.bigdata.udsp.common.constant.ComExcelEnums;
import com.hex.bigdata.udsp.common.model.ComExcelParam;
import com.hex.bigdata.udsp.common.model.ComExcelProperties;
import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.bigdata.udsp.common.model.ComUploadExcelContent;
import com.hex.bigdata.udsp.common.service.ComPropertiesService;
import com.hex.bigdata.udsp.common.util.CreateFileUtil;
import com.hex.bigdata.udsp.common.util.ExcelCopyUtils;
import com.hex.bigdata.udsp.common.util.ExcelUploadhelper;
import com.hex.bigdata.udsp.rts.dao.RtsMetadataMapper;
import com.hex.bigdata.udsp.rts.dao.RtsProducerMapper;
import com.hex.bigdata.udsp.rts.dto.RtsIndexDto;
import com.hex.bigdata.udsp.rts.dto.RtsProducerProsView;
import com.hex.bigdata.udsp.rts.dto.RtsProducerView;
import com.hex.bigdata.udsp.rts.model.RtsMetadataCol;
import com.hex.bigdata.udsp.rts.model.RtsProducer;
import com.hex.goframe.model.Page;
import com.hex.goframe.service.BaseService;
import com.hex.goframe.util.DateUtil;
import com.hex.goframe.util.FileUtil;
import com.hex.goframe.util.Util;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by tomnic on 2017/3/1.
 */
@Service
public class RtsProducerService extends BaseService {

    private static Logger logger = LogManager.getLogger (RtsProducerService.class);

    @Autowired
    private ComPropertiesService comPropertiesService;
    @Autowired
    private RtsProducerMapper rtsProducerMapper;
    @Autowired
    private RtsMetadataColService rtsMetadataColService;
    @Autowired
    private RtsMetadataMapper rtsMetadataMapper;

    private static List<ComExcelParam> comExcelParams;

    static {
        comExcelParams = new ArrayList<> ();
        comExcelParams.add (new ComExcelParam (1, 1, "name"));
        comExcelParams.add (new ComExcelParam (1, 3, "mdId"));
        comExcelParams.add (new ComExcelParam (2, 1, "describe"));
        comExcelParams.add (new ComExcelParam (3, 1, "note"));
    }

    @Transactional
    public String insert(RtsProducer rtsProducer) {
        String pkId = Util.uuid ();
        rtsProducer.setPkId (pkId);
        if (rtsProducerMapper.insert (pkId, rtsProducer)) {
            return pkId;
        }
        return "";
    }

    /**
     * 根据主键更新对象实体
     *
     * @param rtsProducer
     * @return
     */
    @Transactional
    public boolean update(RtsProducer rtsProducer) {
        return rtsProducerMapper.update (rtsProducer.getPkId (), rtsProducer);
    }

    /**
     * 根据主键进行删除
     *
     * @param pkId
     * @return
     */
    @Transactional
    public boolean delete(String pkId) {
        return rtsProducerMapper.delete (pkId);
    }

    /**
     * 根据主键进行查询
     *
     * @param pkId
     * @return
     */
    public RtsProducer select(String pkId) {
        return rtsProducerMapper.select (pkId);
    }

    /**
     * 分页多条件查询
     *
     * @param rtsProConfigView 查询参数
     * @param page             分页参数
     * @return
     */
    public List<RtsProducerView> select(RtsProducerView rtsProConfigView, Page page) {
        return rtsProducerMapper.selectPage (rtsProConfigView, page);
    }

    /**
     * 根据查询条件查询结果list，不分页
     *
     * @param rtsProConfigView 查询参数
     * @return
     */
    public List<RtsProducerView> select(RtsProducerView rtsProConfigView) {
        return rtsProducerMapper.select (rtsProConfigView);
    }

    public List<RtsProducer> selectAll() {
        return rtsProducerMapper.selectAll ();
    }

    /**
     * 新增实时流数据源实体
     *
     * @param rtsProducerProsView 实时流数据源实体
     * @return
     */
    @Transactional
    public String insert(RtsProducerProsView rtsProducerProsView) {
        RtsProducer rtsProducer = rtsProducerProsView.getRtsProducer ();
        String pkId = this.insert (rtsProducer);
        if (StringUtils.isNotBlank (pkId)) {
            List<ComProperties> comPropertiesList = rtsProducerProsView.getComPropertiesList ();
            comPropertiesService.insertList (pkId, comPropertiesList);
            return pkId;
        }
        return "";
    }


    /**
     * 检查名称是否唯一
     *
     * @param name 数据源名称
     * @return 存在返回true，不存在返回fals
     */
    public boolean checekUniqueName(String name) {
        RtsProducer rtsProducer = this.rtsProducerMapper.selectByName (name);
        return rtsProducer != null;
    }

    /**
     * 更新数据源基础信息以及配置参数信息
     *
     * @param rtsProducerProsView
     * @return
     */
    @Transactional
    public boolean update(RtsProducerProsView rtsProducerProsView) {
        RtsProducer rtsProducer = rtsProducerProsView.getRtsProducer ();
        String pkId = rtsProducer.getPkId ();
        //更新基础信息
        if (!rtsProducerMapper.update (pkId, rtsProducer)) {
            return false;
        }
        //删除旧的的配置参数信息
        if (!comPropertiesService.deleteList (pkId)) {
            return false;
        }
        //插入新的配置参数信息
        List<ComProperties> comPropertiesList = rtsProducerProsView.getComPropertiesList ();
        return comPropertiesService.insertList (pkId, comPropertiesList);
    }

    /**
     * 批量删除
     *
     * @param rtsProducers
     * @return
     */
    @Transactional
    public boolean delete(RtsProducer[] rtsProducers) {
        boolean flag = true;
        for (RtsProducer rtsProducer : rtsProducers) {
            String pkId = rtsProducer.getPkId ();
            boolean delFlg = delete (pkId);
            //删除旧的的配置参数信息
            if (!delFlg) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public List<RtsMetadataCol> selectProducerColumns(String producerId) {
        RtsProducer rtsProducer = this.select (producerId);
        List<RtsMetadataCol> colList = null;
        if (rtsProducer != null) {
            colList = rtsMetadataColService.selectByMdId (rtsProducer.getMdId ());
        }
        return colList;
    }

    /**
     * 数据源excel文件导入
     *
     * @param uploadFilePath
     * @return
     */
    public Map<String, String> uploadExcel(String uploadFilePath) {
        Map resultMap = new HashMap<String, String> (2);
        File uploadFile = new File (uploadFilePath);
        FileInputStream in = null;
        try {
            ComUploadExcelContent dataSourceContent = new ComUploadExcelContent ();
            dataSourceContent.setClassName ("com.hex.bigdata.udsp.rts.model.RtsProducer");
            List<ComExcelParam> comExcelParams = new ArrayList<> ();
            //固定栏内容
            comExcelParams.add (new ComExcelParam (1, 1, "name"));
            comExcelParams.add (new ComExcelParam (1, 3, "mdId"));
            comExcelParams.add (new ComExcelParam (2, 1, "describe"));
            comExcelParams.add (new ComExcelParam (3, 1, "note"));

            dataSourceContent.setComExcelParams (comExcelParams);
            List<ComExcelProperties> comExcelPropertiesList = new ArrayList<> ();
            //添加对应的配置栏内容
            comExcelPropertiesList.add (new ComExcelProperties ("数据源配置",
                    "com.hex.bigdata.udsp.common.model.ComProperties",
                    10, 0, 1, ComExcelEnums.Comproperties.getAllNums ()));

            dataSourceContent.setComExcelPropertiesList (comExcelPropertiesList);
            dataSourceContent.setType ("fixed");

            in = new FileInputStream (uploadFile);
            HSSFWorkbook hfb = new HSSFWorkbook (in);
            HSSFSheet sheet;
            for (int i = 0, activeIndex = hfb.getNumberOfSheets (); i < activeIndex; i++) {
                sheet = hfb.getSheetAt (i);
                Map<String, List> uploadExcelModel = ExcelUploadhelper.getUploadExcelModel (sheet, dataSourceContent);
                List<RtsProducer> rtsProducers = (List<RtsProducer>) uploadExcelModel.get ("com.hex.bigdata.udsp.rts.model.RtsProducer");
                RtsProducer rtsProducer = rtsProducers.get (0);
                if (rtsProducerMapper.selectByName (rtsProducer.getName ()) != null) {
                    resultMap.put ("status", "false");
                    resultMap.put ("message", "第" + (i + 1) + "个名称重复！");
                    break;
                }
                if (rtsMetadataMapper.selectByName (rtsProducer.getMdId ()) == null) {
                    resultMap.put ("status", "false");
                    resultMap.put ("message", "第" + (i + 1) + "个对应元数据不存在！");
                    break;
                }
                //更新元数据
                rtsProducer.setMdId (rtsMetadataMapper.selectByName (rtsProducer.getMdId ()).getPkId ());
                String pkId = insert (rtsProducer);
                List<ComProperties> comPropertiesList = (List<ComProperties>) uploadExcelModel.get ("com.hex.bigdata.udsp.common.model.ComProperties");
                boolean insert = comPropertiesService.insertList (pkId, comPropertiesList);
                if (insert) {
                    resultMap.put ("status", "true");
                } else {
                    resultMap.put ("status", "false");
                    resultMap.put ("message", "第" + (i + 1) + "个保存失败！");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
            resultMap.put ("status", "false");
            resultMap.put ("message", "程序内部异常：" + e.getMessage ());
        } finally {
            if (in != null) {
                try {
                    in.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
        return resultMap;
    }


    public String createExcel(RtsProducer[] rtsProducers) {
        HSSFWorkbook workbook = new HSSFWorkbook ();
        HSSFWorkbook sourceWork;
        HSSFSheet sourceSheet = null;
        HSSFRow row;
        HSSFCell cell;
        String seprator = FileUtil.getFileSeparator ();
        //模板文件位置
        String templateFile = ExcelCopyUtils.templatePath + seprator + "downLoadTemplate_rtsProducer.xls";
        // 下载地址
        String dirPath = CreateFileUtil.getLocalDirPath ();
        dirPath += seprator + "download_rtsProducer_excel_" + DateUtil.format (new Date (), "yyyyMMddHHmmss") + ".xls";
        // 获取模板文件第一个Sheet对象
        POIFSFileSystem sourceFile = null;

        try {
            sourceFile = new POIFSFileSystem (new FileInputStream (templateFile));
            sourceWork = new HSSFWorkbook (sourceFile);
            sourceSheet = sourceWork.getSheetAt (0);
        } catch (IOException e) {
            e.printStackTrace ();
        }

        for (RtsProducer rtsProducer : rtsProducers) {
            this.setWorkbookSheet (workbook, sourceSheet, comExcelParams, rtsProducer);
        }

        if (workbook != null) {
            try {
                FileOutputStream stream = new FileOutputStream (dirPath);
                workbook.write (new FileOutputStream (dirPath));
                stream.close ();
                return dirPath;
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
        return null;
    }

    public HSSFWorkbook setWorkbookSheet(HSSFWorkbook workbook, Map<String, String> map, String appId) {
        String templateFile = ExcelCopyUtils.templatePath + FileUtil.getFileSeparator () + "downLoadTemplate_allServiceInfo.xls";
        // 获取模板文件第一个Sheet对象
        POIFSFileSystem sourceFile = null;
        HSSFWorkbook sourceWork;
        HSSFSheet sourceSheet = null;
        try {
            sourceFile = new POIFSFileSystem (new FileInputStream (templateFile));
            sourceWork = new HSSFWorkbook (sourceFile);
            // 实时流-生产者为第5个sheet
            sourceSheet = sourceWork.getSheetAt (4);
        } catch (IOException e) {
            e.printStackTrace ();
        }

        RtsProducer rtsProducer = this.select (appId);

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
        for (; i < 10; i++) {
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

        this.setWorkbookSheetPart (sheet, rtsProducer, sourceSheet, workbook, new RtsIndexDto (i));

        return workbook;
    }

    private void setWorkbookSheet(HSSFWorkbook workbook, HSSFSheet sourceSheet, List<ComExcelParam> comExcelParams, RtsProducer rtsProducer) {
        rtsProducer = rtsProducerMapper.select (rtsProducer.getPkId ());
        HSSFSheet sheet = workbook.createSheet (rtsProducer.getName ());

        //将前面样式内容复制到下载表中
        int i = 0;
        for (; i < 10; i++) {
            try {
                ExcelCopyUtils.copyRow (sheet.createRow (i), sourceSheet.getRow (i), sheet.createDrawingPatriarch (), workbook);
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }

        //设置元数据名字
        rtsProducer.setMdId (rtsMetadataMapper.select (rtsProducer.getMdId ()).getName ());
        for (ComExcelParam comExcelParam : comExcelParams) {
            try {
                Field field = rtsProducer.getClass ().getDeclaredField (comExcelParam.getName ());
                field.setAccessible (true);
                ExcelCopyUtils.setCellValue (sheet, comExcelParam.getRowNum (), comExcelParam.getCellNum (),
                        field.get (rtsProducer) == null ? "" : field.get (rtsProducer).toString ());
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }

        this.setWorkbookSheetPart (sheet, rtsProducer, sourceSheet, workbook, new RtsIndexDto (i));
    }

    private void setWorkbookSheetPart(HSSFSheet sheet, RtsProducer rtsProducer, HSSFSheet sourceSheet, HSSFWorkbook workbook, RtsIndexDto rtsIndexDto) {
        HSSFRow row;
        HSSFCell cell;
        int rowIndex = rtsIndexDto.getRowIndex ();
        List<RtsMetadataCol> colList = rtsMetadataColService.selectByMdId (rtsProducer.getMdId ());
        if (colList != null && colList.size () != 0) {
            int k = 1;
            for (RtsMetadataCol col : colList) {
                row = sheet.createRow (rowIndex);
                cell = row.createCell (0);
                cell.setCellValue (k);
                cell = row.createCell (1);
                cell.setCellValue (col.getName ());
                cell = row.createCell (2);
                cell.setCellValue (col.getType ());
                cell = row.createCell (3);
                cell.setCellValue (col.getDescribe ());
                rowIndex++;
                k++;
            }
        }
    }

}
