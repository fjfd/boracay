package com.hex.bigdata.udsp.rts.controller;

import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.rts.dto.RtsMetadataColsView;
import com.hex.bigdata.udsp.rts.dto.RtsMetadataView;
import com.hex.bigdata.udsp.rts.model.RtsMetadata;
import com.hex.bigdata.udsp.rts.service.RtsMetadataService;
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
 * 实时流-元数据管理控制器
 * Created by tomnic on 2017/2/28.
 */
@RequestMapping("/rts/md")
@Controller
public class RtsMetadataController extends BaseController {
    /**
     * 日志记录
     */
    private static Logger logger = LogManager.getLogger(RtsMetadataController.class);

    /**
     * 实时流-元数据管理
     */
    @Autowired
    private RtsMetadataService rtsMetadataService;

    /**
     * 分页多条件查询
     *
     * @param rtsMetadataView 查询参数
     * @param page            分页参数
     * @return
     */
    @RequestMapping({"/page"})
    @ResponseBody
    public PageListResult queryRtsDatasources(RtsMetadataView rtsMetadataView, Page page) {
        List<RtsMetadataView> list = rtsMetadataService.select(rtsMetadataView, page);
        logger.debug("selectPage search=" + JSONUtil.parseObj2JSON(rtsMetadataView) + " page=" + JSONUtil.parseObj2JSON(page));
        return new PageListResult(list, page);
    }

    /**
     * 实时流-新增元数据记录
     *
     * @param rtsMetadataColsView
     * @return
     */
    @RequestMapping({"/insert"})
    @ResponseBody
    public MessageResult insert(@RequestBody RtsMetadataColsView rtsMetadataColsView) {
        boolean status = true;
        String message = "添加成功";
        if (rtsMetadataColsView == null) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                if (StringUtils.isBlank(rtsMetadataService.insert(rtsMetadataColsView))) {
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
                if (!rtsMetadataService.checekUniqueName(name)) {
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
        RtsMetadata rtsMetadata = null;
        if (StringUtils.isBlank(pkId)) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                rtsMetadata = rtsMetadataService.select(pkId);
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
        return new MessageResult(status, message, rtsMetadata);
    }

    @RequestMapping({"/update"})
    @ResponseBody
    public MessageResult update(@RequestBody RtsMetadataColsView rtsMetadataColsView) {
        boolean status = true;
        String message = "更新成功";
        if (rtsMetadataColsView == null) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                if (rtsMetadataService.hasUsed(rtsMetadataColsView.getRtsMetadata().getPkId())) {
                    status = false;
                    message = "该元数据已被引用！";
                }

                if (status && !rtsMetadataService.update(rtsMetadataColsView)) {
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

    @RequestMapping("/delete")
    @ResponseBody
    public MessageResult delete(@RequestBody RtsMetadata[] rtsMetadatas) {
        boolean status = true;
        String message = "删除成功";
        if (rtsMetadatas.length == 0) {
            status = false;
            message = "请求参数为空";
        }
        try {

            for (RtsMetadata rtsMetadata : rtsMetadatas) {
                if (rtsMetadataService.hasUsed(rtsMetadata.getPkId())) {
                    status = false;
                    message = "元数据名称为：" + rtsMetadataService.select(rtsMetadata.getPkId()).getName() + "已被引用！";
                    break;
                }
            }
            if (status && !rtsMetadataService.delete(rtsMetadatas)) {
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

    @RequestMapping({"/select"})
    @ResponseBody
    public MessageResult<List> select(RtsMetadataView rtsMetadataView) {
        List<RtsMetadataView> list = rtsMetadataService.select(rtsMetadataView);
        logger.debug("select search=" + JSONUtil.parseObj2JSON(rtsMetadataView));
        return new MessageResult(true, list);
    }

    @RequestMapping("upload")
    @ResponseBody
    public MessageResult upload(MultipartFile excelFile) {
        boolean status = true;
        String message = "上传成功";

        //判断结尾是否为xl或者xlsx
        if (((CommonsMultipartFile) excelFile).getFileItem().getName().endsWith(".xls")
                || ((CommonsMultipartFile) excelFile).getFileItem().getName().endsWith(".xlsx")) {
            //将文件放到项目上传文件目录中
            String uploadFilePath = FileUtil.uploadFile(FileUtil.getRealUploadPath("EXCEL_UPLOAD"), excelFile);
            Map<String, String> result = rtsMetadataService.uploadExcel(uploadFilePath);
            if ("false".equals(result.get("status"))) {
                status = false;
                message = result.get("message");
            }
        } else {
            status = false;
            message = "请上传正确格式的文件！";
        }
        return new MessageResult(status, message);
    }

    @ResponseBody
    @RequestMapping("/download")
    public String createExcel(@RequestBody RtsMetadata[] rtsMetadatas) {
        // 写入Excel文件
        String filePath = "";
        try {
            filePath = rtsMetadataService.createExcel(rtsMetadatas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

}
