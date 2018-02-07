package com.hex.bigdata.udsp.rts.controller;

import com.hex.bigdata.udsp.common.model.ComDatasource;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.rts.dto.RtsConsumerProsView;
import com.hex.bigdata.udsp.rts.dto.RtsConsumerView;
import com.hex.bigdata.udsp.rts.model.RtsConsumer;
import com.hex.bigdata.udsp.rts.model.RtsMatedataCol;
import com.hex.bigdata.udsp.rts.service.RtsConsumerService;
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
 * Created by tomnic on 2017/3/1.
 */
@RequestMapping("/rts/consumer")
@Controller
public class RtsConsumerController extends BaseController {

    /**
     * 日志记录
     */
    private static Logger logger = LogManager.getLogger(RtsConsumerController.class);

    /**
     * 实时流数据源服务
     */
    @Autowired
    private RtsConsumerService rtsConsumerService;


    /**
     * 分页多条件查询
     *
     * @param rtsConsumerView 查询参数
     * @param page              分页参数
     * @return
     */
    @RequestMapping({"/page"})
    @ResponseBody
    public PageListResult queryRtsDatasources(RtsConsumerView rtsConsumerView, Page page) {
        List<RtsConsumerView> list = rtsConsumerService.select(rtsConsumerView, page);
        logger.debug("selectPage search=" + JSONUtil.parseObj2JSON(rtsConsumerView) + " page=" + JSONUtil.parseObj2JSON(page));
        return new PageListResult(list, page);
    }

    @RequestMapping({"/select"})
    @ResponseBody
    public MessageResult<List> select(RtsConsumerView rtsDatasourceView) {
        List<RtsConsumerView> list = rtsConsumerService.select(rtsDatasourceView);
        logger.debug("select search=" + JSONUtil.parseObj2JSON(rtsDatasourceView));
        return new MessageResult(true, list);
    }

    /**
     * 新增实时流数据源实体
     *
     * @param rtsConsumerProsView 实时流数据源及其配置参数实体
     * @return
     */
    @RequestMapping("/insert")
    @ResponseBody
    public MessageResult insert(@RequestBody RtsConsumerProsView rtsConsumerProsView) {
        boolean status = true;
        String message = "添加成功";
        if (rtsConsumerProsView == null) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                if (StringUtils.isBlank(rtsConsumerService.insert(rtsConsumerProsView))) {
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
     * @param name 数据源名称
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
                if (!rtsConsumerService.checekUniqueName(name)) {
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

    /**
     * 根据主键查询实时流数据源实体
     *
     * @param pkId 记录主键
     * @return
     */
    @RequestMapping({"/select/{pkId}"})
    @ResponseBody
    public MessageResult select(@PathVariable("pkId") String pkId) {
        boolean status = true;
        String message = "查询成功";
        RtsConsumer rtsDatasource  = null;
        if (StringUtils.isBlank(pkId)) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                rtsDatasource = this.rtsConsumerService.select(pkId);
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
        return new MessageResult(status, message, rtsDatasource);
    }

    /**
     * 编辑数据源实体
     *
     * @param rtsConsumerProsView
     * @return
     */
    @RequestMapping({"/update"})
    @ResponseBody
    public MessageResult update(@RequestBody RtsConsumerProsView rtsConsumerProsView) {
        boolean status = true;
        String message = "更新成功";
        if (rtsConsumerProsView == null) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                if (!rtsConsumerService.update(rtsConsumerProsView)) {
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
    }

    /**
     * 批量删除
     *
     * @param rtsConsumers
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public MessageResult deleteList(@RequestBody RtsConsumer[] rtsConsumers) {
        boolean status = true;
        String message = "删除成功";
        if (rtsConsumers.length == 0) {
            status = false;
            message = "请求参数为空";
        }
        try {
            if (!rtsConsumerService.delete(rtsConsumers)) {
                status = false;
                message = "删除失败";
            }
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

    @RequestMapping("/selectConsumerColumns/{consumerId}")
    @ResponseBody
    public MessageResult selectConsumerColumns(@PathVariable("consumerId")String consumerId) {
        boolean status = true;
        String message = "查询成功";
        List<RtsMatedataCol> rtsConsumerCols = null;
        if (StringUtils.isBlank(consumerId)) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                rtsConsumerCols = this.rtsConsumerService.selectConsumerColumns(consumerId);
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
        return new MessageResult(status, message, rtsConsumerCols);
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
            Map<String,String> result = rtsConsumerService.uploadExcel(uploadFilePath);
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
    public String createExcel(@RequestBody RtsConsumer[] rtsConsumers){
        // 写入Excel文件
        String filePath = "";
        try {
            filePath = rtsConsumerService.createExcel(rtsConsumers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
