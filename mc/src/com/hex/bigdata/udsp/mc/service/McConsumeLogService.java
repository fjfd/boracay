package com.hex.bigdata.udsp.mc.service;

import com.hex.bigdata.udsp.mc.dao.McConsumeLogMapper;
import com.hex.bigdata.udsp.mc.dto.McConsumeLogView;
import com.hex.bigdata.udsp.mc.model.McConsumeLog;
import com.hex.goframe.model.Page;
import com.hex.goframe.service.BaseService;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

/**
 * Created by junjiem on 2017-2-23.
 */
@Service
public class McConsumeLogService extends BaseService {
    private static Logger logger = LoggerFactory.getLogger(McConsumeLogService.class);

    @Autowired
    private McConsumeLogMapper mcConsumeLogMapper;

    private static final FastDateFormat ymdFormat = FastDateFormat.getInstance("yyyy-MM-dd");
    private static final FastDateFormat ymdhmsFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    /**
     * 保留最近N天的消费日志
     */
    @Value("${keep.consume.log.period:100}")
    private int keepConsumeLogPeriod;

    @Transactional
    public String insert(McConsumeLog mcConsumeLog) {
        if (mcConsumeLogMapper.insert(mcConsumeLog.getPkId(), mcConsumeLog)) {
            return mcConsumeLog.getPkId();
        }
        return "";
    }

    public McConsumeLog select(String pkId) {
        return mcConsumeLogMapper.select(pkId);
    }

    public List<McConsumeLog> select(McConsumeLogView mcConsumeLogView, Page page) {
        return mcConsumeLogMapper.select(mcConsumeLogView, page);
    }

    public List<McConsumeLog> select(McConsumeLogView mcConsumeLogView) {
        return mcConsumeLogMapper.select(mcConsumeLogView);
    }

    /**
     * 清空过期的消费日志
     */
    public boolean cleanOutmodedConsumeLog() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1 * keepConsumeLogPeriod);
        String date = ymdhmsFormat.format(calendar.getTime());
        logger.info("清空" + date + "和其之前的消费日志记录！");
        return mcConsumeLogMapper.clean(date);
    }
}
