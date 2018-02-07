package com.hex.bigdata.udsp.common.controller;

import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.bigdata.udsp.common.service.ComPropertiesService;
import com.hex.goframe.controller.BaseController;
import com.hex.goframe.model.MessageResult;
import com.hex.goframe.model.PageListResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 公共配置控制层
 * Created by tomnic on 2017/2/27.
 */
@RequestMapping("/com/props/")
@Controller
public class ComPropertiesController extends BaseController {
    private static Logger logger = LogManager.getLogger(ComPropertiesController.class);

    @Autowired
    private ComPropertiesService comPropertiesService;

    @RequestMapping({"/select/{fkId}"})
    @ResponseBody
    public MessageResult select(@PathVariable("fkId") String fkId) {
        boolean status = true;
        String message = "查询成功";
        List<ComProperties> list = null;
        if (StringUtils.isBlank(fkId)) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                list = this.comPropertiesService.selectList(fkId);
            } catch (Exception e) {
                e.printStackTrace();
                status = false;
                message = "系统异常:" + e;
            }
        }
        if (status) {
            logger.debug(message);
        } else {
            logger.info(message);
        }
        return new PageListResult(list);
    }

}
