package com.hex.bigdata.udsp.controller;

import com.hex.bigdata.udsp.common.constant.ConsumerEntity;
import com.hex.bigdata.udsp.common.constant.ConsumerType;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.common.dto.ComDatasourcePropsView;
import com.hex.bigdata.udsp.common.util.FTPClientConfig;
import com.hex.bigdata.udsp.common.util.FTPHelper;
import com.hex.bigdata.udsp.common.util.HostUtil;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.util.RequestUtil;
import com.hex.bigdata.udsp.service.DatasourceTestService;
import com.hex.bigdata.udsp.service.InnerConsumerService;
import com.hex.goframe.controller.BaseController;
import com.hex.goframe.model.GFLoginUser;
import com.hex.goframe.model.MessageResult;
import com.hex.goframe.model.Page;
import com.hex.goframe.model.PageListResult;
import com.hex.goframe.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Http Controller
 */
@RequestMapping("/http")
@Controller
public class HttpController extends BaseController {
    private static Logger logger = LogManager.getLogger(HttpController.class);

    static {
        FTPClientConfig.loadConf("goframe/udsp/udsp.config.properties");
    }

    @Value("${download.timeout.ms:300000}")
    private Long downloadTimeoutMs;

    @Value("${download.sleep.time.ms:1000}")
    private Long downloadSleepTimeMs;

    @Autowired
    private InnerConsumerService consumerService;

    @Autowired
    private DatasourceTestService datasourceTestService;

    /**
     * 内部请求1
     * <p/>
     * (由于nui的datagrid请求方式特殊性)
     */
    @RequestMapping({"/datagrid/consume"})
    @ResponseBody
    public PageListResult datagridConsume(Request request, Page page, HttpServletRequest httpServletRequest) {
        boolean status = true;
        String message = "执行成功";
        com.hex.bigdata.udsp.common.api.model.Page p = new com.hex.bigdata.udsp.common.api.model.Page();
        p.setPageIndex(page.getPageIndex());
        p.setPageSize(page.getPageSize());
        request.setPage(p);
        //获取并设置客户端请求的IP
        request.setRequestIp(HostUtil.getRealRequestIp(httpServletRequest));
        PageListResult pageListResult = null;
        try {
            Response response = getInnerConsume(request);
            if ("SUCCESS".equals(response.getStatus())) {
                if (response.getPage() != null) {
                    pageListResult = new PageListResult(response.getRecords(), page);
                    pageListResult.setTotal(response.getPage().getTotalCount());
                } else {
                    pageListResult = new PageListResult(response.getRecords());
                }
                pageListResult.setStatus(status);
                pageListResult.setMessage(message);
            } else {
                status = false;
                message = response.getMessage();
                pageListResult = new PageListResult(null);
                pageListResult.setStatus(status);
                pageListResult.setMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
            message = e.getMessage();
            pageListResult = new PageListResult(null);
            pageListResult.setStatus(status);
            pageListResult.setMessage(message);
        }
        return pageListResult;
    }

    /**
     * 内部请求2
     */
    @RequestMapping({"/inner/consume"})
    @ResponseBody
    public Response innerConsume(@RequestBody String json, HttpServletRequest httpServletRequest) {
        Request request = RequestUtil.jsonToRequest(json);
        request.setRequestIp(HostUtil.getRealRequestIp(httpServletRequest)); //获取并设置客户端请求的IP
        return getInnerConsume(request);
    }

    /**
     * 内部请求3
     * <p/>
     * 异步请求并下载文件
     */
    @RequestMapping({"/inner/async/consume"})
    @ResponseBody
    public MessageResult innerAsyncConsume(@RequestBody String json, HttpServletRequest httpServletRequest) {
        Request request = RequestUtil.jsonToRequest(json);
        request.setRequestIp(HostUtil.getRealRequestIp(httpServletRequest)); //获取并设置客户端请求的IP
        if (!ConsumerType.ASYNC.getValue().equalsIgnoreCase(request.getType())
                || !ConsumerEntity.START.getValue().equalsIgnoreCase(request.getEntity())) {
            return new MessageResult(false, "不为异步的start请求");
        }
        Response response = getInnerConsume(request);
        if (response == null || !Status.SUCCESS.getValue().equalsIgnoreCase(response.getStatus())) {
            return new MessageResult(false, "异步的start请求失败");
        }
        String responseContent = response.getResponseContent();
        request.setType("async");
        request.setEntity("status");
        request.setConsumeId(response.getConsumeId());
        int count = 0;
        int num = (int) (downloadTimeoutMs / downloadSleepTimeMs);
        while (true) {
            if (count >= num) {
                return new MessageResult(false, "异步下载文件超时");
            }
            response = getInnerConsume(request);
            if (response == null) {
                return new MessageResult(false, "异步的status响应为空");
            }
            if (StatusCode.SUCCESS.getValue().equals(response.getStatusCode())) {
                return new MessageResult(true, responseContent);
            } else if (StatusCode.RUNNING.getValue().equals(response.getStatusCode())) {
                try {
                    Thread.sleep(downloadSleepTimeMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                return new MessageResult(false, response.getMessage());
            }
            count++;
        }
    }

    /**
     * 下载文件
     */
    @RequestMapping({"/inner/download"})
    @ResponseBody
    public void innerDownload(String filePath, HttpServletResponse rsp) {
        OutputStream os = null;
        InputStream in = null;
        try {
            os = rsp.getOutputStream();
            rsp.reset();
            String fileName = FileUtil.getFileName(filePath);
            rsp.setHeader("Content-Disposition", "attachment;filename="
                    + fileName);
            rsp.setContentType("application/octet-stream;charset=utf-8");
            FTPHelper ftpHelper = new FTPHelper();
            ftpHelper.connectFTPServer();
            in = ftpHelper.downloadFile(filePath);
            IOUtils.copy(in, os);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 测试数据源
     */
    @RequestMapping("/ds/test")
    @ResponseBody
    public MessageResult dsTest(@RequestBody ComDatasourcePropsView comDatasourcePropsView) {
        boolean status = true;
        String message = "测试数据源成功！";
        if (comDatasourcePropsView == null) {
            status = false;
            message = "请求参数为空";
        } else {
            try {
                status = datasourceTestService.testDatasource(comDatasourcePropsView);
                if (status) {
                    message = "测试数据源成功！";
                } else {
                    message = "测试数据源失败！";
                }
            } catch (Exception e) {
                e.printStackTrace();
                status = false;
                message = "测试数据源失败!";
            }
        }
        if (status) {
            logger.debug(message);
        } else {
            logger.warn(message);
        }
        return new MessageResult(status, message);
    }

    private Response getInnerConsume(Request request) {
        List<String> roles = getUserRoles();
        boolean isAdmin = roles.contains("ADMIN");
        GFLoginUser loginUser = getLoginUser();
        request.setUdspUser(loginUser.getUserId());
        return consumerService.innerConsume(request, isAdmin);
    }
}
