package com.hex.bigdata.udsp.rts.service;

import com.hex.bigdata.udsp.common.constant.ComExcelEnums;
import com.hex.bigdata.udsp.common.dao.ComDatasourceMapper;
import com.hex.bigdata.udsp.common.model.ComExcelParam;
import com.hex.bigdata.udsp.common.model.ComExcelProperties;
import com.hex.bigdata.udsp.common.model.ComUploadExcelContent;
import com.hex.bigdata.udsp.common.util.ExcelCopyUtils;
import com.hex.bigdata.udsp.common.util.ExcelUploadhelper;
import com.hex.bigdata.udsp.rts.dao.RtsMetadataMapper;
import com.hex.bigdata.udsp.rts.dto.RtsMetadataColsView;
import com.hex.bigdata.udsp.rts.dto.RtsMetadataView;
import com.hex.bigdata.udsp.rts.model.RtsMetadata;
import com.hex.bigdata.udsp.rts.model.RtsMetadataCol;
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
 * 实时流-元数据服务
 * Created by tomnic on 2017/2/28.
 */
@Service
public class RtsMetadataService extends BaseService {

    private static Logger logger = LogManager.getLogger(RtsMetadataService.class);

    @Autowired
    private RtsMetadataMapper rtsMetadataMapper;
    @Autowired
    private RtsMetadataColService rtsMetadataColService;
    @Autowired
    private ComDatasourceMapper comDatasourceMapper;

    private static List<ComExcelParam> comExcelParams = new ArrayList<>();

    static {
        //固定栏内容
        comExcelParams.add(new ComExcelParam(1, 1, "name"));
        comExcelParams.add(new ComExcelParam(1, 3, "dsId"));
        comExcelParams.add(new ComExcelParam(2, 1, "topic"));
        comExcelParams.add(new ComExcelParam(2, 3, "describe"));
        comExcelParams.add(new ComExcelParam(3, 1, "note"));
    }

    @Transactional
    public String insert(RtsMetadata rtsMetadata) {
        String pkId = Util.uuid();
        rtsMetadata.setPkId(pkId);
        if (rtsMetadataMapper.insert(pkId, rtsMetadata)) {
            return pkId;
        }
        return "";
    }

    /**
     * 根据主键更新对象实体
     *
     * @param rtsMetadata
     * @return
     */
    @Transactional
    public boolean update(RtsMetadata rtsMetadata) {
        return rtsMetadataMapper.update(rtsMetadata.getPkId(), rtsMetadata);
    }

    /**
     * 根据主键进行删除
     *
     * @param pkId
     * @return
     */
    @Transactional
    public boolean delete(String pkId) {
        return rtsMetadataMapper.delete(pkId);
    }

    /**
     * 根据主键进行查询
     *
     * @param pkId
     * @return
     */
    public RtsMetadata select(String pkId) {
        return rtsMetadataMapper.select(pkId);
    }

    /**
     * 分页多条件查询
     *
     * @param rtsMetadataView 查询参数
     * @param page            分页参数
     * @return
     */
    public List<RtsMetadataView> select(RtsMetadataView rtsMetadataView, Page page) {
        return rtsMetadataMapper.selectPage(rtsMetadataView, page);
    }

    /**
     * 根据查询条件查询结果list，不分页
     *
     * @param rtsMetadataView 查询参数
     * @return
     */
    public List<RtsMetadataView> select(RtsMetadataView rtsMetadataView) {
        return rtsMetadataMapper.select(rtsMetadataView);
    }

    /**
     * 新增实时流数据源实体
     *
     * @param rtsMetadataColsView
     * @return
     */
    @Transactional
    public String insert(RtsMetadataColsView rtsMetadataColsView) {
        RtsMetadata rtsMetadata = rtsMetadataColsView.getRtsMetadata();
        String pkId = this.insert(rtsMetadata);
        if (StringUtils.isNotBlank(pkId)) {
            List<RtsMetadataCol> rtsMetadataCols = rtsMetadataColsView.getRtsMetadataColList();
            for (RtsMetadataCol rtsMetadataCol : rtsMetadataCols) {
                rtsMetadataCol.setMdId(pkId);
                rtsMetadataColService.insert(rtsMetadataCol);
            }
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
        return rtsMetadataMapper.selectByName(name) != null;
    }

    /**
     * 更新数据源基础信息以及配置参数信息
     *
     * @param rtsMetadataColsView
     * @return
     */
    @Transactional
    public boolean update(RtsMetadataColsView rtsMetadataColsView) {
        RtsMetadata rtsMetadata = rtsMetadataColsView.getRtsMetadata();
        String pkId = rtsMetadata.getPkId();
        //更新基础信息
        rtsMetadataMapper.update(pkId, rtsMetadata);
        //删除旧的的配置参数信息
        if (rtsMetadataColService.deleteByMdId(pkId)) {
            //插入新的配置参数信息
            List<RtsMetadataCol> rtsMetadataCols = rtsMetadataColsView.getRtsMetadataColList();
            for (RtsMetadataCol rtsMetadataCol : rtsMetadataCols) {
                rtsMetadataCol.setMdId(pkId);
                rtsMetadataColService.insert(rtsMetadataCol);
            }
        }
        return true;
    }

    /**
     * 批量删除
     *
     * @param rtsMetadatas
     * @return
     */
    @Transactional
    public boolean delete(RtsMetadata[] rtsMetadatas) {
        boolean flag = true;
        for (RtsMetadata rtsMetadata : rtsMetadatas) {
            String pkId = rtsMetadata.getPkId();
            boolean delFlg = delete(pkId);
            if (!delFlg) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public boolean hasUsed(String pkId) {
        return rtsMetadataMapper.selectAppIdsByMdid(pkId).size() > 0;
    }

    /**
     * 数据源excel文件导入
     *
     * @param uploadFilePath
     * @return
     */
    public Map<String, String> uploadExcel(String uploadFilePath) {
        Map resultMap = new HashMap<String, String>(2);
        File uploadFile = new File(uploadFilePath);
        FileInputStream in = null;
        try {
            ComUploadExcelContent dataSourceContent = new ComUploadExcelContent();
            dataSourceContent.setClassName("com.hex.bigdata.udsp.rts.model.RtsMetadata");

            dataSourceContent.setComExcelParams(comExcelParams);
            List<ComExcelProperties> comExcelPropertiesList = new ArrayList<>();
            //添加对应的配置栏内容
            comExcelPropertiesList.add(new ComExcelProperties("数据列配置",
                    "com.hex.bigdata.udsp.rts.model.RtsMetadataCol",
                    10, 0, 1, ComExcelEnums.RtsMetadataCol.getAllNums()));

            dataSourceContent.setComExcelPropertiesList(comExcelPropertiesList);
            dataSourceContent.setType("fixed");

            in = new FileInputStream(uploadFile);
            HSSFWorkbook hfb = new HSSFWorkbook(in);
            HSSFSheet sheet;
            for (int i = 0, activeIndex = hfb.getNumberOfSheets(); i < activeIndex; i++) {
                sheet = hfb.getSheetAt(i);

                Map<String, List> uploadExcelModel = ExcelUploadhelper.getUploadExcelModel(sheet, dataSourceContent);
                List<RtsMetadata> rtsMetadatas = (List<RtsMetadata>) uploadExcelModel.get("com.hex.bigdata.udsp.rts.model.RtsMetadata");
                RtsMetadata rtsMetadata = rtsMetadatas.get(0);
                if (rtsMetadataMapper.selectByName(rtsMetadata.getName()) != null) {
                    resultMap.put("status", "false");
                    resultMap.put("message", "第" + (i + 1) + "个名称重复！");
                    break;
                }
                if (comDatasourceMapper.selectByModelAndName("RTS", rtsMetadata.getDsId()) == null) {
                    resultMap.put("status", "false");
                    resultMap.put("message", "第" + (i + 1) + "个对应数据源不存在！");
                    break;
                }
                //设置数据源id
                rtsMetadata.setDsId(comDatasourceMapper.selectByModelAndName("RTS", rtsMetadata.getDsId()).getPkId());
                String pkId = insert(rtsMetadata);
                List<RtsMetadataCol> rtsMetadataCols = (List<RtsMetadataCol>) uploadExcelModel.get("com.hex.bigdata.udsp.rts.model.RtsMetadataCol");
                for(RtsMetadataCol rtsMetadataCol: rtsMetadataCols){
                    if(rtsMetadataCol.getName().trim().equals("time")) {
                        resultMap.put ("status", "false");
                        resultMap.put ("message", "第" + (i + 1) + "个数据列配置中不能有保留名称'time'！");
                        break;
                    }
                }
                boolean insert = rtsMetadataColService.insertList(pkId, rtsMetadataCols);
                if (insert) {
                    resultMap.put("status", "true");
                } else {
                    resultMap.put("status", "false");
                    resultMap.put("message", "第" + (i + 1) + "个保存失败！");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "false");
            resultMap.put("message", "程序内部异常：" + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultMap;
    }

    public String createExcel(RtsMetadata[] rtsMetadatas) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFWorkbook sourceWork = null;
        HSSFSheet sourceSheet = null;
        HSSFRow row;
        HSSFCell cell;
        String seprator = FileUtil.getFileSeparator();
        String dirPath = FileUtil.getWebPath("/");
        //模板文件位置
        String templateFile = ExcelCopyUtils.templatePath + seprator + "downLoadTemplate_rtsMetadata.xls";
        // 判断是否存在，不存在则创建
        dirPath += seprator + "TEMP_DOWNLOAD";
        File file = new File(dirPath);
        // 判断文件是否存在
        if (!file.exists()) {
            FileUtil.mkdir(dirPath);
        }
        dirPath += seprator + "download_rtsMetadata_excel_" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".xls";
        // 获取模板文件第一个Sheet对象
        POIFSFileSystem sourceFile = null;

        try {
            sourceFile = new POIFSFileSystem(new FileInputStream(templateFile));
            sourceWork = new HSSFWorkbook(sourceFile);
            sourceSheet = sourceWork.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HSSFSheet sheet = null;
        for (RtsMetadata rtsMetadata : rtsMetadatas) {
            rtsMetadata = rtsMetadataMapper.select(rtsMetadata.getPkId());
            sheet = workbook.createSheet(rtsMetadata.getName ());

            //将前面样式内容复制到下载表中
            int i = 0;
            for (; i < 10; i++) {
                try {
                    ExcelCopyUtils.copyRow(sheet.createRow(i), sourceSheet.getRow(i), sheet.createDrawingPatriarch(), workbook);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //设置数据源名字
            rtsMetadata.setDsId(comDatasourceMapper.select(rtsMetadata.getDsId()).getName());
            for (ComExcelParam comExcelParam : comExcelParams) {
                try {
                    Field field = rtsMetadata.getClass().getDeclaredField(comExcelParam.getName());
                    field.setAccessible(true);
                    ExcelCopyUtils.setCellValue(sheet, comExcelParam.getRowNum(), comExcelParam.getCellNum(),
                            field.get(rtsMetadata) == null ? "" : field.get(rtsMetadata).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<RtsMetadataCol> rtsMetadataCols = rtsMetadataColService.selectByMdId(rtsMetadata.getPkId());
            if (rtsMetadataCols.size() > 0) {
                for (RtsMetadataCol col : rtsMetadataCols) {
                    row = sheet.createRow(i);
                    cell = row.createCell(0);
                    cell.setCellValue(col.getSeq());
                    cell = row.createCell(1);
                    cell.setCellValue(col.getName());
                    cell = row.createCell(2);
                    cell.setCellValue(col.getType());
                    cell = row.createCell(3);
                    cell.setCellValue(col.getDescribe());
                    i++;
                }
            }
        }

        if (workbook != null) {
            try {
                FileOutputStream stream = new FileOutputStream(dirPath);
                workbook.write(new FileOutputStream(dirPath));
                stream.close();
                return dirPath;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
