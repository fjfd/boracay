package com.hex.bigdata.udsp.iq.controller;

import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.iq.model.IqMetadataCol;
import com.hex.bigdata.udsp.iq.service.IqMetadataColService;
import com.hex.goframe.controller.BaseController;
import com.hex.goframe.model.MessageResult;
import com.hex.goframe.model.Page;
import com.hex.goframe.model.PageListResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by tomnic on 2017/2/20.
 */
@RequestMapping("/iq/md/col")
@Controller
public class IqMetadataColController extends BaseController {
    private static Logger logger = LogManager.getLogger(IqMetadataColController.class);

    @Autowired
    private IqMetadataColService iqMetadataColService;

    @RequestMapping({"/page"})
    @ResponseBody
    public PageListResult page(IqMetadataCol iqMetadataCol, Page page) {
        logger.debug("select search=" + JSONUtil.parseObj2JSON(iqMetadataCol) + " page=" + JSONUtil.parseObj2JSON(page));
        List<IqMetadataCol> list = null;
        try {
            list = iqMetadataColService.select(iqMetadataCol, page);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：" + e);
        }
        return new PageListResult(list, page);
    }

    @RequestMapping({"/select"})
    @ResponseBody
    public PageListResult select(IqMetadataCol iqMetadataCol) {
        logger.debug("select search=" + JSONUtil.parseObj2JSON(iqMetadataCol));
        List<IqMetadataCol> list = null;
        try {
            list = iqMetadataColService.select(iqMetadataCol);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：" + e);
        }
        return new PageListResult(list);
    }

    @RequestMapping({"/select/{pkId}"})
    @ResponseBody
    public MessageResult selectByPkId(@PathVariable("pkId") String pkId) {
        logger.debug("select pkId=" + pkId);
        IqMetadataCol iqMetadataCol = null;
        try {
            iqMetadataCol = iqMetadataColService.select(pkId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：" + e);
        }
        return new MessageResult(true, "", iqMetadataCol);
    }

    @RequestMapping({"/selectQueryColList/{mdId}"})
    @ResponseBody
    public MessageResult selectQueryColList(@PathVariable("mdId") String mdId) {
        logger.debug("selectQueryColList mdId=" + mdId);
        List<IqMetadataCol> list = null;
        try {
            list = iqMetadataColService.selectQueryColList(mdId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：" + e);
        }
        return new PageListResult(list);
    }

    @RequestMapping({"/selectReturnColList/{mdId}"})
    @ResponseBody
    public MessageResult selectReturnColList(@PathVariable("mdId") String mdId) {
        logger.debug("selectReturnColList mdId=" + mdId);
        List<IqMetadataCol> list = null;
        try {
            list = iqMetadataColService.selectReturnColList(mdId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("系统异常：" + e);
        }
        return new PageListResult(list);
    }

//    @RequestMapping({"/select/{mdId}/{type}"})
//    @ResponseBody
//    public MessageResult selectByPkIdAndType(@PathVariable("mdId") String mdId, @PathVariable("type") String type) {
//        logger.debug("select mdId=" + mdId + ",type=" + type);
//        List<IqMetadataCol> list = null;
//        try {
//            list = iqMetadataColService.select(mdId, type);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("系统异常：" + e);
//        }
//        return new PageListResult(list);
//    }

//    @RequestMapping({"/checkBeforeRemoveCols/{mdId}"})
//    @ResponseBody
//    public MessageResult checkBeforeRemoveCols(@PathVariable("mdId") String mdId) {
//        boolean status = true;
//        String message = "";
//        //删除检查
//        try{
//            if(iqMetadataColService.selectAppPkIdsByMdid(mdId).size() > 0){
//                status = false;
//                message = "该元数据已被引用！";
//            }
//        }catch (Exception e){
//            status = false;
//            message = e.getMessage();
//        }
//
//        return  new MessageResult(status, message);
//    }
}
