package com.hex.bigdata.udsp.task;

import com.hex.bigdata.udsp.common.util.CreateFileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 临时文件定时清理
 */
@Component
public class TempFileCleanTask {
    /**
     * 日志打印
     */
    private static Logger logger = LogManager.getLogger(TempFileCleanTask.class);

    /**
     * 清理N天前的数据，即保留最近N天数据
     */
    @Value("${download.tempfile.clean.days:7}")
    private int downloadTempfileCleanDays;

    /**
     * UDSP项目临时文件夹，文件清理
     */
    @Scheduled(cron = "${download.tempfile.clean.task:0 30 2 ? * WED}")
    public void downLoadTempFileCleanTask() {
        long time = downloadTempfileCleanDays * 24 * 60 * 60 * 1000;
        /**
         * 检查路径
         */
        String checkPath = CreateFileUtil.getLocalDirPath();
        System.err.println(checkPath);
        File fileDir = new File(checkPath);
        //判断临时文件文件夹是否存在
        if (!fileDir.exists()) {
            return;
        }
        //获取文件夹下所有文件
        File[] fileArray = fileDir.listFiles();
        for (File fileItem : fileArray) {
            //文件最后修改时间
            long lastModified = fileItem.lastModified();
            //当前时间
            long current = System.currentTimeMillis();
            //文件最后修改时间为当前时间7天以前的都可以进行删除
            if (current - lastModified > time) {
                //删除文件
                fileItem.delete();
            }
        }
    }
}
