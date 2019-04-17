package com.hex.bigdata.udsp.olq.service;

import com.hex.bigdata.udsp.common.constant.ComExcelEnums;
import com.hex.bigdata.udsp.common.constant.DatasourceMode;
import com.hex.bigdata.udsp.common.constant.EnumTrans;
import com.hex.bigdata.udsp.common.constant.YesOrNo;
import com.hex.bigdata.udsp.common.dto.ComDatasourceView;
import com.hex.bigdata.udsp.common.model.ComDatasource;
import com.hex.bigdata.udsp.common.model.ComExcelParam;
import com.hex.bigdata.udsp.common.model.ComExcelProperties;
import com.hex.bigdata.udsp.common.model.ComUploadExcelContent;
import com.hex.bigdata.udsp.common.service.ComDatasourceService;
import com.hex.bigdata.udsp.common.util.CreateFileUtil;
import com.hex.bigdata.udsp.common.util.ExcelCopyUtils;
import com.hex.bigdata.udsp.common.util.ExcelUploadhelper;
import com.hex.bigdata.udsp.olq.dao.OlqApplicationMapper;
import com.hex.bigdata.udsp.olq.dto.OlqApplicationDto;
import com.hex.bigdata.udsp.olq.dto.OlqApplicationView;
import com.hex.bigdata.udsp.olq.dto.OlqIndexDto;
import com.hex.bigdata.udsp.olq.model.OlqApplication;
import com.hex.bigdata.udsp.olq.model.OlqApplicationParam;
import com.hex.bigdata.udsp.olq.utils.SqlExpressionEvaluator;
import com.hex.goframe.model.MessageResult;
import com.hex.goframe.model.Page;
import com.hex.goframe.service.BaseService;
import com.hex.goframe.util.DateUtil;
import com.hex.goframe.util.FileUtil;
import com.hex.goframe.util.Util;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 联机查询应用管理服务
 * Created with IntelliJ IDEA
 * Author: tomnic.wang
 * DATE:2017/6/26
 * TIME:10:48
 */
@Service
public class OlqApplicationService extends BaseService {

    private static Logger logger = LogManager.getLogger (OlqApplicationService.class);

    @Autowired
    private OlqApplicationMapper olqApplicationMapper;
    @Autowired
    private ComDatasourceService comDatasourceService;
    @Autowired
    private OlqApplicationParamService olqApplicationParamService;

    /**
     * 分页查询
     *
     * @param olqApplicationView
     * @param page
     * @return
     */
    public List<OlqApplicationView> select(OlqApplicationView olqApplicationView, Page page) {
        return this.olqApplicationMapper.selectPage (olqApplicationView, page);
    }

    /**
     * 应用信息插入到数据库
     *
     * @param oLQApplicationDto
     * @return
     */
    @Transactional
    public String insert(OlqApplicationDto oLQApplicationDto) {
        String pkId = Util.uuid ();
        OlqApplication olqApplication = oLQApplicationDto.getOlqApplication ();
        olqApplication.setPkId (pkId);
        this.olqApplicationMapper.insert (pkId, olqApplication);
        List<OlqApplicationParam> params = oLQApplicationDto.getParams ();
        if (params != null && params.size () != 0) {
            olqApplicationParamService.insertList (params, pkId);
        }
        return pkId;
    }

    /**
     * @param oLQApplicationDto
     * @return
     */
    @Transactional
    public boolean update(OlqApplicationDto oLQApplicationDto) {
        //应用信息更新到数据库
        OlqApplication olqApplication = oLQApplicationDto.getOlqApplication ();
        boolean updateFlg = this.olqApplicationMapper.update (olqApplication.getPkId (), olqApplication);
        if (!updateFlg) {
            logger.info ("更新联机查询应用信息失败！,参数信息如下：【" + oLQApplicationDto.toString () + "】");
            return updateFlg;
        }
        String pkId = olqApplication.getPkId ();
        //应用参数配置信息更新到数据库
        boolean delFlg = this.olqApplicationParamService.deleteByAppId (pkId);
        if (!delFlg) {
            logger.info ("删除更新联机查询应用参数信息失败！");
            return delFlg;
        }
        List<OlqApplicationParam> params = oLQApplicationDto.getParams ();
        boolean insertFlg = true;
        if (params != null && params.size () != 0) {
            insertFlg = this.olqApplicationParamService.insertList (params, pkId);
        }
        return insertFlg;
    }

    /**
     * 检查应用信息唯一性
     *
     * @param name
     * @return
     */
    public boolean checekUniqueName(String name) {
        OlqApplication olqApplication = this.olqApplicationMapper.selectByName (name);
        return olqApplication != null;
    }

    /**
     * 通过名称查找
     *
     * @param name
     * @return
     */
    public OlqApplication selectByName(String name) {
        return this.olqApplicationMapper.selectByName (name);
    }

    /**
     * 通过名称查找完整的信息
     *
     * @param name
     * @return
     */
    public OlqApplicationDto selectFullInfoByName(String name) {
        OlqApplicationDto olqApplicationDto = new OlqApplicationDto ();
        OlqApplication olqApplication = this.olqApplicationMapper.selectByName (name);
        if (olqApplication == null) {
            return null;
        }
        olqApplicationDto.setOlqApplication (olqApplication);
        List<OlqApplicationParam> olqApplicationParams = this.olqApplicationParamService.selectByAppId (olqApplication.getPkId ());
        olqApplicationDto.setParams (olqApplicationParams);
        return olqApplicationDto;
    }

    /**
     * 通过主键查询
     *
     * @param pkId
     * @return
     */
    public OlqApplication select(String pkId) {
        return this.olqApplicationMapper.select (pkId);
    }

    /**
     * 通过主键查询
     *
     * @param pkId
     * @return
     */
    public OlqApplicationDto selectFullAppInfo(String pkId) {
        if (StringUtils.isBlank (pkId)) {
            return null;
        }
        OlqApplicationDto applicationDto = new OlqApplicationDto ();
        OlqApplication olqApplication = this.select (pkId);
        if (olqApplication != null) {
            applicationDto.setOlqApplication (olqApplication);
            List<OlqApplicationParam> olqApplicationParams = this.olqApplicationParamService.selectByAppId (pkId);
            applicationDto.setParams (olqApplicationParams);
        }
        return applicationDto;
    }

    /**
     * 批量删除
     *
     * @param olqApplications
     * @return
     */
    @Transactional
    public boolean delete(OlqApplication[] olqApplications) {
        for (OlqApplication olqApplication : olqApplications) {
            this.olqApplicationMapper.delete (olqApplication.getPkId ());
        }
        return true;
    }

    /**
     * 解析并获取参数集合
     *
     * @param sql
     * @return
     */
    public MessageResult parseParams(String sql) {
        Map<String, Boolean> map = null;
        try {
            map = SqlExpressionEvaluator.parseParamMap (sql);
        } catch (Exception e) {
            return new MessageResult (false, e.getMessage ());
        }
        List<OlqApplicationParam> params = new ArrayList<> ();
        if (map != null && map.size () > 0) {
            for (Map.Entry<String, Boolean> entry : map.entrySet ()) {
                OlqApplicationParam param = new OlqApplicationParam ();
                param.setParamName (entry.getKey ());
                param.setParamDesc (entry.getKey ());
                param.setIsNeed (entry.getValue () ? "0" : "1");
                params.add (param);
            }
        }
        return new MessageResult (true, "解析参数成功！", params);
    }

    /**
     * 查询所有存在的应用
     *
     * @return
     */
    public List<OlqApplication> selectAll() {
        return this.olqApplicationMapper.selectAll ();
    }

    /**
     * 上传配置信息
     *
     * @param uploadFilePath
     * @return
     */
    public MessageResult uploadExcel(String uploadFilePath) {
        File uploadFile = new File (uploadFilePath);
        FileInputStream in = null;
        try {
            ComUploadExcelContent dataSourceContent = new ComUploadExcelContent ();
            dataSourceContent.setClassName ("com.hex.bigdata.udsp.olq.model.OlqApplication");
            List<ComExcelParam> comExcelParams = new ArrayList<> ();
            comExcelParams.add (new ComExcelParam (2, 1, "name"));
            comExcelParams.add (new ComExcelParam (2, 3, "olqDsName"));
            comExcelParams.add (new ComExcelParam (2, 5, "note"));
            comExcelParams.add (new ComExcelParam (3, 1, "describe"));
            comExcelParams.add (new ComExcelParam (3, 3, "olqSql"));
            dataSourceContent.setComExcelParams (comExcelParams);
            List<ComExcelProperties> comExcelPropertiesList = new ArrayList<> ();
            comExcelPropertiesList.add (new ComExcelProperties ("参数字段",
                    "com.hex.bigdata.udsp.olq.model.OlqApplicationParam",
                    10, 0, 1, ComExcelEnums.OlqApplicationParamCoumn.getAllNums ()));
            dataSourceContent.setComExcelPropertiesList (comExcelPropertiesList);
            dataSourceContent.setType ("fixed");
            in = new FileInputStream (uploadFile);
            HSSFWorkbook hfb = new HSSFWorkbook (in);
            HSSFSheet sheet;
            for (int i = 0, activeIndex = hfb.getNumberOfSheets (); i < activeIndex; i++) {
                sheet = hfb.getSheetAt (i);
                Map<String, List> uploadExcelModel = ExcelUploadhelper.getUploadExcelModel (sheet, dataSourceContent);
                List<OlqApplication> olqApplications = (List<OlqApplication>) uploadExcelModel.get ("com.hex.bigdata.udsp.olq.model.OlqApplication");
                OlqApplication olqApplication = olqApplications.get (0);
                //数据合法性检查
                if (StringUtils.isNotBlank (olqApplication.getName ()) && this.selectByName (olqApplication.getName ()) != null) {
                    return new MessageResult (false, "第" + (i + 1) + "个名称已存在！");
                }
                List<OlqApplicationParam> olqApplicationParams = (List<OlqApplicationParam>) uploadExcelModel.get ("com.hex.bigdata.udsp.olq.model.OlqApplicationParam");
                OlqApplicationDto olqApplicationDto = new OlqApplicationDto ();
                olqApplicationDto.setOlqApplication (olqApplication);
                olqApplicationDto.setParams (olqApplicationParams);
                MessageResult messageResult = this.uploadCheck (olqApplicationDto);
                if (!messageResult.isStatus ()) return messageResult;
                olqApplicationDto = (OlqApplicationDto) messageResult.getData ();
                String olqPkId = this.insert (olqApplicationDto);
                if (StringUtils.isBlank (olqPkId)) {
                    return new MessageResult (false, "第" + (i + 1) + "个sheet保存失败！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
            return new MessageResult (false, "程序内部异常" + e.getMessage ());
        } finally {
            if (in != null) {
                try {
                    in.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
        return new MessageResult (true, "上传成功！");
    }


    /**
     * 上传文件内容合法性检查
     *
     * @param olqApplicationDto
     * @return
     */
    public MessageResult uploadCheck(OlqApplicationDto olqApplicationDto) {
        //检查联机查询应用信息合法性校验
        OlqApplication olqApplication = olqApplicationDto.getOlqApplication ();
        String olqDsName = olqApplication.getOlqDsName ();
        if (StringUtils.isBlank (olqDsName)) {
            return new MessageResult (false, "数据源名称不能为空，请检查！");
        }
        //数据源名称
        ComDatasource comDatasource = this.comDatasourceService.selectByModelAndName (DatasourceMode.OLQ.getValue (), olqDsName);
        if (comDatasource == null) {
            return new MessageResult (false, "数据源名称对应的数据源不存在，请检查！");
        }
        olqApplication.setOlqDsId (comDatasource.getPkId ());
        //联机查询应用名称
        String olqName = olqApplication.getName ();
        if (StringUtils.isBlank (olqName)) {
            return new MessageResult (false, "联机查询应用名称不能为空！");
        }
        if (this.selectByName (olqApplication.getName ()) != null) {
            return new MessageResult (false, "联机查询应用名称对应的应用已经存在，请检查！");
        }
        //联机查询应用说明
        String olqDesc = olqApplication.getDescribe ();
        if (StringUtils.isBlank (olqDesc)) {
            return new MessageResult (false, "联机查询应用说明不能为空！");
        }
        //联机查询应用SQL
        String olqSql = olqApplication.getOlqSql ();
        if (StringUtils.isBlank (olqSql)) {
            return new MessageResult (false, "联机查询应用SQL不能为空！");
        }
        List<String> paramNames = null;
        try {
            paramNames = SqlExpressionEvaluator.parseParams2 (olqSql);
        } catch (Exception e) {
            return new MessageResult (false, e.getMessage ());
        }
//        if (paramNames == null || paramNames.size() == 0) {
//            return new MessageResult(false, "应用查询SQL语句中的参数名称为空！");
//        }
        List<OlqApplicationParam> paramObjects = olqApplicationDto.getParams ();
        if ((paramNames == null || paramNames.size () == 0)
                && (paramObjects == null || paramObjects.size () == 0)) {
            //二者都为空
            return new MessageResult (true, "检查完成，数据正确！", olqApplicationDto);
        } else if ((paramNames != null && paramNames.size () > 0)
                && (paramObjects != null || paramObjects.size () > 0)) {
            //二者都不为空
            //检查参数个数是否一致
            if (paramNames.size () != paramObjects.size ()) {
                return new MessageResult (false, "联机查询应用SQL占位符参数个数和参数列表中参数个数不一致，请检查");
            }
            //检查参数名称是否完全一致
            boolean containFlg = true;
            for (OlqApplicationParam param : paramObjects) {
                if (!paramNames.contains (param.getParamName ())) {
                    containFlg = false;
                    break;
                }
            }
            if (!containFlg) {
                return new MessageResult (false, "联机查询应用SQL占位符参数名称和参数列表中参数名称不一致，请检查");
            }
            StringBuffer checkResult = new StringBuffer ("");
            //检查参数列合法性
            for (int i = 0; i < paramObjects.size (); i++) {
                OlqApplicationParam param = paramObjects.get (i);
                int seq = param.getSeq ();
                String paramDesc = param.getParamDesc ();
                if (StringUtils.isBlank (paramDesc)) {
                    checkResult.append ("序号为" + seq + "的参数描述不能为空；");
                }
                String isNeed = param.getIsNeed ();
                if (StringUtils.isBlank (isNeed)) {
                    checkResult.append ("序号为" + seq + "的参数必填的值不能为空；");
                } else if (YesOrNo.YES.getName ().equals (isNeed)) {
                    param.setIsNeed (YesOrNo.YES.getValue ());
                } else if (YesOrNo.NO.getName ().equals (isNeed)) {
                    param.setIsNeed (YesOrNo.NO.getValue ());
                } else {
                    checkResult.append ("序号为" + seq + "的参数是必填的值非法，请输入是或否；");
                }
            }
            if (StringUtils.isNotBlank (checkResult)) {
                return new MessageResult (false, checkResult.toString ());
            }
        } else {
            return new MessageResult (false, "联机查询应用SQL占位符参数个数和参数列表中参数个数不一致，请检查");
        }
        return new MessageResult (true, "检查完成，数据正确！", olqApplicationDto);
    }

    /**
     * 根据应用配置信息、参数值获取sql语句
     *
     * @param olqApplicationDto
     * @param paraMap
     * @return
     */
    public String getExecuteSQL(OlqApplicationDto olqApplicationDto, Map<String, String> paraMap) {
        String sql = olqApplicationDto.getOlqApplication ().getOlqSql ();
        List<OlqApplicationParam> appParams = olqApplicationDto.getParams ();
        if (appParams != null && appParams.size () != 0) {
            sql = SqlExpressionEvaluator.parseSql (sql, paraMap); // 解析字符串并批量替换表达式
        }
        logger.info ("SQL:" + sql);
        return sql;
    }

    /**
     * 检查输入的参数
     */
    public void checkParam(List<OlqApplicationParam> appParams, Map<String, String> paraMap) throws Exception {
        boolean isError = false;
        String message = "";
        int count = 0;
        for (OlqApplicationParam appParam : appParams) {
            String name = appParam.getParamName ();
            String defaultValue = appParam.getDefaultValue (); // 默认值
            String value = (paraMap != null ? paraMap.get (name) : null);
            if (StringUtils.isBlank (value)) { // 没有传入值
                if (paraMap != null && StringUtils.isNotBlank (defaultValue)) { // 有默认值
                    paraMap.put (name, defaultValue);
                } else { // 没有默认值
                    if (EnumTrans.transTrue (appParam.getIsNeed ())) { // 必输
                        isError = true;
                        message += (count == 0 ? "" : ", ") + name;
                        count++;
                    }
                }
            }
        }
        if (isError) {
            throw new Exception (message + "参数不能为空!");
        }
    }

    /**
     * @param olqApplications
     * @return
     */
    public String downloadExcel(OlqApplication[] olqApplications) {
        //查询出olqApplication所有信息
        List<OlqApplicationDto> olqApplicationDtos = new ArrayList<> ();

        for (OlqApplication olqApplication : olqApplications) {
            olqApplicationDtos.add (this.selectFullAppInfo (olqApplication.getPkId ()));
        }

        HSSFWorkbook workbook = new HSSFWorkbook ();
        HSSFWorkbook sourceWork;
        HSSFSheet sourceSheet = null;

        String seprator = FileUtil.getFileSeparator ();
        // 模板文件位置
        String templateFile = ExcelCopyUtils.templatePath + seprator + "downLoadTemplate_olqApplication.xls";
        // 下载地址
        String dirPath = CreateFileUtil.getLocalDirPath ();
        dirPath += seprator + "download_olqApplication_excel_" + DateUtil.format (new Date (), "yyyyMMddHHmmss") + ".xls";
        // 获取模板文件第一个Sheet对象
        POIFSFileSystem sourceFile = null;

        try {
            sourceFile = new POIFSFileSystem (new FileInputStream (templateFile));
            sourceWork = new HSSFWorkbook (sourceFile);
            sourceSheet = sourceWork.getSheetAt (0);
        } catch (IOException e) {
            e.printStackTrace ();
        }

        List<ComExcelParam> comExcelParams = new ArrayList<> ();
        comExcelParams.add (new ComExcelParam (2, 1, "name"));
        comExcelParams.add (new ComExcelParam (2, 3, "olqDsName"));
        comExcelParams.add (new ComExcelParam (2, 5, "note"));
        comExcelParams.add (new ComExcelParam (3, 1, "describe"));
        comExcelParams.add (new ComExcelParam (3, 3, "olqSql"));

        for (OlqApplicationDto olqApplicationDto : olqApplicationDtos) {
            this.setWorkbookSheet (workbook, sourceSheet, comExcelParams, olqApplicationDto);
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
        HSSFWorkbook sourceWork = null;
        HSSFSheet sourceSheet = null;
        try {
            sourceFile = new POIFSFileSystem (new FileInputStream (templateFile));
            sourceWork = new HSSFWorkbook (sourceFile);
            // 联机查询应用为第3个sheet
            sourceSheet = sourceWork.getSheetAt (2);
        } catch (IOException e) {
            e.printStackTrace ();
        }

        OlqApplicationDto olqApplicationDto = this.selectFullAppInfo (appId);

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

        this.setWorkbookSheetPart (sheet, olqApplicationDto, sourceSheet, workbook, new OlqIndexDto (i));

        return workbook;
    }

    private void setWorkbookSheet(HSSFWorkbook workbook, HSSFSheet sourceSheet, List<ComExcelParam> comExcelParams, OlqApplicationDto olqApplicationDto) {

        OlqApplication olqApp = olqApplicationDto.getOlqApplication ();
        olqApp = select(olqApp.getPkId ());
        ComDatasource comDs = comDatasourceService.select (olqApp.getOlqDsId ());
        olqApp.setOlqDsName (comDs.getName ());
        HSSFSheet sheet = workbook.createSheet (olqApp.getName ());

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
                Field field = olqApp.getClass ().getDeclaredField (comExcelParam.getName ());
                field.setAccessible (true);
                ExcelCopyUtils.setCellValue (sheet, comExcelParam.getRowNum (), comExcelParam.getCellNum (),
                        field.get (olqApp) == null ? "" : field.get (olqApp).toString ());
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }

        this.setWorkbookSheetPart (sheet, olqApplicationDto, sourceSheet, workbook, new OlqIndexDto (i));
    }

    private void setWorkbookSheetPart(HSSFSheet sheet, OlqApplicationDto olqApplicationDto, HSSFSheet sourceSheet, HSSFWorkbook workbook, OlqIndexDto olqIndexDto) {
        HSSFRow row;
        HSSFCell cell;
        int rowIndex = olqIndexDto.getRowIndex ();
        List<OlqApplicationParam> olqApplicationParams = olqApplicationDto.getParams ();
        if (olqApplicationParams != null && olqApplicationParams.size () > 0) {
            for (OlqApplicationParam applicationParam : olqApplicationParams) {
                row = sheet.createRow (rowIndex);
                cell = row.createCell (0);
                cell.setCellValue (applicationParam.getSeq ());
                cell = row.createCell (1);
                cell.setCellValue (applicationParam.getParamName ());
                cell = row.createCell (2);
                cell.setCellValue (applicationParam.getParamDesc ());
                cell = row.createCell (3);
                cell.setCellValue (applicationParam.getDefaultValue ());
                cell = row.createCell (4);
                if ("0".equals (applicationParam.getIsNeed ())) {
                    cell.setCellValue ("是");
                } else {
                    cell.setCellValue ("否");
                }
                rowIndex++;
            }
        }
    }

    public List<ComDatasource> selectOlqDataSource() {
        ComDatasourceView datasourceView = new ComDatasourceView ();
        datasourceView.setModel (DatasourceMode.OLQ.getValue ());
        List<ComDatasource> searchList = comDatasourceService.select (datasourceView);
        return searchList;
    }


    public static void main(String[] args) {

    }
}
