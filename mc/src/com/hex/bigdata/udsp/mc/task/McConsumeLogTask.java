package com.hex.bigdata.udsp.mc.task;

import com.hex.bigdata.udsp.mc.service.McConsumeLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 消费日志的调度任务
 */
@Component
public class McConsumeLogTask {
    private static Logger logger = LogManager.getLogger(McConsumeLogTask.class);

    @Autowired
    private McConsumeLogService mcConsumeLogService;

    @Scheduled(cron = "${clean.consume.log.cron.expression:0 0 0 * * ?}")
    public void cleanOutmodedConsumeLog() {
        logger.debug("清空过期的消费日志【开始】");
        mcConsumeLogService.cleanOutmodedConsumeLog();
        logger.debug("清空过期的消费日志【结束】");
    }

}
