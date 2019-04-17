package com.hex.bigdata.udsp.listener;


import com.hex.bigdata.udsp.common.service.FtpUserManagerService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Repository;

/**
 * Spring初始化完成时指定执行该类
 */
@Repository
public class ListenerContent implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LogManager.getLogger (ListenerContent.class);

    @Value("${java.security.krb5.conf}")
    private String javaSecurityKrb5Conf;

    @Value("${java.security.auth.login.config}")
    private String javaSecurityAuthLoginConfig;

    @Value("${javax.security.auth.useSubjectCredsOnly}")
    private String javaxSecurityAuthUseSubjectCredsOnly;

    @Value("${sun.security.krb5.debug}")
    private String sunSecurityKrb5Debug;

    @Value("${ipc.client.fallback-to-simple-auth-allowed}")
    private String ipcClientFallbackToSimpleAuthAllowed;

    @Autowired
    private FtpUserManagerService ftpUserManagerService;
//    @Autowired
//    private RunQueueService runQueueService;
//    @Autowired
//    private WaitQueueService waitQueueService;

    /**
     * Spring初始化完成时指定执行该方法
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (StringUtils.isNotBlank (javaSecurityKrb5Conf)) {
            System.setProperty ("java.security.krb5.conf", javaSecurityKrb5Conf);
            logger.info ("系统配置：java.security.krb5.conf = " + System.getProperty ("java.security.krb5.conf"));
        }
        if (StringUtils.isNotBlank (javaSecurityAuthLoginConfig)) {
            System.setProperty ("java.security.auth.login.config", javaSecurityAuthLoginConfig);
            logger.info ("系统配置：java.security.auth.login.config = " + System.getProperty ("java.security.auth.login.config"));
        }
        if (StringUtils.isNotBlank (javaxSecurityAuthUseSubjectCredsOnly)) {
            System.setProperty ("javax.security.auth.useSubjectCredsOnly", javaxSecurityAuthUseSubjectCredsOnly);
            logger.info ("系统配置：javax.security.auth.useSubjectCredsOnly = " + System.getProperty ("javax.security.auth.useSubjectCredsOnly"));
        }
        if (StringUtils.isNotBlank (sunSecurityKrb5Debug)) {
            System.setProperty ("sun.security.krb5.debug", sunSecurityKrb5Debug);
            logger.info ("系统配置：sun.security.krb5.debug = " + System.getProperty ("sun.security.krb5.debug"));
        }
        if (StringUtils.isNotBlank (ipcClientFallbackToSimpleAuthAllowed)) {
            System.setProperty ("ipc.client.fallback-to-simple-auth-allowed", ipcClientFallbackToSimpleAuthAllowed);
            logger.info ("系统配置：ipc.client.fallback-to-simple-auth-allowed = " + System.getProperty ("ipc.client.fallback-to-simple-auth-allowed"));
        }

        logger.info ("启动服务时初始化FTP服务器上UDSP用户和用户组");
        ftpUserManagerService.init ();

        /*
        这里无法初始化时清空队列信息，因为这里WebApplicationContext还没有初始化
         */
//        logger.info("启动服务时清空本机的运行队列信息");
//        runQueueService.emptyLocalCache();
//        logger.info("启动服务时清空本机的等待队列信息");
//        waitQueueService.emptyLocalCache();

    }
}
