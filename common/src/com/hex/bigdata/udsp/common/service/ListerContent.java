package com.hex.bigdata.udsp.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Repository;

/**
 * Spring初始化完成时指定执行该类
 */
@Repository
public class ListerContent implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private FtpUserManagerService ftpUserManagerService;

    /**
     * Spring初始化完成时指定执行该方法
     *
     * @param contextRefreshedEvent
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ftpUserManagerService.init();
    }
}
