package com.hex.bigdata.udsp.mc.util;

import com.hex.bigdata.udsp.common.util.HostUtil;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.mc.model.Current;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;

public class McCommonUtil {

    private static final FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 获取并发信息
     *
     * @param request
     * @param maxCurrentNum
     * @return
     */
    public static Current getMcCurrent(Request request, int maxCurrentNum) {
        Current mcCurrent = new Current();
        String requestContent = JSONUtil.parseObj2JSON(request);
        String consumeId = HostUtil.getConsumeId(requestContent);
        mcCurrent.setStartTime(format.format(new Date()));
        mcCurrent.setServiceName(request.getServiceName());
        mcCurrent.setUserName(request.getUdspUser());
        mcCurrent.setAppType(request.getAppType());
        mcCurrent.setAppName(request.getAppName());
        mcCurrent.setRequestType(request.getRequestType());
        mcCurrent.setPid(consumeId);
        mcCurrent.setAppId(request.getAppId());
        mcCurrent.setSyncType(request.getType().toUpperCase());
        mcCurrent.setPkId(consumeId);
        mcCurrent.setMaxCurrentNum(maxCurrentNum);
        mcCurrent.setRequestContent(requestContent);
        return mcCurrent;
    }
}
