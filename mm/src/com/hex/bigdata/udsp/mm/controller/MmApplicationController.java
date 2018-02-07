package com.hex.bigdata.udsp.mm.controller;

import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.mm.dto.MmApplicationParamView;
import com.hex.bigdata.udsp.mm.dto.MmApplicationView;
import com.hex.bigdata.udsp.mm.dto.MmModelInfoView;
import com.hex.bigdata.udsp.mm.model.MmAppExecuteParam;
import com.hex.bigdata.udsp.mm.model.MmAppReturnParam;
import com.hex.bigdata.udsp.mm.model.MmApplication;
import com.hex.bigdata.udsp.mm.service.MmAppExecuteParamService;
import com.hex.bigdata.udsp.mm.service.MmApplicationService;
import com.hex.bigdata.udsp.mm.service.MmAppReturnParamService;
import com.hex.goframe.controller.BaseController;
import com.hex.goframe.model.MessageResult;
import com.hex.goframe.model.Page;
import com.hex.goframe.model.PageListResult;
import com.hex.goframe.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.List;
import java.util.Map;


/**
 * 模型管理-模型应用管理控制器
 * Created with IntelliJ IDEA
 * Author: tomnic.wang
 * DATE:2017/4/6
 * TIME:9:11
 */
@RequestMapping("/mm/app/")
@Controller
public class MmApplicationController extends BaseController{
    /**
     * 日志记录
     */
    private static Logger logger = LogManager.getLogger(MmApplicationController.class);

    /**
     * 模型管理-模型应用管理服务
     */
    @Autowired
    private MmApplicationService MmApplicationService;

    /**
     * 模型管理-模型应用配置管理字段定义
     */
    @Autowired
    private MmAppExecuteParamService executeParamService;

    /**
     * 模型管理-模型应用配置返回字段定义
     */
    @Autowired
    private MmAppReturnParamService returnParamService;



    /**
     * 分页多条件查询
     * @param mmApplicationView 查询参数
     * @param page 分页参数
     * @return
     */
    @RequestMapping({"/page"})
    @ResponseBody
    public PageListResult queryRtsDatasources(MmApplicationView mmApplicationView, Page page) {
        List<MmApplicationView> list = MmApplicationService.select(mmApplicationView, page);
        logger.debug("selectPage search=" + JSONUtil.parseObj2JSON(mmApplicationView) + " page=" + JSONUtil.parseObj2JSON(page));
        return new PageListResult(list, page);
    }

    /**
     * 模型管理-新增模型应用记录
     * @param mmApplicationParamView
     * @return
     */
    @RequestMapping({"/insert"})
    @ResponseBody
    public MessageResult insert(@RequestBody MmApplicationParamView mmApplicationParamView) {
        boolean status = true;
        String message = "添加成功";
        if (mmApplicationParamView == null) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                if (StringUtils.isBlank(MmApplicationService.insert(mmApplicationParamView))) {
                    status = false;
                    message = "添加失败";
                }
            } catch (Exception e) {
                e.printStackTrace();
                status = false;
                message = "系统异常：" + e.getMessage();
            }
        }
        if (status) {
            logger.debug(message);
        } else {
            logger.warn(message);
        }
        return new MessageResult(status, message);
    }

    /**
     * 检查名称唯一性
     *
     * @param name 服务名称
     * @return
     */
    @RequestMapping({"/checkName/{name}"})
    @ResponseBody
    public MessageResult checekName(@PathVariable("name") String name) {
        boolean status = true;
        String message = "添加成功";
        if (StringUtils.isBlank(name)) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                if (!MmApplicationService.checekUniqueName(name)) {
                    status = false;
                    message = "检查完成，名称不存在！";
                }
            } catch (Exception e) {
                e.printStackTrace();
                status = false;
                message = "系统异常：" + e.getMessage();
            }
        }
        if (status) {
            logger.debug(message);
        } else {
            logger.warn(message);
        }
        return new MessageResult(status, message);
    }


    @RequestMapping({"/select/{pkId}"})
    @ResponseBody
    public MessageResult select(@PathVariable("pkId") String pkId) {
        boolean status = true;
        String message = "查询成功";
        MmApplication mmApplication = null;
        if (StringUtils.isBlank(pkId)) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                mmApplication = this.MmApplicationService.select(pkId);
            } catch (Exception e) {
                status = false;
                message = "系统异常：" + e.getMessage();
            }
        }
        if (status) {
            logger.debug(message);
        } else {
            logger.warn(message);
        }
        return new MessageResult(status, message, mmApplication);
    }

   /* @RequestMapping({"/update"})
    @ResponseBody
    public MessageResult update(@RequestBody MmApplication mmApplication) {
        boolean status = true;
        String message = "更新成功";
        if (mmApplication == null) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                if (!MmApplicationService.update(mmApplication)) {
                    status = false;
                    message = "更新失败";
                }
            } catch (Exception e) {
                status = false;
                message = "系统异常：" + e.getMessage();
            }
        }
        if (status) {
            logger.debug(message);
        } else {
            logger.warn(message);
        }
        return new MessageResult(status, message);
    }*/

    @RequestMapping("/delete")
    @ResponseBody
    public MessageResult delete(@RequestBody MmApplication[] mmApplications) {
        boolean status = true;
        String message = "删除成功";
        if (mmApplications.length == 0) {
            status = false;
            message = "请求参数为空";
        }
        try {
           return MmApplicationService.delete(mmApplications);
        } catch (Exception e) {
            status = false;
            message = "系统异常：" + e.getMessage();
        }
        if (status) {
            logger.debug(message);
        } else {
            logger.warn(message);
        }
        return new MessageResult(status, message);
    }

    /**
     * 查询所有有效应用
     * @param modelInfoView
     * @return
     */
    @RequestMapping({"/select"})
    @ResponseBody
    public PageListResult select(MmModelInfoView modelInfoView) {
        logger.debug("select search=" + JSONUtil.parseObj2JSON(modelInfoView));
        List<MmApplication> list = null;
        try {
            list = MmApplicationService.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：" + e);
        }
        return new PageListResult(list);
    }

    /**
     * 查询模型应用配置查询字段
     * @param appId
     * @return
     */
    @RequestMapping({"/selectByAppId/{appId}"})
    @ResponseBody
    public PageListResult executeParamInfo(@PathVariable("appId") String appId) {
        logger.debug("select search=" + appId);
        List<MmAppExecuteParam> list = null;
        try {
            list = executeParamService.selectByFkId(appId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：" + e);
        }
        return new PageListResult(list);
    }

    /**
     * 查询模型应用返回参数查询字段
     * @param appId
     * @return
     */
    @RequestMapping({"/selectBackByAppId/{appId}"})
    @ResponseBody
    public PageListResult returnParamInfo(@PathVariable("appId") String appId) {
        logger.debug("select search=" + appId);
        List<MmAppReturnParam> list = null;
        try {
            list = returnParamService.selectByFkId(appId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：" + e);
        }
        return new PageListResult(list);
    }

    /**
     * 更新模型应用配置
     * @param mmApplicationParamView
     * @return
     */
    @RequestMapping({"/update"})
    @ResponseBody
    public MessageResult update(@RequestBody MmApplicationParamView mmApplicationParamView) {
        boolean status = true;
        String message = "更新成功";
        if (mmApplicationParamView == null) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                if (!MmApplicationService.update(mmApplicationParamView)) {
                    status = false;
                    message = "更新失败";
                }
            } catch (Exception e) {
                e.printStackTrace();
                status = false;
                message = "系统异常：" + e;
            }
        }
        if (status) {
            logger.debug(message);
        } else {
            logger.error(message);
        }
        return new MessageResult(status, message);
    }


    @RequestMapping("upload")
    @ResponseBody
    public MessageResult upload(MultipartFile excelFile){
        boolean status = true;
        String message = "上传成功";

        //判断结尾是否为xl或者xlsx
        if (((CommonsMultipartFile)excelFile).getFileItem().getName().endsWith(".xls")
                || ((CommonsMultipartFile)excelFile).getFileItem().getName().endsWith(".xlsx")) {
            //将文件放到项目上传文件目录中
            String uploadFilePath = FileUtil.uploadFile(FileUtil
                    .getRealUploadPath("EXCEL_UPLOAD"), excelFile);
            Map<String,String> result = MmApplicationService.uploadExcel(uploadFilePath);
            if("false".equals(result.get("status"))){
                status = false;
                message = result.get("message");
            }
        }else{
            status = false;
            message = "请上传正确格式的文件！";
        }
        return new MessageResult(status,message);
    }

    @ResponseBody
    @RequestMapping("/download")
    public String createExcel(@RequestBody MmApplication[] mmApplications){
        // 写入Excel文件
        String filePath = "";
        try {
            filePath = MmApplicationService.createExcel(mmApplications);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

}
