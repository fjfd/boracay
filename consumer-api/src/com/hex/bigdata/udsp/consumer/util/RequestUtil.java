package com.hex.bigdata.udsp.consumer.util;

import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.consumer.model.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JunjieM on 2018-9-7.
 */
public class RequestUtil {
    public static final String CONSUME_RTS_DATASTREAM = "dataStream";

    /**
     * JSON字符串转Request对象
     *
     * @param json
     * @return
     */
    public static Request jsonToRequest(String json) {
        Map<String, Class> classMap = new HashMap<>();
        classMap.put(CONSUME_RTS_DATASTREAM, Map.class);
        return JSONUtil.parseJSON2Obj(json, Request.class, classMap);
    }
}
