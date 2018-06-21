package com.hex.bigdata.udsp.im.converter;

import com.hex.bigdata.udsp.im.converter.model.Model;

/**
 * Created by JunjieM on 2017-9-5.
 */
public interface BatchTargetConverter extends TargetConverter, BatchConverter {

    /**
     * 批量构建
     * @param model
     * @throws Exception
     */
    void buildBatch(String key, Model model) throws Exception;

    /**
     * 创建目标引擎Schema
     *
     * @param model
     * @return
     * @throws Exception
     */
    void createTargetEngineSchema(Model model) throws Exception;

    /**
     * 删除目标引擎Schema
     *
     * @param model
     * @return
     * @throws Exception
     */
    void dropTargetEngineSchema(Model model) throws Exception;
}
