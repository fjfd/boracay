package com.hex.bigdata.udsp.task;

import com.hex.bigdata.udsp.common.service.InitParamService;
import com.hex.bigdata.udsp.service.HeartbeatCheckService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时心跳检查
 */
@Component
public class HeartbeatCheckTask {
    private static Logger logger = LogManager.getLogger(HeartbeatCheckTask.class);

    @Autowired
    private HeartbeatCheckService heartbeatCheckService;
    @Autowired
    private InitParamService initParamService;

    /**
     * 发送本服务心跳的任务
     */
    @Scheduled(cron = "${send.local.heartbeat.task:*/20 * * * * ?}")
    public void sendLocalHeartbeatTask() {
        if (initParamService.isClusterMode()) {
            logger.debug("发送本服务心跳【开始】");
            heartbeatCheckService.sendLocalHeartbeat();
            logger.debug("发送本服务心跳【结束】");
        } else {
            logger.debug("单机模式不需要发送心跳");
        }
    }

    /**
     * 检查集群服务心跳的任务
     */
    @Scheduled(cron = "${check.cluster.heartbeat.task:*/30 * * * * ?}")
    public void checkClusterHeartbeatTask() {
        if (initParamService.isClusterMode()) {
            logger.debug("检测集群服务心跳【开始】");
            heartbeatCheckService.checkClusterHeartbeat();
            logger.debug("检测集群服务心跳【结束】");
        } else {
            logger.debug("单机模式不需要检查心跳");
        }
    }
}
