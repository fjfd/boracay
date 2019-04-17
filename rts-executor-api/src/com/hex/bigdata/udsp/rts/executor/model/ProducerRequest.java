package com.hex.bigdata.udsp.rts.executor.model;

import org.apache.commons.lang.time.FastDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by junjiem on 2017-2-20.
 */
public class ProducerRequest {
    private static final FastDateFormat format19 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final FastDateFormat format23 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss:SSS");

    private Application application;
    private List<Map<String, String>> messageDatas;

    public ProducerRequest(Application application, List<Map<String, String>> messageDatas) {
        this.application = application;
        for (Map<String, String> message : messageDatas) {
            message.put("time", format23.format(new Date()));
        }
        this.messageDatas = messageDatas;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public List<Map<String, String>> getMessageDatas() {
        return messageDatas;
    }

    public void setMessageDatas(List<Map<String, String>> messageDatas) {
        for (Map<String, String> message : messageDatas) {
            message.put("time", format23.format(new Date()));
        }
        this.messageDatas = messageDatas;
    }
}
