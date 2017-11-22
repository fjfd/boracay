package com.hex.bigdata.udsp.service;


import com.hex.bigdata.udsp.common.constant.CommonConstant;
import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.common.model.ComDatasource;
import com.hex.bigdata.udsp.common.provider.model.Page;
import com.hex.bigdata.udsp.common.service.ComDatasourceService;
import com.hex.bigdata.udsp.common.service.InitParamService;
import com.hex.bigdata.udsp.common.util.*;
import com.hex.bigdata.udsp.constant.ConsumerConstant;
import com.hex.bigdata.udsp.dto.ConsumeRequest;
import com.hex.bigdata.udsp.dto.QueueIsFullResult;
import com.hex.bigdata.udsp.dto.WaitNumResult;
import com.hex.bigdata.udsp.iq.model.IqApplication;
import com.hex.bigdata.udsp.iq.service.IqAppQueryColService;
import com.hex.bigdata.udsp.iq.service.IqApplicationService;
import com.hex.bigdata.udsp.mc.constant.McConstant;
import com.hex.bigdata.udsp.mc.model.Current;
import com.hex.bigdata.udsp.mc.model.McConsumeLog;
import com.hex.bigdata.udsp.mc.service.CurrentService;
import com.hex.bigdata.udsp.mc.service.McConsumeLogService;
import com.hex.bigdata.udsp.mc.service.RunQueueService;
import com.hex.bigdata.udsp.mc.service.WaitQueueService;
import com.hex.bigdata.udsp.mc.util.McCommonUtil;
import com.hex.bigdata.udsp.mm.model.MmApplication;
import com.hex.bigdata.udsp.mm.service.MmApplicationService;
import com.hex.bigdata.udsp.model.ExternalRequest;
import com.hex.bigdata.udsp.model.InnerRequest;
import com.hex.bigdata.udsp.model.Request;
import com.hex.bigdata.udsp.model.Response;
import com.hex.bigdata.udsp.olq.dto.OLQApplicationDto;
import com.hex.bigdata.udsp.olq.model.OLQApplication;
import com.hex.bigdata.udsp.olq.service.OLQApplicationService;
import com.hex.bigdata.udsp.olq.utils.OLQCommUtil;
import com.hex.bigdata.udsp.rc.model.RcService;
import com.hex.bigdata.udsp.rc.model.RcUserService;
import com.hex.bigdata.udsp.rc.service.AlarmService;
import com.hex.bigdata.udsp.rc.service.RcServiceService;
import com.hex.bigdata.udsp.rc.service.RcUserServiceService;
import com.hex.bigdata.udsp.rc.util.RcConstant;
import com.hex.bigdata.udsp.rts.model.RtsConsumer;
import com.hex.bigdata.udsp.rts.model.RtsProducer;
import com.hex.bigdata.udsp.rts.service.RtsConsumerService;
import com.hex.bigdata.udsp.rts.service.RtsMatedataColService;
import com.hex.bigdata.udsp.rts.service.RtsProducerService;
import com.hex.bigdata.udsp.thread.WaitQueueCallable;
import com.hex.bigdata.udsp.thread.sync.ImSyncServiceCallable;
import com.hex.bigdata.udsp.thread.sync.IqSyncServiceCallable;
import com.hex.bigdata.udsp.thread.sync.OlqSyncServiceCallable;
import com.hex.goframe.model.MessageResult;
import com.hex.goframe.service.UserService;
import com.hex.goframe.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.*;

/**
 * 消费的服务
 */
@Service
public class ConsumerService {
    private static Logger logger = LogManager.getLogger(ConsumerService.class);
    private static final FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ExecutorService executorService = new ThreadPoolExecutor(256,
            2048, 30, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(1000));

    @Autowired
    private UserService userService;
    @Autowired
    private RcServiceService rcServiceService;
    @Autowired
    private RcUserServiceService rcUserServiceService;
    @Autowired
    private McConsumeLogService mcConsumeLogService;
    @Autowired
    private IqApplicationService iqApplicationService;
    @Autowired
    private ComDatasourceService comDatasourceService;
    @Autowired
    private RtsProducerService rtsProducerService;
    @Autowired
    private RtsConsumerService rtsConsumerService;
    @Autowired
    private RtsSyncService rtsSyncService;
    @Autowired
    private MmRequestService mmRequestService;
    @Autowired
    private MmApplicationService mmApplicationService;
    @Autowired
    private RunQueueService runQueueService;
    @Autowired
    private CurrentService mcCurrentService;
    @Autowired
    private IqAppQueryColService iqAppQueryColService;
    @Autowired
    private RtsMatedataColService rtsMatedataColService;
    @Autowired
    private OLQApplicationService olqApplicationService;
    @Autowired
    private WaitQueueService mcWaitQueueService;
    @Autowired
    private InitParamService initParamService;
    @Autowired
    private AlarmService alarmService;

    /**
     * 管理员用户最大同步并发数
     */
    @Value("${admin.sync.count:100}")
    private Integer adminMaxSyncNum;

    /**
     * 管理员用户最大异步并发数
     */
    @Value("${admin.async.count:100}")
    private Integer adminMaxAsyncNum;

    /**
     * 同步循环时间间隔，单位毫秒
     */
    @Value("${sync.cycle.time.interval:200}")
    private long syncCycleTimeInterval;

    /**
     * 异步循环时间间隔，单位秒
     */
    @Value("${async.cycle.time.interval:1000}")
    private long asyncCycleTimeInterval;

    /**
     * 外部请求消费
     *
     * @param externalRequest 外部请求内容
     * @return
     */
    public Response externalConsume(ExternalRequest externalRequest) {
        logger.debug("ExternalRequest=" + JSONUtil.parseObj2JSON(externalRequest));

        long bef = System.currentTimeMillis();

        Request request = new Request();
        ObjectUtil.copyObject(externalRequest, request);

        ConsumeRequest consumeRequest = checkBeforExternalConsume(request);
        Response response = consume(consumeRequest, bef);

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;
        response.setConsumeTime(consumeTime);
        return response;
    }

    /**
     * 外部消费前检查
     */
    private ConsumeRequest checkBeforExternalConsume(Request request) {

        ConsumeRequest consumeRequest = new ConsumeRequest();
        request.setRequestType(ConsumerConstant.CONSUMER_REQUEST_TYPE_OUTER);

        String serviceName = request.getServiceName();
        String udspUser = request.getUdspUser();
        String udspPass = request.getToken();
        String appUser = request.getAppUser();
        String entity = request.getEntity();
        String type = request.getType();

        Current mcCurrent = null;

        //外部调用必输参数检查
        if (StringUtils.isBlank(serviceName) || StringUtils.isBlank(udspUser) || StringUtils.isBlank(udspPass) || StringUtils.isBlank(entity) || StringUtils.isBlank(type)) {
            consumeRequest.setError(ErrorCode.ERROR_000009);
            return consumeRequest;
        }

        //消费前公共输入参数检查
        //异同步类型检查和entity类型检查
        if (!(
                ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(type)
                        || ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(type)
        ) || !(
                ConsumerConstant.CONSUMER_ENTITY_STATUS.equalsIgnoreCase(entity)
                        || ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)
                        || ConsumerConstant.CONSUMER_ENTITY_STOP.equalsIgnoreCase(entity)
        )) {
            consumeRequest.setError(ErrorCode.ERROR_000010);
            return consumeRequest;
        }

        //检查用户身份合法性
        MessageResult messageResult = userService.validateUser(udspUser, udspPass);
        if (!messageResult.isStatus()) {
            consumeRequest.setError(ErrorCode.ERROR_000002);
            return consumeRequest;
        }

        boolean currentFlg = false;
        RcService rcService = rcServiceService.selectByServiceName(serviceName);
        if (rcService == null) {
            //没有注册服务
            consumeRequest.setError(ErrorCode.ERROR_000004);
            return consumeRequest;
        } else {
            //判断服务是否停用
            if (ConsumerConstant.SERVICE_STATUS_STOP.equals(rcService.getStatus())) {
                consumeRequest.setError(ErrorCode.ERROR_000017);
                return consumeRequest;
            }
            consumeRequest.setRcService(rcService);
            String serviceId = rcService.getPkId();
            String appType = rcService.getType();
            String appId = rcService.getAppId();
            String appName = getAppName(appType, appId);
            request.setAppName(appName);
            request.setAppId(appId);
            request.setAppType(appType);

            RcUserService rcUserService = rcUserServiceService.selectByUserIdAndServiceId(udspUser, serviceId);
            if (rcUserService == null) {
                //没有授权服务
                consumeRequest.setError(ErrorCode.ERROR_000008);
                return consumeRequest;
            } else {
                //IP段控制
                consumeRequest.setRcUserService(rcUserService);
                if (StringUtils.isNotBlank(rcUserService.getIpSection())) {
                    //没有拿到IP
                    if (StringUtils.isBlank(request.getRequestIp())) {
                        consumeRequest.setError(ErrorCode.ERROR_000006);
                        return consumeRequest;
                    }
                    boolean ipFlg = rcUserServiceService.checkIpSuitForSections(request.getRequestIp(), rcUserService.getIpSection());
                    if (!ipFlg) {
                        consumeRequest.setError(ErrorCode.ERROR_000006);
                        return consumeRequest;
                    }
                }
                //检查等待队列长度
                WaitNumResult waitNumResult = this.checkWaitNum(request, rcUserService);
                if (waitNumResult.getWaitQueueIsExist()) {
                    //判断等待队列是否满了，满了则退出
                    if (waitNumResult.getWaitQueueIsFull()) {
                        consumeRequest.setError(ErrorCode.ERROR_000016);
                        return consumeRequest;
                    } else {
                        //判断等待队列是否满了，未满则请求进入等待队列
                        waitNumResult.setIntoWaitQueue(true);
                    }
                } else {
                    //不存在等待队列
                    mcCurrent = this.checkCurrentNum(request, rcUserService);
                    if (mcCurrent == null) {
                        //并发数量不够
                        consumeRequest.setError(ErrorCode.ERROR_000003);
                        return consumeRequest;
                    } else {
                        //进入执行队列
                        waitNumResult.setIntoExeQueue(true);
                    }
                }
                //设置结果到消费请求实体
                consumeRequest.setWaitNumResult(waitNumResult);
            }
        }
        consumeRequest.setMcCurrent(mcCurrent);
        consumeRequest.setError(null);
        consumeRequest.setRequest(request);
        return consumeRequest;
    }

    /**
     * 内部请求消费
     *
     * @param innerRequest 内部请求内容
     * @param isAdmin      是否是管理员
     * @return
     */
    public Response innerConsume(InnerRequest innerRequest, boolean isAdmin) {
        logger.debug("InnerRequest=" + JSONUtil.parseObj2JSON(innerRequest));

        long bef = System.currentTimeMillis();

        Request request = new Request();
        ObjectUtil.copyObject(innerRequest, request);

        ConsumeRequest consumeRequest = checkBeforInnerConsume(request, isAdmin);
        Response response = consume(consumeRequest, bef);

        if (response.getPage() != null && response.getPage().getPageIndex() >= 1) {
            response.getPage().setPageIndex(response.getPage().getPageIndex() - 1);
        }

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;
        response.setConsumeTime(consumeTime);

        return response;
    }

    /**
     * 内部消费前检查
     */
    private ConsumeRequest checkBeforInnerConsume(Request request, boolean isAdmin) {

        ConsumeRequest consumeRequest = new ConsumeRequest();
        request.setRequestType(ConsumerConstant.CONSUMER_REQUEST_TYPE_INNER);

        String udspUser = request.getUdspUser();
        String appType = request.getAppType();
        String appId = request.getAppId();
        String appName = getAppName(appType, appId);
        request.setAppName(appName);
        request.setAppUser(udspUser);
        Current mcCurrent = null;
        String type = request.getType() == null ? "" : request.getType().toUpperCase();
        String entity = request.getEntity() == null ? "" : request.getEntity().toUpperCase();

        if (request.getPage() != null && request.getPage().getPageIndex() >= 0) {
            request.getPage().setPageIndex(request.getPage().getPageIndex() + 1);
        }

        //消费前公共输入参数检查
        //异同步类型检查和entity类型检查
        if (!(
                ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(type)
                        || ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(type)
        ) || !(
                ConsumerConstant.CONSUMER_ENTITY_STATUS.equalsIgnoreCase(entity)
                        || ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)
                        || ConsumerConstant.CONSUMER_ENTITY_STOP.equalsIgnoreCase(entity)
        )) {
            consumeRequest.setError(ErrorCode.ERROR_000010);
            return consumeRequest;
        }

        //并发数是否合法
        if (isAdmin) {
            // 管理员用户，直接访问
            // 管理员用户并发数...常量
            //McCurrentCountService mcCurrentCountService = McCurrentCountService.getInstance();
            if (ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(type)
                    && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)) {
                mcCurrent = runQueueService.checkSyncCurrent(request, adminMaxSyncNum);
            } else if (ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(type)
                    && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)) {
                mcCurrent = runQueueService.checkAsyncCurrent(request, adminMaxAsyncNum);
            }
            if (mcCurrent == null) {
                consumeRequest.setError(ErrorCode.ERROR_000003);
                return consumeRequest;
            }
        } else {
            // 非管理员用户，授权访问
            RcService rcService = rcServiceService.selectByAppTypeAndAppId(appType, appId);
            if (rcService == null) {
                //没有注册服务
                consumeRequest.setError(ErrorCode.ERROR_000004);
                return consumeRequest;
            } else {
                //判断服务是否停用
                if (ConsumerConstant.SERVICE_STATUS_STOP.equals(rcService.getStatus())) {
                    consumeRequest.setError(ErrorCode.ERROR_000017);
                    return consumeRequest;
                }
                consumeRequest.setRcService(rcService);
                String serviceId = rcService.getPkId();
                String serviceName = rcService.getName();
                request.setServiceName(serviceName);
                RcUserService rcUserService = rcUserServiceService.selectByUserIdAndServiceId(udspUser, serviceId);
                if (rcUserService == null) {
                    // 没有授权服务
                    consumeRequest.setError(ErrorCode.ERROR_000008);
                    return consumeRequest;
                } else {
                    consumeRequest.setRcUserService(rcUserService);
                    //IP段控制
                    logger.info("内部请求跳过ip段");
                    if (StringUtils.isNotBlank(rcUserService.getIpSection())) {
                        //没有拿到IP
                        if (StringUtils.isBlank(request.getRequestIp())) {
                            consumeRequest.setError(ErrorCode.ERROR_000006);
                            return consumeRequest;
                        }
                        boolean ipFlg = rcUserServiceService.checkIpSuitForSections(request.getRequestIp(),
                                rcUserService.getIpSection());
                        if (!ipFlg) {
                            consumeRequest.setError(ErrorCode.ERROR_000006);
                            return consumeRequest;
                        }
                    }
                    //检查等待队列长度
                    WaitNumResult waitNumResult = this.checkWaitNum(request, rcUserService);
                    if (waitNumResult.getWaitQueueIsExist()) {
                        //判断等待队列是否满了，满了则退出
                        if (waitNumResult.getWaitQueueIsFull()) {
                            consumeRequest.setError(ErrorCode.ERROR_000016);
                            return consumeRequest;
                        } else {
                            //判断等待队列是否满了，未满则请求进入等待队列
                            waitNumResult.setIntoWaitQueue(true);
                        }
                    } else {
                        //不存在等待队列
                        mcCurrent = this.checkCurrentNum(request, rcUserService);
                        if (mcCurrent == null) {
                            //并发数量不够
                            consumeRequest.setError(ErrorCode.ERROR_000003);
                            return consumeRequest;
                        } else {
                            //进入执行队列
                            waitNumResult.setIntoExeQueue(true);
                        }
                    }
                    //设置结果到消费请求实体
                    consumeRequest.setWaitNumResult(waitNumResult);
                }
            }
        }
        consumeRequest.setMcCurrent(mcCurrent);
        consumeRequest.setError(null);
        consumeRequest.setRequest(request);
        return consumeRequest;
    }

    /**
     * 消费
     *
     * @param consumeRequest
     * @param bef
     * @return
     */
    private Response consume(ConsumeRequest consumeRequest, long bef) {
        Request request = consumeRequest.getRequest();
        Current mcCurrent = consumeRequest.getMcCurrent();
        ErrorCode errorCode = consumeRequest.getError();
        RcUserService rcUserService = consumeRequest.getRcUserService();
        try {
            if (errorCode == null) {
                //如果并发对象为空，则从request获取
                if (mcCurrent == null) {
                    if (!ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
                        mcCurrent = McCommonUtil.getMcCurrent(consumeRequest.getRequest(), rcUserService.getMaxSyncNum());
                    } else if (ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(request.getType())
                            && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
                        mcCurrent = McCommonUtil.getMcCurrent(consumeRequest.getRequest(), rcUserService.getMaxSyncNum());
                    } else if (ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(request.getType())
                            && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
                        mcCurrent = McCommonUtil.getMcCurrent(consumeRequest.getRequest(), rcUserService.getMaxAsyncNum());
                    }
                    //设置mcCurrent到消费请求对象consumeRequest中
                    consumeRequest.setMcCurrent(mcCurrent);
                }
                // 消费
                return consume(consumeRequest);
            } else {
                Response response = new Response();
                this.setErrorResponse(response, consumeRequest, bef, errorCode.getValue(), errorCode.getName(), null);
                return response;
            }
        } finally {
            // 从内存数据库中修改并发信息
            if (mcCurrent != null) {
                logger.debug("从内存数据库中修改并发信息:" + mcCurrent.getPkId());
                if (!ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
                    this.runQueueService.reduceSyncCurrent(mcCurrent);
                } else if (ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(request.getType())
                        && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
                    this.runQueueService.reduceSyncCurrent(mcCurrent);
                } else if (RcConstant.UDSP_SERVICE_TYPE_MM.equalsIgnoreCase(request.getAppType())
                        && ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(request.getType())) {
                    // 模型管理异步是通过外部厂商的服务来做异步处理的，我们平台不需要做异步处理
                    this.runQueueService.reduceAsyncCurrent(mcCurrent);
                }
            }
        }
    }

    /**
     * 请求出错，设置错误码，错误信息
     *
     * @param response
     * @param errorCode
     * @param message
     * @return
     */
    public void setErrorResponse(Response response, ConsumeRequest consumeRequest, long bef, String errorCode, String message, String consumeId) {
        Request request = consumeRequest.getRequest();
        String appType = request.getAppType();
//        String type = request.getType() == null ? "" : request.getType().toUpperCase();
//        String entity = request.getEntity() == null ? "" : request.getEntity().toUpperCase();

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;

        /**
         * OLQ或OLQ_APP执行超时时，取消正在执行的SQL
         */
        if ((RcConstant.UDSP_SERVICE_TYPE_OLQ.equalsIgnoreCase(appType) || RcConstant.UDSP_SERVICE_TYPE_OLQ_APP.equals(appType))
                && ErrorCode.ERROR_000015.getValue().equals(errorCode)) {
            try {
                cancel(consumeId);
            } catch (SQLException e) {
                message = "取消正在执行的SQL出错，错误信息：" + e.getMessage();
            }
        }

        /**
         * 当等待/执行超时，发送报警信息
         */
        if (ErrorCode.ERROR_000014.getValue().equals(errorCode) || ErrorCode.ERROR_000015.getValue().equals(errorCode)) {
            RcUserService rcUserService = consumeRequest.getRcUserService();
            long timout = 0;
            if (ErrorCode.ERROR_000014.getValue().equals(errorCode)) {
                timout = ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(request.getType()) ?
                        rcUserService.getMaxSyncWaitTimeout() : rcUserService.getMaxAsyncWaitTimeout();
            } else {
                timout = ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(request.getType()) ?
                        rcUserService.getMaxSyncExecuteTimeout() : rcUserService.getMaxAsyncExecuteTimeout();
            }
            String msg = "请求参数：\n" + JSONUtil.parseObj2JSON(request)
                    + "\n告警信息：\n" + request.getUdspUser() + "用户"
                    + (ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(request.getType()) ? "【同步】" : "【异步】")
                    + "方式执行" + request.getServiceName() + "服务"
                    + (ErrorCode.ERROR_000014.getValue().equals(errorCode) ? "【等待】" : "【执行】")
                    + "超时，超时时间：" + timout + "秒，目前耗时：" + new BigDecimal((float) consumeTime / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "秒！";
            try {
                alarmService.send(rcUserService, msg);
            } catch (Exception e) {
                e.printStackTrace();
                message = "发送警报出错，错误信息：" + e.getMessage();
            }
        }

        if (StringUtils.isNotBlank(consumeId)) {
            consumeId = UdspCommonUtil.getConsumeId(JSONUtil.parseObj2JSON(request));
        }

        response.setStatus(Status.DEFEAT.getValue());
        response.setStatusCode(StatusCode.DEFEAT.getValue());
        response.setMessage(message);
        response.setErrorCode(errorCode);
        response.setConsumeTime(consumeTime);
        response.setConsumeId(consumeId);

        McConsumeLog mcConsumeLog = new McConsumeLog();
        mcConsumeLog.setPkId(consumeId);
        mcConsumeLog.setResponseContent("");
        mcConsumeLog.setErrorCode(errorCode);
        mcConsumeLog.setMessage(message);
        mcConsumeLog.setRequestStartTime(UdspDateUtil.getDateString(bef));
        mcConsumeLog.setRequestEndTime(UdspDateUtil.getDateString(now));

        //日志信息入库
        this.consumerLogToDb(request, mcConsumeLog, McConstant.MCLOG_STATUS_FAILED);

    }

    /**
     * 向数据库写消费日志
     *
     * @param request      请求信息
     * @param mcConsumeLog
     * @param status       结果状态(0：成功1：失败)
     */
    private void consumerLogToDb(Request request, McConsumeLog mcConsumeLog, String status) {
        //同步/异步
        if (StringUtils.isNotBlank(request.getType())) {
            mcConsumeLog.setSyncType(request.getType().toUpperCase());
        }
        //服务名称
        if (StringUtils.isNotBlank(request.getServiceName())) {
            mcConsumeLog.setServiceName(request.getServiceName());
        }
        //用户名称
        if (StringUtils.isNotBlank(request.getUdspUser())) {
            mcConsumeLog.setUserName(request.getUdspUser());
        }
        //请求类型
        if (StringUtils.isNotBlank(request.getRequestType())) {
            mcConsumeLog.setRequestType(request.getRequestType());
        }
        //设置应用类型
        if (StringUtils.isNotBlank(request.getAppType())) {
            mcConsumeLog.setAppType(request.getAppType().toUpperCase());
        }
        //设置应用名称
        if (StringUtils.isNotBlank(request.getAppName())) {
            mcConsumeLog.setAppName(request.getAppName());
        }
        //设置结果状态(0：成功 1：失败)
        mcConsumeLog.setStatus(status);
        mcConsumeLog.setRequestContent(JSONUtil.parseObj2JSON(request));
        mcConsumeLogService.insert(mcConsumeLog);
    }

    /**
     * 开始消费
     *
     * @param consumeRequest
     * @return
     */
    public Response consume(ConsumeRequest consumeRequest) {
        Request request = consumeRequest.getRequest();
        Current mcCurrent = consumeRequest.getMcCurrent();
        RcUserService rcUserService = consumeRequest.getRcUserService();
        WaitNumResult waitNumResult = consumeRequest.getWaitNumResult();

        String consumeId = UdspCommonUtil.getConsumeId(JSONUtil.parseObj2JSON(request));

        //如果任务在等待队列中，则mcCurrent带上等待任务的id
        if (null != waitNumResult && StringUtils.isNotBlank(waitNumResult.getWaitQueueTaskId())) {
            mcCurrent.setWaitQueueTaskId(waitNumResult.getWaitQueueTaskId());
        }
        //在等执行列中执行
        long bef = System.currentTimeMillis();
        long runStart = 0;
        long runEnd = 0;
        Response response = new Response();
        String appType = request.getAppType();
        String appId = request.getAppId();
        String type = request.getType() == null ? "" : request.getType().toUpperCase();
        String entity = request.getEntity() == null ? "" : request.getEntity().toUpperCase();
        Page page = request.getPage();
        String sql = request.getSql();
        String udspUser = request.getUdspUser();

        //在等待队列中等待执行(同步)
        if (null != waitNumResult && waitNumResult.isIntoWaitQueue()
                && CommonConstant.REQUEST_SYNC.equalsIgnoreCase(waitNumResult.getWaitQueueSyncType())) {
            Future<Boolean> futureTask = executorService.submit(new WaitQueueCallable(mcCurrent, syncCycleTimeInterval));
            try {
                long maxSyncWaitTimeout = (rcUserService == null || rcUserService.getMaxSyncWaitTimeout() == 0) ?
                        initParamService.getMaxSyncWaitTimeout() : rcUserService.getMaxSyncWaitTimeout();
                futureTask.get(maxSyncWaitTimeout, TimeUnit.SECONDS);
                mcCurrentService.insert(mcCurrent);
                runQueueService.addAsyncCurrent(mcCurrent);
            } catch (TimeoutException e) {
                this.setErrorResponse(response, consumeRequest, bef, ErrorCode.ERROR_000014.getValue(), ErrorCode.ERROR_000014.getName(), consumeId);
                return response;
            } catch (Exception e) {
                this.setErrorResponse(response, consumeRequest, bef, ErrorCode.ERROR_000007.getValue(),
                        ErrorCode.ERROR_000007.getName() + ":" + e.toString(), consumeId);
                return response;
            } finally {

            }
        }

        //解决应用测试的时候，没有配置同步、异步执行超时时间，则必须先进行判断
        long maxSyncExecuteTimeout = (rcUserService == null || rcUserService.getMaxSyncExecuteTimeout() == 0) ?
                initParamService.getMaxSyncExecuteTimeout() : rcUserService.getMaxSyncExecuteTimeout();

        //根据类型进入不同的处理逻辑
        String fileName = CreateFileUtil.getFileName(); // 生成随机的文件名
        runStart = System.currentTimeMillis();
        if (RcConstant.UDSP_SERVICE_TYPE_IQ.equalsIgnoreCase(appType)) {
            //开始iq消费
            if (ConsumerConstant.CONSUMER_ENTITY_STATUS.equalsIgnoreCase(entity)) {
                logger.debug("execute IQ STATUS");
                response = status(request.getConsumeId());
            } else if (ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(type)
                    && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)) {
                logger.debug("execute IQ ASYNC START");
                ThreadPool.execute(new IqAsyncService(consumeRequest, appId,
                        request.getData(), page, fileName, asyncCycleTimeInterval));
            } else if (ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(type)
                    && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)) {
                logger.debug("execute IQ SYNC START");
                Future<Response> iqFuture = executorService.submit(new IqSyncServiceCallable(request.getData(), appId, page));
                try {
                    response = iqFuture.get(maxSyncExecuteTimeout, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    this.setErrorResponse(response, consumeRequest, bef, ErrorCode.ERROR_000015.getValue(), ErrorCode.ERROR_000015.getName(), consumeId);
                    return response;
                } catch (Exception e) {
                    this.setErrorResponse(response, consumeRequest, bef, ErrorCode.ERROR_000007.getValue(), ErrorCode.ERROR_000007.getName() + ":" + e.toString(), consumeId);
                    return response;
                }
            }
        } else if (RcConstant.UDSP_SERVICE_TYPE_OLQ.equalsIgnoreCase(appType)) {
            //开始olq消费
            if (ConsumerConstant.CONSUMER_ENTITY_STATUS.equalsIgnoreCase(entity)) {
                logger.debug("execute OLQ STATUS");
                response = status(request.getConsumeId());
            } else if (ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(type)
                    && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)) {
                logger.debug("execute OLQ ASYNC START");
                ThreadPool.execute(new OlqAsyncService(consumeRequest, appId, sql, page,
                        RcConstant.UDSP_SERVICE_TYPE_OLQ, fileName, asyncCycleTimeInterval));
            } else if (ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(type)
                    && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)) {
                logger.debug("execute OLQ SYNC START");
                Future<Response> olqFuture = executorService.submit(new OlqSyncServiceCallable(consumeId, appId, sql, page));
                try {
                    response = olqFuture.get(maxSyncExecuteTimeout, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    this.setErrorResponse(response, consumeRequest, bef, ErrorCode.ERROR_000015.getValue(), ErrorCode.ERROR_000015.getName(), consumeId);
                    return response;
                } catch (Exception e) {
                    this.setErrorResponse(response, consumeRequest, bef, ErrorCode.ERROR_000007.getValue(), ErrorCode.ERROR_000007.getName() + ":" + e.toString(), consumeId);
                    return response;
                }
            }
        } else if (RcConstant.UDSP_SERVICE_TYPE_OLQ_APP.equals(appType)) {
            //开始olqApp消费
            if (ConsumerConstant.CONSUMER_ENTITY_STATUS.equalsIgnoreCase(entity)) {
                logger.debug("execute OLQ_APP STATUS");
                response = status(request.getConsumeId());
            } else if (ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(type)
                    && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)) {
                logger.debug("execute OLQ_APP ASYNC START");
                OLQApplicationDto olqApplicationDto = this.olqApplicationService.selectFullAppInfo(appId);
                String dsId = olqApplicationDto.getOlqApplication().getOlqDsId();
                //mcCurrent.setAppName(olqApplicationDto.getOlqApplication().getName());
                sql = this.olqApplicationService.getExecuteSQL(olqApplicationDto, request.getData());
                ThreadPool.execute(new OlqAsyncService(consumeRequest, dsId, sql, page,
                        RcConstant.UDSP_SERVICE_TYPE_OLQ_APP, fileName, asyncCycleTimeInterval));
            } else if (ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(type)
                    && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)) {
                logger.debug("execute OLQ_APP SYNC START");
                OLQApplicationDto olqApplicationDto = this.olqApplicationService.selectFullAppInfo(appId);
                String dsId = olqApplicationDto.getOlqApplication().getOlqDsId();
                sql = this.olqApplicationService.getExecuteSQL(olqApplicationDto, request.getData());
                Future<Response> olqAppFuture = executorService.submit(new OlqSyncServiceCallable(consumeId, dsId, sql, page));
                try {
                    response = olqAppFuture.get(maxSyncExecuteTimeout, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    this.setErrorResponse(response, consumeRequest, bef, ErrorCode.ERROR_000015.getValue(), ErrorCode.ERROR_000015.getName(), consumeId);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    this.setErrorResponse(response, consumeRequest, bef, ErrorCode.ERROR_000007.getValue(), ErrorCode.ERROR_000007.getName() + ":" + e.toString(), consumeId);
                    return response;
                }
            }
        } else if (RcConstant.UDSP_SERVICE_TYPE_MM.equalsIgnoreCase(appType)) {
            //开始MM消费
            if (ConsumerConstant.CONSUMER_ENTITY_STATUS.equalsIgnoreCase(entity)) {
                logger.debug("execute MM STATUS");
                request.setConsumeId(mcCurrent.getPkId()); //设置消费id到request对象
                response = mmRequestService.status(request, appId); //调用后台
            } else if (ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(type)
                    && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)) {
                logger.debug("execute MM ASYNC START");
                response = mmRequestService.start(mcCurrent, appId, request);
            } else if (ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(type)
                    && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)) {
                logger.debug("execute MM SYNC START");
                response = mmRequestService.start(mcCurrent, appId, request);
            }
        } else if (RcConstant.UDSP_SERVICE_TYPE_RTS_PRODUCER.equalsIgnoreCase(appType)) {
            logger.debug("execute RTS_PRODUCER SYNC START");
            response = rtsSyncService.startProducer(appId, request.getDataStream());
        } else if (RcConstant.UDSP_SERVICE_TYPE_RTS_CONSUMER.equalsIgnoreCase(appType)) {
            logger.debug("execute RTS_CONSUMER SYNC START");
            response = rtsSyncService.startConsumer(appId, request.getTimeout());
        } else if (RcConstant.UDSP_SERVICE_TYPE_IM.equalsIgnoreCase(appType)) {
            logger.debug("execute IM SYNC START");
            Future<Response> imFuture = executorService.submit(new ImSyncServiceCallable(appId, request.getData()));
            try {
                response = imFuture.get(maxSyncExecuteTimeout, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                this.setErrorResponse(response, consumeRequest, bef, ErrorCode.ERROR_000015.getValue(), ErrorCode.ERROR_000015.getName(), consumeId);
                return response;
            } catch (Exception e) {
                this.setErrorResponse(response, consumeRequest, bef, ErrorCode.ERROR_000007.getValue(), ErrorCode.ERROR_000007.getName() + ":" + e.toString(), consumeId);
                return response;
            }
        }
        runEnd = System.currentTimeMillis();

        response.setConsumeId(mcCurrent.getPkId());
        long now = System.currentTimeMillis();
        long consumeTime = now - bef;
        response.setConsumeTime(consumeTime);

        if (ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(type)
                && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(entity)
                && !RcConstant.UDSP_SERVICE_TYPE_MM.equalsIgnoreCase(appType)) {
            if ("admin".equals(udspUser)) udspUser = "udsp" + udspUser;
            String ftpFilePath = FTPClientConfig.getRootpath() + "/" + udspUser + "/" + FTPClientConfig.getUsername()
                    + "/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + fileName + CreateFileUtil.DATA_FILE_SUFFIX;
            response.setResponseContent(ftpFilePath);
            response.setStatusCode(StatusCode.SUCCESS.getValue());
            response.setStatus(Status.SUCCESS.getValue());
        } else {
            this.writeSyncLog(mcCurrent, bef, now, runStart, runEnd, request, response);
        }

        return response;
    }

    /**
     * 杀死正在执行的SQL
     */
    private void cancel(String consumeId) throws SQLException {
        // 杀死正在执行的SQL
        Statement stmt = OLQCommUtil.removeStatement(consumeId);
        if (stmt != null) {
            stmt.cancel();
            stmt.close();
        }
    }

    /**
     * 写同步日志到数据库
     */

    private void writeSyncLog(Current mcCurrent, long bef, long now, long runStart, long runEnd, Request request, Response response) {
        McConsumeLog mcConsumeLog = new McConsumeLog();
        mcConsumeLog.setPkId(mcCurrent.getPkId());
        mcConsumeLog.setRequestStartTime(UdspDateUtil.getDateString(bef));
        mcConsumeLog.setRequestEndTime(UdspDateUtil.getDateString(now));
        if (runStart != 0) {
            mcConsumeLog.setRunStartTime(UdspDateUtil.getDateString(runStart));
        }
        if (runEnd != 0) {
            mcConsumeLog.setRunEndTime(UdspDateUtil.getDateString(runEnd));
        }
        if (StringUtils.isNotBlank(response.getErrorCode())) {
            mcConsumeLog.setErrorCode(response.getErrorCode());
        }
        if (StringUtils.isNotBlank(response.getMessage())) {
            mcConsumeLog.setMessage(response.getMessage());
        }
        if (CommonConstant.REQUEST_SYNC.equalsIgnoreCase(mcCurrent.getSyncType())) {
            mcConsumeLog.setSyncType(mcCurrent.getSyncType().toUpperCase());
        } else if (CommonConstant.REQUEST_ASYNC.equalsIgnoreCase(mcCurrent.getSyncType())) {
            mcConsumeLog.setSyncType(mcCurrent.getSyncType().toUpperCase());
        } else {
            mcConsumeLog.setSyncType("NULL");
        }
        if (StringUtils.isNotBlank(response.getResponseContent())) {
            mcConsumeLog.setResponseContent(response.getResponseContent());
        }
        if (Status.SUCCESS.getValue().equals(response.getStatus())
                || Status.RUNING.getValue().equals(response.getStatus())) {
            consumerLogToDb(request, mcConsumeLog, McConstant.MCLOG_STATUS_SUCCESS);
        } else {
            consumerLogToDb(request, mcConsumeLog, McConstant.MCLOG_STATUS_FAILED);
        }
    }

    private String getAppName(String appType, String appId) {
        String appName = "";
        if (RcConstant.UDSP_SERVICE_TYPE_IQ.equalsIgnoreCase(appType)) {
            IqApplication iqApplication = iqApplicationService.select(appId);
            appName = iqApplication.getName();
        } else if (RcConstant.UDSP_SERVICE_TYPE_OLQ.equalsIgnoreCase(appType)) {
            ComDatasource comDatasource = comDatasourceService.select(appId);
            appName = comDatasource.getName();
        } else if (RcConstant.UDSP_SERVICE_TYPE_MM.equalsIgnoreCase(appType)) {
            MmApplication mmApplication = mmApplicationService.select(appId);
            appName = mmApplication.getName();
        } else if (RcConstant.UDSP_SERVICE_TYPE_RTS_PRODUCER.equalsIgnoreCase(appType)) {
            RtsProducer rtsProducer = rtsProducerService.select(appId);
            appName = rtsProducer.getName();
        } else if (RcConstant.UDSP_SERVICE_TYPE_RTS_CONSUMER.equalsIgnoreCase(appType)) {
            RtsConsumer rtsConsumer = rtsConsumerService.select(appId);
            appName = rtsConsumer.getName();
        } else if (RcConstant.UDSP_SERVICE_TYPE_OLQ_APP.equalsIgnoreCase(appType)) {
            OLQApplication olqApplication = olqApplicationService.select(appId);
            appName = olqApplication.getName();
        }
        return appName;
    }

    private Response status(String consumeId) {
        Status status = Status.OTHER;
        StatusCode statusCode = StatusCode.OTHER;
        String message = "没有消费";
        Response response = new Response();
        // 检查消费ID是否为空
        if (StringUtils.isBlank(consumeId)) {
            response.setStatus(Status.DEFEAT.getValue());
            response.setStatusCode(StatusCode.DEFEAT.getValue());
            response.setErrorCode(ErrorCode.ERROR_000009.getValue());
            response.setMessage(ErrorCode.ERROR_000009.getName());
            return response;
        }
        Current selectMcCurrent = mcCurrentService.select(consumeId);
        if (selectMcCurrent != null) {// 正在消费
            status = Status.RUNING;
            statusCode = StatusCode.RUNING;
            message = ConsumerConstant.CONSUME_ACTION_RUNNING;
        } else {// 在写日志或已写完
            // 查询消费日志
            McConsumeLog mcConsumeLog = mcConsumeLogService.select(consumeId);
            if (mcConsumeLog != null) { // 消费完成
                status = Status.SUCCESS;
                statusCode = StatusCode.SUCCESS;
                if (ConsumerConstant.CONSUME_REQUEST_STATUS_SUCCESS.equals(mcConsumeLog.getStatus())) {
                    message = ConsumerConstant.CONSUME_ACTION_SUCCESS;
                    // 输出消费成功的文件位置信息
                    if (StringUtils.isNotBlank(mcConsumeLog.getRequestContent())) {
                        response.setResponseContent(mcConsumeLog.getResponseContent());
                    }
                } else {
                    message = ConsumerConstant.CONSUME_ACTION_FAILED;
                    //输出具体失败消息
                    if (StringUtils.isNotBlank(mcConsumeLog.getMessage())) {
                        response.setResponseContent("消费id为‘" + consumeId + "’的异步任务失败原因：" + mcConsumeLog.getMessage());
                    }
                }
            } else { // 正在消费
                status = Status.RUNING;
                statusCode = StatusCode.RUNING;
                message = ConsumerConstant.CONSUME_ACTION_RUNNING;
            }
        }
        response.setStatus(status.getValue());
        response.setStatusCode(statusCode.getValue());
        response.setMessage(message);
        return response;
    }

    /**
     * 检查用户-服务并发数是否达到设定的最大并发数
     * 达到最大并发数返回 false
     *
     * @param request
     * @param rcUserService
     * @return
     */
    private Current checkCurrentNum(Request request, RcUserService rcUserService) {
        Current mcCurrent = null;
        logger.info(Thread.currentThread().getId() + "检查执行队列-开始");
        //并发控制
        if (!ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
            mcCurrent = runQueueService.checkSyncCurrent(request, rcUserService.getMaxSyncNum());
        } else if (ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(request.getType())
                && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
            mcCurrent = runQueueService.checkSyncCurrent(request, rcUserService.getMaxSyncNum());
        } else if (ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(request.getType())
                && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
            mcCurrent = runQueueService.checkAsyncCurrent(request, rcUserService.getMaxAsyncNum());
        }
        logger.info(Thread.currentThread().getId() + "检查执行队列-结束");
        return mcCurrent;
    }

    /**
     * 检查用户-服务并发数是否达到设定的最大并发数
     * 达到最大并发数返回 false
     *
     * @param request
     * @param rcUserService
     * @return
     */
    private WaitNumResult checkWaitNum(Request request, RcUserService rcUserService) {
        WaitNumResult waitNumResult = new WaitNumResult();
        if (!ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
            waitNumResult.setWaitQueueIsExist(false);
            return waitNumResult;
        }
        int maxSyncWaitNum = rcUserService.getMaxSyncWaitNum();
        int maxAsyncWaitNum = rcUserService.getMaxAsyncWaitNum();
        //等待队列长度为0，则返回
        if (maxSyncWaitNum == 0
                && ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(request.getType())
                && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
            waitNumResult.setWaitQueueIsExist(false);
            return waitNumResult;
        } else if (maxAsyncWaitNum == 0
                && ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(request.getType())
                && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
            waitNumResult.setWaitQueueIsExist(false);
            return waitNumResult;
        }
        //等待队列存在
        waitNumResult.setWaitQueueIsExist(true);
        logger.info(Thread.currentThread().getId() + "检查等待队列长度-开始");
        //判断请求的类型，根据不同的类型进行请求队列判断
        QueueIsFullResult isFullResult = null;
        if (ConsumerConstant.CONSUMER_TYPE_SYNC.equalsIgnoreCase(request.getType())
                && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
            isFullResult = mcWaitQueueService.checkWaitQueueIsFull(request, maxSyncWaitNum);
        } else if (ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(request.getType())
                && ConsumerConstant.CONSUMER_ENTITY_START.equalsIgnoreCase(request.getEntity())) {
            isFullResult = mcWaitQueueService.checkWaitQueueIsFull(request, maxAsyncWaitNum);
        }
        if (null != isFullResult) {
            waitNumResult.setWaitQueueIsFull(isFullResult.isWaitQueueIsFull());
            waitNumResult.setIntoWaitQueue(!isFullResult.isWaitQueueIsFull());
            waitNumResult.setWaitQueueTaskId(isFullResult.getWaitQueueTaskId());
        }
        waitNumResult.setWaitQueueSyncType(request.getType().toUpperCase());
        logger.info(Thread.currentThread().getId() + "检查等待队列长度-完成");
        return waitNumResult;
    }

}
