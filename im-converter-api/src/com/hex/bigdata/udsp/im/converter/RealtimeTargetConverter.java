package com.hex.bigdata.udsp.im.converter;

import com.hex.bigdata.udsp.im.converter.model.Model;

/**
 * Created by JunjieM on 2017-9-5.
 */
public interface RealtimeTargetConverter extends TargetConverter, RealtimeConverter {

    /**
     * 实时构建
     *
     * @param key
     * @param model
     * @throws Exception
     */
    void buildRealtime(String key, Model model) throws Exception;

}
