package com.hex.bigdata.udsp.im.service;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.common.constant.ComExcelEnums;
import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.common.constant.Operator;
import com.hex.bigdata.udsp.common.dao.ComDatasourceMapper;
import com.hex.bigdata.udsp.common.model.*;
import com.hex.bigdata.udsp.common.service.ComPropertiesService;
import com.hex.bigdata.udsp.common.util.*;
import com.hex.bigdata.udsp.im.constant.*;
import com.hex.bigdata.udsp.im.converter.model.*;
import com.hex.bigdata.udsp.im.dao.*;
import com.hex.bigdata.udsp.im.dto.ImIndexDto;
import com.hex.bigdata.udsp.im.dto.ImModelDto;
import com.hex.bigdata.udsp.im.dto.ImModelView;
import com.hex.bigdata.udsp.im.model.*;
import com.hex.goframe.dao.GFDictMapper;
import com.hex.goframe.model.Page;
import com.hex.goframe.util.DateUtil;
import com.hex.goframe.util.FileUtil;
import com.hex.goframe.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
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
import java.text.ParseException;
import java.util.*;

/**
 * 交互建模处理类
 * Created by jintian on 2017/9/6.
 */
@Service
public class ImModelService {

    private static final FastDateFormat format8 = FastDateFormat.getInstance("yyyyMMdd");
    private static final FastDateFormat format10 = FastDateFormat.getInstance("yyyy-MM-dd");
    private static final FastDateFormat format17 = FastDateFormat.getInstance("yyyyMMdd HH:mm:ss");
    private static final FastDateFormat format19 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ImModelMapper imModelMapper;
    @Autowired
    private ImModelFilterColMapper imModelFilterColMapper;
    @Autowired
    private ImModelMappingMapper imModelMappingMapper;
    @Autowired
    private ComPropertiesService comPropertiesService;
    @Autowired
    private ImModelUpdateKeyMapper imModelUpdateKeyMapper;
    @Autowired
    private ImConverterService imConverterService;
    @Autowired
    private ImMetadataMapper imMetadataMapper;
    @Autowired
    private ComDatasourceMapper comDatasourceMapper;
    @Autowired
    private GFDictMapper gfDictMapper;
    @Autowired
    private ImMetadataColMapper imMetadataColMapper;

    private static List<ComExcelParam> comExcelParams;

    static {
        comExcelParams = new ArrayList<>();
        comExcelParams.add(new ComExcelParam(1, 1, "name"));
        comExcelParams.add(new ComExcelParam(1, 3, "sourceDsId"));
        comExcelParams.add(new ComExcelParam(2, 1, "describe"));
        comExcelParams.add(new ComExcelParam(2, 3, "targetMdId"));
        comExcelParams.add(new ComExcelParam(3, 1, "note"));
        comExcelParams.add(new ComExcelParam(5, 1, "updateMode"));
        comExcelParams.add(new ComExcelParam(5, 3, "updateKey"));
        comExcelParams.add(new ComExcelParam(6, 1, "buildMode"));
        comExcelParams.add(new ComExcelParam(6, 3, "engineDsId"));
    }

    @Transactional
    public String insert(ImModelDto imModelViews) throws Exception {
        String pkId = Util.uuid();
        ImModel model = imModelViews.getImModel();
        model.setPkId(pkId);
        if ("1".equals(model.getType())) { // 批量
            model.setStatus("1"); // 未建
        } else if ("2".equals(model.getType())) { // 实时
            model.setStatus("2"); // 已建
        }
        if (!imModelMapper.insert(pkId, model)) {
            throw new RuntimeException("保存【配置栏基础】信息异常");
        }
        List<ImModelFilterCol> imModelFilterCols = imModelViews.getImModelFilterCols();
        if (imModelFilterCols != null && !imModelFilterColMapper.insertFilterCols(pkId, imModelFilterCols)) {
            throw new RuntimeException("保存【过滤字段】异常");
        }
        List<ImModelMapping> imModelMappings = imModelViews.getImModelMappings();
        if (imModelMappings != null && !imModelMappingMapper.insertModelMappings(pkId, imModelMappings)) {
            throw new RuntimeException("保存【映射字段】异常");
        }
        List<ComProperties> comPropertiesList = imModelViews.getComPropertiesList();
        if (comPropertiesList != null && !comPropertiesService.insertList(pkId, comPropertiesList)) {
            throw new RuntimeException("保存【参数配置栏】信息异常");
        }
        //添加更新字段
        String updateKeys = model.getUpdateKey();
        if (StringUtils.isNotBlank(updateKeys)) {
            String[] updateKeyArray = updateKeys.split(",");
            Map<String, ImModelMapping> map = new HashMap<>();
            for (ImModelMapping imModelMapping : imModelMappings) {
                map.put(imModelMapping.getColId(), imModelMapping);
            }
            for (String updateKey : updateKeyArray) {
                if (map.get(updateKey) == null) {
                    ImMetadataCol imMetadataCol = imMetadataColMapper.select(updateKey);
                    throw new IllegalArgumentException("【更新主键】中的字段：[" + imMetadataCol.getName() + "]不在【映射字段】中，请检查并修改后再保存！");
                }
            }
            for (String updateKey : updateKeyArray) {
                ImModelUpdateKey imModelUpdateKey = new ImModelUpdateKey();
                String updateKeyId = Util.uuid();
                imModelUpdateKey.setPkId(updateKeyId);
                imModelUpdateKey.setModelId(pkId);
                imModelUpdateKey.setColId(updateKey);
                if (!imModelUpdateKeyMapper.insert(updateKeyId, imModelUpdateKey)) {
                    throw new RuntimeException("添加【更新主键】异常");
                }
            }
        }
        return pkId;
    }

    @Transactional
    public boolean update(ImModelDto imModelViews) throws Exception {
        ImModel model = imModelViews.getImModel();
        //获取模型主键
        String pkId = model.getPkId();
        if ("1".equals(model.getType())) { // 批量
            model.setStatus("1"); // 未建
        } else if ("2".equals(model.getType())) { // 实时
            model.setStatus("2"); // 已建
        }
        if (!imModelMapper.update(pkId, model)) {
            //添加运行时异常，使事务回滚
            throw new RuntimeException("更新【配置栏基础】信息异常");
        }
        List<ImModelFilterCol> imModelFilterCols = imModelViews.getImModelFilterCols();
        if (!imModelFilterColMapper.deleteList(pkId) || !imModelFilterColMapper.insertFilterCols(pkId, imModelFilterCols)) {
            throw new RuntimeException("更新【过滤字段】异常");
        }
        List<ImModelMapping> imModelMappings = imModelViews.getImModelMappings();
        if (!imModelMappingMapper.deleteList(pkId) || !imModelMappingMapper.insertModelMappings(pkId, imModelMappings)) {
            throw new RuntimeException("更新【映射字段】异常");
        }
        List<ComProperties> comPropertiesList = imModelViews.getComPropertiesList();
        if (!comPropertiesService.deleteList(pkId) || !comPropertiesService.insertList(pkId, comPropertiesList)) {
            throw new RuntimeException("更新【参数配置栏】信息异常");
        }

        if (!imModelUpdateKeyMapper.deleteList(pkId)) {
            throw new RuntimeException("删除【更新主键】异常");
        }
        //添加更新字段
        String updateKeys = model.getUpdateKey();
        if (StringUtils.isNotBlank(updateKeys)) {
            String[] updateKeyArray = updateKeys.split(",");
            Map<String, ImModelMapping> map = new HashMap<>();
            for (ImModelMapping imModelMapping : imModelMappings) {
                map.put(imModelMapping.getColId(), imModelMapping);
            }
            for (String updateKey : updateKeyArray) {
                if (map.get(updateKey) == null) {
                    ImMetadataCol imMetadataCol = imMetadataColMapper.select(updateKey);
                    throw new IllegalArgumentException("【更新主键】中的字段：[" + imMetadataCol.getName() + "]不在【映射字段】中，请检查并修改后再保存！");
                }
            }
            for (String updateKey : updateKeyArray) {
                ImModelUpdateKey imModelUpdateKey = new ImModelUpdateKey();
                String updateKeyId = Util.uuid();
                imModelUpdateKey.setPkId(updateKeyId);
                imModelUpdateKey.setModelId(pkId);
                imModelUpdateKey.setColId(updateKey);
                if (!imModelUpdateKeyMapper.insert(updateKeyId, imModelUpdateKey)) {
                    throw new RuntimeException("更新【更新主键】异常");
                }
            }
        }
        return true;
    }

    public List<ImModelView> select(ImModelView imModelView, Page page) {
        return imModelMapper.select(imModelView, page);
    }

    public boolean delete(ImModel[] imModels) throws Exception {
        for (ImModel imModel : imModels) {
            imModel.setDelFlg("1");
            // 进行逻辑删除
            if (!imModelMapper.updateModelDelStatus(imModel.getPkId(), "1")) {
                //跑出异常，进行回滚
                throw new RuntimeException("删除失败,删除失败的模型名称为：" + imModel.getName());
            }
        }
        return true;
    }

    public ImModel select(String pkId) {
        return imModelMapper.select(pkId);
    }

    public ImModel selectByName(String modelName) {
        return imModelMapper.selectByName(modelName);
    }

    public List<MetadataCol> getSrcMetadata(Property[] properties, String srcDataSourceId) {
        //根据元数据获取相关信息
        Model model = new Model(Arrays.asList(properties));
        ComDatasource comDatasource = comDatasourceMapper.select(srcDataSourceId);
        List<ComProperties> comProperties = comPropertiesService.selectList(srcDataSourceId);
        Datasource datasource = DatasourceUtil.getDatasource(comDatasource, comProperties);
        // 由于该实现类和模型中的实现类不一样故制为空
        datasource.setImplClass("");
        model.setProperties(Arrays.asList(properties));
        model.setSourceDatasource(datasource);
        return imConverterService.getCloumnInfo(model);
    }

    public List<ImModel> selectAll() {
        return imModelMapper.selectAll();
    }

    @Transactional
    public Map<String, String> uploadExcel(String uploadFilePath) {
        Map resultMap = new HashMap<String, String>(2);
        File uploadFile = new File(uploadFilePath);
        FileInputStream in = null;
        int i = 0;
        try {
            ComUploadExcelContent dataSourceContent = new ComUploadExcelContent();
            dataSourceContent.setClassName("com.hex.bigdata.udsp.im.model.ImModel");

            dataSourceContent.setComExcelParams(comExcelParams);
            List<ComExcelProperties> comExcelPropertiesList = new ArrayList<>();
            comExcelPropertiesList.add(new ComExcelProperties("配置栏",
                    "com.hex.bigdata.udsp.common.model.ComProperties",
                    12, 0, 1, ComExcelEnums.Comproperties.getAllNums()));
            comExcelPropertiesList.add(new ComExcelProperties("字段映射",
                    "com.hex.bigdata.udsp.im.model.ImModelMapping",
                    10, 0, 2, ComExcelEnums.ImMapping.getAllNums()));
            comExcelPropertiesList.add(new ComExcelProperties("字段过滤",
                    "com.hex.bigdata.udsp.im.model.ImModelFilterCol",
                    10, 0, 3, ComExcelEnums.ImModelFiterCol.getAllNums()));

            dataSourceContent.setComExcelPropertiesList(comExcelPropertiesList);
            dataSourceContent.setType("fixed");

            in = new FileInputStream(uploadFile);
            HSSFWorkbook hfb = new HSSFWorkbook(in);
            HSSFSheet sheet;

            Page page = new Page();
            page.setPageIndex(0);
            page.setPageSize(1);
            for (int activeIndex = hfb.getNumberOfSheets(); i < activeIndex; i++) {
                sheet = hfb.getSheetAt(i);
                ImModelDto imModelDto = new ImModelDto();
                Map<String, List> uploadExcelModel = ExcelUploadhelper.getUploadExcelModel(sheet, dataSourceContent);
                List<ImModel> imModels = (List<ImModel>) uploadExcelModel.get("com.hex.bigdata.udsp.im.model.ImModel");
                ImModel imModel = imModels.get(0);
                if (imModelMapper.selectByName(imModel.getName()) != null) {
                    resultMap.put("status", "false");
                    resultMap.put("message", "第" + (i + 1) + "个名称已存在！");
                    break;
                }
                /*//更改模型源信息  单独获取模块名称
                String modelName = ExcelUploadhelper.getCellValue(sheet,7,1);*/
                imModel.setSourceDsId(comDatasourceMapper.selectByModelAndName("IM", imModel.getSourceDsId()).getPkId());
                //更改目标元数据
                imModel.setTargetMdId(imMetadataMapper.selectByName(imModel.getTargetMdId()).getPkId());
                //更改引擎数据源
                imModel.setEngineDsId(comDatasourceMapper.selectByModelAndName("IM", imModel.getEngineDsId()).getPkId());

                //更改更新策略
                if (StringUtils.isNotBlank(imModel.getUpdateMode())) {
                    imModel.setUpdateMode(imModelMapper.getModelUpdateModeByName(imModel.getUpdateMode()));
                }

                //更改构建策略
                if (StringUtils.isNotBlank(imModel.getBuildMode())) {
                    imModel.setBuildMode(imModelMapper.getModelBuildModeByName(imModel.getBuildMode()));
                }

                //设置模型类型
                if (comDatasourceMapper.checkSourceType(imModel.getSourceDsId())) {
                    imModel.setType("2");
                } else {
                    imModel.setType("1");
                }

                //设置模型状态
                imModel.setStatus("1");
                //设置模型
                imModelDto.setImModel(imModel);
                //获取参数内容
                List<ComProperties> comPropertiesList = (List<ComProperties>) uploadExcelModel.get("com.hex.bigdata.udsp.common.model.ComProperties");
                imModelDto.setComPropertiesList(comPropertiesList);
                //获取映射内容
                List<ImModelMapping> imModelMappings = (List<ImModelMapping>) uploadExcelModel.get("com.hex.bigdata.udsp.im.model.ImModelMapping");
                //遍历更改目标字段对应的字段pkId
                if (imModelMappings != null) {
                    for (ImModelMapping imModelMapping : imModelMappings) {
                        imModelMapping.setColId(imMetadataColMapper.selectByNameAndMdId(imModel.getTargetMdId(), imModelMapping.getColId()).getPkId());
                        //设置映射的存储默认值为0
                        imModelMapping.setStored("否".equals(imModelMapping.getStored()) ? "1" : "0");
                    }
                }
                imModelDto.setImModelMappings(imModelMappings);
                //获取过滤字段内容
                List<ImModelFilterCol> imModelFilterCols = (List<ImModelFilterCol>) uploadExcelModel.get("com.hex.bigdata.udsp.im.model.ImModelFilterCol");
                imModelDto.setImModelFilterCols(imModelFilterCols);
                String pkId = insert(imModelDto);

                //更改主键
                String updateKey = imModel.getUpdateKey();
                if (StringUtils.isNotBlank(updateKey)) {
                    String[] updateKeyNames = updateKey.split(",");

                    String updateKeyPkId;
                    ImModelUpdateKey imModelUpdateKey = new ImModelUpdateKey();
                    imModelUpdateKey.setModelId(pkId);
                    for (String updateKeyName : updateKeyNames) {
                        updateKeyPkId = Util.uuid();
                        imModelUpdateKey.setModelId(updateKeyPkId);
                        imModelUpdateKey.setColId(imMetadataColMapper.selectByNameAndMdId(imModel.getTargetMdId(), updateKeyName).getPkId());
                        imModelUpdateKeyMapper.insert(updateKeyPkId, imModelUpdateKey);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", "false");
            resultMap.put("message", "第" + i + "个报错异常,错误信息：" + e.getMessage());
            throw new RuntimeException(e.getMessage());
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

    public String createExcel(ImModel[] imModels) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFWorkbook sourceWork = null;
        HSSFSheet sourceSheet = null;
        HSSFRow row;
        HSSFCell cell;
        String seprator = FileUtil.getFileSeparator();
        // 模板文件位置
        String templateFile = ExcelCopyUtils.templatePath + seprator + "downLoadTemplate_imModel.xls";
        // 下载地址
        String dirPath = CreateFileUtil.getLocalDirPath();
        dirPath += seprator + "download_imModel_excel_" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".xls";
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
        for (ImModel imModel : imModels) {
            imModel = imModelMapper.select(imModel.getPkId());
            sheet = workbook.createSheet(imModel.getName ());

            //将前面样式内容复制到下载表中
            int i = 0;
            for (; i < 12; i++) {
                try {
                    ExcelCopyUtils.copyRow(sheet.createRow(i), sourceSheet.getRow(i), sheet.createDrawingPatriarch(), workbook);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //设置引擎数据源
            if (StringUtils.isNotBlank(imModel.getEngineDsId())) {
                imModel.setEngineDsId(comDatasourceMapper.select(imModel.getEngineDsId()).getName());
            }
            //设置源数据名称
            ComDatasource comDatasource = comDatasourceMapper.select(imModel.getSourceDsId());
            imModel.setSourceDsId(comDatasource.getName());
            /*//设置源数据模块名称 单独设置
            ExcelCopyUtils.setCellValue(sheet,7,7,comDatasource.getModel());*/

            //设置目标元数据名称
            imModel.setTargetMdId(imMetadataMapper.select(imModel.getTargetMdId()).getName());
            //设置更新策略,不为空时更新
            if (StringUtils.isNotBlank(imModel.getUpdateMode())) {
                imModel.setUpdateMode(gfDictMapper.selectByPrimaryKey("IM_MODEL_UPDATE_TYPE", imModel.getUpdateMode()).getDictName());
            }
            //设置构建策略
            if (StringUtils.isNotBlank(imModel.getBuildMode())) {
                imModel.setBuildMode(gfDictMapper.selectByPrimaryKey("IM_MODEL_BUILD_TYPE", imModel.getBuildMode()).getDictName());
            }
            //设置更新主键
            List<ImMetadataCol> imMetadataCols = imMetadataColMapper.selectModelUpdateKeys(imModel.getPkId());
            StringBuffer updateKey = new StringBuffer("");
            for (int k = 0, len = imMetadataCols.size(); k < len; k++) {
                updateKey.append(imMetadataCols.get(k).getName());
                //不为最后一个时
                if (k != len - 1) {
                    updateKey.append(",");
                }
            }
            imModel.setUpdateKey(updateKey.toString());

            for (ComExcelParam comExcelParam : comExcelParams) {
                try {
                    Field field = imModel.getClass().getDeclaredField(comExcelParam.getName());
                    field.setAccessible(true);
                    ExcelCopyUtils.setCellValue(sheet, comExcelParam.getRowNum(), comExcelParam.getCellNum(), field.get(imModel) == null ? "" : field.get(imModel).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //设置参数栏
            List<ComProperties> comProperties = comPropertiesService.selectList(imModel.getPkId());
            if (comProperties.size() > 0) {
                int k = 1;
                for (ComProperties comProperty : comProperties) {
                    row = sheet.createRow(i);
                    cell = row.createCell(0);
                    cell.setCellValue(k);
                    cell = row.createCell(1);
                    cell.setCellValue(comProperty.getName());
                    cell = row.createCell(2);
                    cell.setCellValue(comProperty.getValue());
                    cell = row.createCell(3);
                    cell.setCellValue(comProperty.getDescribe());
                    i++;
                    k++;
                }
            }

            //设置字段映射栏
            try {
                ExcelCopyUtils.copyRow(sheet.createRow(i++), sourceSheet.getRow(16), sheet.createDrawingPatriarch(), workbook);
                ExcelCopyUtils.copyRow(sheet.createRow(i++), sourceSheet.getRow(17), sheet.createDrawingPatriarch(), workbook);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<ImModelMapping> imModelMappings = imModelMappingMapper.selectByModelId(imModel.getPkId());
            if (imModelMappings.size() > 0) {
                for (ImModelMapping imModelMapping : imModelMappings) {
                    row = sheet.createRow(i);
                    cell = row.createCell(0);
                    cell.setCellValue(imModelMapping.getSeq());
                    cell = row.createCell(1);
                    cell.setCellValue(imModelMapping.getName());
                    cell = row.createCell(2);
                    cell.setCellValue(imModelMapping.getColId());
                    cell = row.createCell(3);
                    cell.setCellValue(imModelMapping.getType());
                    cell = row.createCell(4);
                    cell.setCellValue(imModelMapping.getLength());
                    cell = row.createCell(5);
                    cell.setCellValue(imModelMapping.getDescribe());
                    cell = row.createCell(6);
                    cell.setCellValue("1".equals(imModelMapping.getIndexed()) ? "否" : "是");
                    cell = row.createCell(7);
                    cell.setCellValue("1".equals(imModelMapping.getPrimary()) ? "否" : "是");
                    cell = row.createCell(8);
                    cell.setCellValue("1".equals(imModelMapping.getStored()) ? "否" : "是");

                    cell = row.createCell(9);
                    cell.setCellValue(imModelMapping.getNote());
                    i++;
                }
            }

            //设置字段过滤
            try {
                ExcelCopyUtils.copyRow(sheet.createRow(i++), sourceSheet.getRow(21), sheet.createDrawingPatriarch(), workbook);
                ExcelCopyUtils.copyRow(sheet.createRow(i++), sourceSheet.getRow(22), sheet.createDrawingPatriarch(), workbook);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<ImModelFilterCol> imModelFilterCols = imModelFilterColMapper.selectList(imModel.getPkId());
            if (imModelFilterCols.size() > 0) {
                for (ImModelFilterCol imModelFilterCol : imModelFilterCols) {
                    row = sheet.createRow(i);
                    cell = row.createCell(0);
                    cell.setCellValue(imModelFilterCol.getSeq());
                    cell = row.createCell(1);
                    cell.setCellValue(imModelFilterCol.getName());
                    cell = row.createCell(2);
                    cell.setCellValue(imModelFilterCol.getDescribe());
                    cell = row.createCell(3);
                    cell.setCellValue(imModelFilterCol.getType());
                    cell = row.createCell(4);
                    cell.setCellValue(imModelFilterCol.getLength());
                    cell = row.createCell(5);
                    cell.setCellValue(imModelFilterCol.getIsNeed().equals("1") ? "否" : "是");
                    cell = row.createCell(6);
                    cell.setCellValue(imModelFilterCol.getOperator());
                    cell = row.createCell(7);
                    cell.setCellValue(imModelFilterCol.getDefaultVal());
                    cell = row.createCell(8);
                    cell.setCellValue(imModelFilterCol.getLabel());
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

    public boolean updateModelDelStatus(String pkId, String status) {
        return imModelMapper.updateModelDelStatus(pkId, status);
    }

    public boolean updateStatus(ImModel imModel, String status) throws Exception {
        boolean result = false;
        //组织需要构建或则删除构建的模型
        Model model = getModelByImModel(imModel);
        if ("1".equals(status)) { // 删除模型
            imConverterService.dropEngineSchema(model);
        } else if ("2".equals(status)) { // 创建模型
            imConverterService.createEngineSchema(model);
        }
        //修改数据库中建模的状态
        imModel.setStatus(status);
        synchronized (imModel.getPkId()) {
            result = imModelMapper.update(imModel.getPkId(), imModel);
        }
        return result;
    }

    public Model getModel(String pkId, ModelFilterCol[] modelFilterCols) throws Exception {
        Model model = getModelByImModel(select(pkId));
        //设置其过滤字段
        if (modelFilterCols != null) {
            for (ModelFilterCol modelFilterCol : modelFilterCols) {
                for (ModelFilterCol modelFilterCol1 : model.getModelFilterCols()) {
                    if (modelFilterCol1.getLabel().equals(modelFilterCol.getLabel())) {
                        modelFilterCol1.setValue(modelFilterCol.getValue());
                    }
                }
            }
        }
        return model;
    }

    public Model getModel(String pkId, Map<String, String> datas) throws Exception {
        Model model = getModelByImModel(select(pkId));
        for (ModelFilterCol modelFilterCol : model.getModelFilterCols()) {
            boolean isNeed = modelFilterCol.isNeed();
            DataType type = modelFilterCol.getType();
            int length = getLen(modelFilterCol.getLength());
            String label = modelFilterCol.getLabel();
            String value = (datas != null ? datas.get(label) : null);
            if (StringUtils.isNotBlank(value)) {
                if (DataType.TIMESTAMP.equals(type)) { // 字段类型是TIMESTAMP
                    value = tarnDateStr(length, value); // 日期格式转换
                }
            }
            if (isNeed) { // 必填
                if (StringUtils.isBlank(value)) {
                    throw new IllegalArgumentException(label + "不能为空");
                }
                modelFilterCol.setValue(value);
            } else { // 选填
                if (StringUtils.isNotBlank(value)) {
                    modelFilterCol.setValue(value);
                }
            }
        }
        return model;
    }

    private int getLen(String length) {
        int len = 0;
        if (StringUtils.isNotBlank(length) && StringUtils.isNumeric(length)) {
            len = Integer.valueOf(length);
        }
        return len;
    }

    private String tarnDateStr(int length, String value) {
        if (length == 8 || length == 10 || length == 17 || length == 19) {
            Date date = strToDate(value);
            if (date != null) {
                if (length == 8) {
                    value = format8.format(date);
                } else if (length == 10) {
                    value = format10.format(date);
                } else if (length == 17) {
                    value = format17.format(date);
                } else if (length == 19) {
                    value = format19.format(date);
                }
            }
        }
        return value;
    }

    private Date strToDate(String dataStr) {
        Date date = null;
        try {
            if (dataStr.length() == 8) {
                date = format8.parse(dataStr);
            } else if (dataStr.length() == 10) {
                date = format10.parse(dataStr.replaceAll("/", "-"));
            } else if (dataStr.length() == 17) {
                date = format17.parse(dataStr);
            } else if (dataStr.length() == 19) {
                date = format19.parse(dataStr.replaceAll("/", "-"));
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期字段传入的不是日期格式字符串参数");
        }
        return date;
    }

    //通过模型id获取模型
    public Model getModelByImModel(ImModel imModel) throws Exception {
        String pkId = imModel.getPkId();
        //修改comProperties为继承property类比较好,设计有问题，冗余
        List<ComProperties> properties = comPropertiesService.selectList(pkId);
        List<Property> propertyList = new ArrayList<>(properties.size());
        Property property;
        for (ComProperties comProperties : properties) {
            property = new Property();
            property.setDescribe(comProperties.getDescribe());
            property.setName(comProperties.getName());
            property.setValue(comProperties.getValue());
            propertyList.add(property);
        }
        Model model = new Model(propertyList);
        //设置源数据源
        model.setSourceDatasource(getDatasourceById(imModel.getSourceDsId()));
        //设置一些基础信息
        transformModel(model, imModel);
        //设置目标数据元信息
        model.setTargetMetadata(getMetadataById(imModel.getTargetMdId()));
        //设置引擎数据源信息
        model.setEngineDatasource(getDatasourceById(imModel.getEngineDsId()));
        //设置更新键值
        List<ImMetadataCol> imMetadataCols = imMetadataColMapper.selectModelUpdateKeys(pkId);
        List<MetadataCol> metadataCols = new ArrayList<>();
        MetadataCol metadataCol;
        for (ImMetadataCol imMetadataCol : imMetadataCols) {
            metadataCol = new MetadataCol();
            transformMetaCol(imMetadataCol, metadataCol);
            metadataCols.add(metadataCol);
        }
        model.setUpdateKeys(metadataCols);
        //设置字段映射
        List<ModelMapping> modelMappings = new ArrayList<>();
        List<ImModelMapping> imModelMappings = imModelMappingMapper.selectList(pkId);
        ModelMapping modelMapping;
        for (ImModelMapping imModelMapping : imModelMappings) {
            metadataCol = new MetadataCol();
            modelMapping = new ModelMapping();
            transformModelMapping(imModelMapping, modelMapping);
            //获取映射的目标字段信息
            ImMetadataCol imMetadataCol = imMetadataColMapper.select(imModelMapping.getColId());
            //如果出错则将异常抛出来
            transformMetaCol(imMetadataCol, metadataCol);
            modelMapping.setMetadataCol(metadataCol);
            modelMappings.add(modelMapping);
        }
        model.setModelMappings(modelMappings);
        //设置过滤字段集合
        List<ImModelFilterCol> imModelFilterCols = imModelFilterColMapper.selectList(pkId);
        List<ModelFilterCol> modelFilterCols = new ArrayList<>();
        ModelFilterCol modelFilterCol;
        for (ImModelFilterCol imModelFilterCol : imModelFilterCols) {
            modelFilterCol = new ModelFilterCol();
            transformModelFilterCol(imModelFilterCol, modelFilterCol);
            modelFilterCols.add(modelFilterCol);
        }
        model.setModelFilterCols(modelFilterCols);
        return model;
    }

    private Metadata getMetadataById(String mdId) throws Exception {

        List<ComProperties> comProperties = comPropertiesService.selectList(mdId);
        Metadata metadata = new Metadata(PropertyUtil.convertToPropertyList(comProperties));
        //设置metadata的基础信息
        ImMetadata imMetadata = imMetadataMapper.select(mdId);
        metadata.setDatasource(getDatasourceById(imMetadata.getDsId()));
        metadata.setDescribe(imMetadata.getDescribe());
        metadata.setName(imMetadata.getName());
        metadata.setNote(imMetadata.getNote());

        if ("1".equals(imMetadata.getStatus())) {
            metadata.setStatus(MetadataStatus.CREATED);
        } else if ("2".equals(imMetadata.getStatus())) {
            metadata.setStatus(MetadataStatus.NO_CREATED);
        }

        metadata.setTbName(imMetadata.getTbName());

        if ("0".equals(imMetadata.getType())) {
            metadata.setType(MetadataType.INTERIOR);
        } else if ("1".equals(imMetadata.getType())) {
            metadata.setType(MetadataType.EXTERNAL);
        }

        List<MetadataCol> metadataCols = new ArrayList<>();
        ImMetadataCol imMetadataCol1 = new ImMetadataCol();
        imMetadataCol1.setMdId(mdId);
        List<ImMetadataCol> imMetadataCols = imMetadataColMapper.select(imMetadataCol1);
        MetadataCol metadataCol;
        for (ImMetadataCol imMetadataCol : imMetadataCols) {
            metadataCol = new MetadataCol();
            transformMetaCol(imMetadataCol, metadataCol);
            metadataCols.add(metadataCol);
        }
        metadata.setMetadataCols(metadataCols);
        return metadata;
    }

    //模型基础信息转换
    private void transformModel(Model model, ImModel imModel) throws Exception {
        model.setId(imModel.getPkId());
        model.setName(imModel.getName());
        model.setDescribe(imModel.getDescribe());
        if ("1".equals(imModel.getBuildMode())) {
            model.setBuildMode(BuildMode.INSERT_INTO);
        } else if ("2".equals(imModel.getBuildMode())) {
            model.setBuildMode(BuildMode.INSERT_OVERWRITE);
        }
        model.setNote(imModel.getNote());
        if ("2".equals(imModel.getStatus())) {
            model.setStatus(ModelStatus.CREATED);
        } else if ("1".equals(imModel.getStatus())) {
            model.setStatus(ModelStatus.NO_CREATED);
        }
        if ("1".equals(imModel.getType())) {
            model.setType(ModelType.BATCH);
        } else if ("2".equals(imModel.getType())) {
            model.setType(ModelType.REALTIME);
        }
        if ("1".equals(imModel.getUpdateMode())) {
            model.setUpdateMode(UpdateMode.MATCHING_UPDATE);
        } else if ("2".equals(imModel.getUpdateMode())) {
            model.setUpdateMode(UpdateMode.UPDATE_INSERT);
        } else if ("3".equals(imModel.getUpdateMode())) {
            model.setUpdateMode(UpdateMode.INSERT_INTO);
        }
    }

    private Datasource getDatasourceById(String id) throws Exception {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        ComDatasource comDatasource = comDatasourceMapper.select(id);
        List<ComProperties> comProperties = comPropertiesService.selectList(id);
        return DatasourceUtil.getDatasource(comDatasource, comProperties);
    }

    //元数据字段信息转换
    private void transformMetaCol(ImMetadataCol imMetadataCol, MetadataCol metadataCol) throws Exception {
        if (imMetadataCol != null) {
            metadataCol.setSeq(imMetadataCol.getSeq());
            metadataCol.setDescribe(imMetadataCol.getDescribe());
            metadataCol.setIndexed(imMetadataCol.getIndexed().equals("0"));
            metadataCol.setName(imMetadataCol.getName());
            metadataCol.setType(DataType.valueOf(imMetadataCol.getType()));
            metadataCol.setNote(imMetadataCol.getNote());
            metadataCol.setStored(imMetadataCol.getStored().equals("0"));
            metadataCol.setPrimary(imMetadataCol.getPrimary().equals("0"));
            metadataCol.setLength(imMetadataCol.getLength());
        }
    }

    //映射字段转换
    private void transformModelMapping(ImModelMapping imModelMapping, ModelMapping modelMapping) throws Exception {
        modelMapping.setName(imModelMapping.getName());
        modelMapping.setDescribe(imModelMapping.getDescribe());
        modelMapping.setLength(imModelMapping.getLength());
        modelMapping.setNote(imModelMapping.getNote());
        modelMapping.setType(DataType.valueOf(imModelMapping.getType()));
        modelMapping.setSeq(imModelMapping.getSeq());
        modelMapping.setIndexed("0".equals(imModelMapping.getIndexed()));
        modelMapping.setPrimary("0".equals(imModelMapping.getPrimary()));
        modelMapping.setStored("0".equals(imModelMapping.getStored()));
    }

    //过滤字段转换
    private void transformModelFilterCol(ImModelFilterCol imModelFilterCol, ModelFilterCol modelFilterCol) throws Exception {
        modelFilterCol.setSeq(imModelFilterCol.getSeq());
        modelFilterCol.setDefaultVal(imModelFilterCol.getDefaultVal());
        modelFilterCol.setDescribe(imModelFilterCol.getDescribe());
        modelFilterCol.setLabel(imModelFilterCol.getLabel());
        modelFilterCol.setLength(imModelFilterCol.getLength());
        modelFilterCol.setName(imModelFilterCol.getName());
        modelFilterCol.setType(DataType.valueOf(imModelFilterCol.getType()));
        modelFilterCol.setNeed(imModelFilterCol.getIsNeed().equals("0"));
        modelFilterCol.setOperator(Operator.getOperatorByValue(imModelFilterCol.getOperator()));
    }

    public HSSFWorkbook setWorkbookSheet(HSSFWorkbook workbook, Map<String,String> map, String appId) {
        String templateFile = ExcelCopyUtils.templatePath + FileUtil.getFileSeparator() + "downLoadTemplate_allServiceInfo.xls";
        // 获取模板文件第一个Sheet对象
        POIFSFileSystem sourceFile = null;
        HSSFWorkbook sourceWork = null;
        HSSFSheet sourceSheet = null;
        try {
            sourceFile = new POIFSFileSystem(new FileInputStream(templateFile));
            sourceWork = new HSSFWorkbook(sourceFile);
            // 交互建模为第7个sheet
            sourceSheet = sourceWork.getSheetAt(6);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImModel imModel = select(appId);

        List<ComExcelParam> comExcelParams = new ArrayList<ComExcelParam>();
        comExcelParams.add(new ComExcelParam(2, 1, "serviceName"));
        comExcelParams.add(new ComExcelParam(2, 3, "serviceDescribe"));
        comExcelParams.add(new ComExcelParam(3, 1, "maxSyncNum"));
        comExcelParams.add(new ComExcelParam(3, 3, "maxAsyncNum"));
        comExcelParams.add(new ComExcelParam(3, 5, "maxSyncWaitNum"));
        comExcelParams.add(new ComExcelParam(3, 7, "maxAsyncWaitNum"));
        comExcelParams.add(new ComExcelParam(4, 1, "userId"));
        comExcelParams.add(new ComExcelParam(4, 3, "userName"));

        HSSFSheet sheet = workbook.createSheet(map.get("serviceName"));

        //将前面样式内容复制到下载表中
        int i = 0;
        for (; i < 11; i++) {
            try {
                ExcelCopyUtils.copyRow(sheet.createRow(i), sourceSheet.getRow(i), sheet.createDrawingPatriarch(), workbook);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (ComExcelParam comExcelParam : comExcelParams) {
            try {
                ExcelCopyUtils.setCellValue(sheet, comExcelParam.getRowNum(), comExcelParam.getCellNum(), map.get(comExcelParam.getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.setWorkbookSheetPart(sheet, imModel, sourceSheet, workbook, new ImIndexDto(i));

        return workbook;
    }

    private void setWorkbookSheetPart(HSSFSheet sheet, ImModel imModel, HSSFSheet sourceSheet,
                                     HSSFWorkbook workbook, ImIndexDto imIndexDto) {
        HSSFRow row;
        HSSFCell cell;
        int rowIndex = imIndexDto.getRowIndex();

        List<ImModelFilterCol> imModelFilterCols = imModelFilterColMapper.selectList(imModel.getPkId());
        if (imModelFilterCols != null && imModelFilterCols.size() > 0) {
            int k = 1;
            for (ImModelFilterCol imModelFilterCol : imModelFilterCols) {
                row = sheet.createRow(rowIndex);
                cell = row.createCell(0);
                cell.setCellValue(k);
                cell = row.createCell(1);
                cell.setCellValue(imModelFilterCol.getName());
                cell = row.createCell(2);
                cell.setCellValue(imModelFilterCol.getDescribe());
                cell = row.createCell(3);
                cell.setCellValue(imModelFilterCol.getType());
                cell = row.createCell(4);
                cell.setCellValue(imModelFilterCol.getLength());
                cell = row.createCell(5);
                cell.setCellValue("0".equals(imModelFilterCol.getIsNeed()) ? "是" : "否");
                cell = row.createCell(6);
                cell.setCellValue(imModelFilterCol.getOperator());
                cell = row.createCell(7);
                cell.setCellValue(imModelFilterCol.getDefaultVal());
                cell = row.createCell(8);
                cell.setCellValue(imModelFilterCol.getLabel());
                rowIndex++;
                k++;
            }
        }
    }
}
