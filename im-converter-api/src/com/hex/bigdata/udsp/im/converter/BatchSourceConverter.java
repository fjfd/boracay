package com.hex.bigdata.udsp.im.converter;

import com.hex.bigdata.udsp.im.converter.model.Model;

/**
 * Created by JunjieM on 2017-9-5.
 */
public interface BatchSourceConverter extends SourceConverter, BatchConverter {

    /**
     * 创建源引擎Schema
     *
     * @param model
     * @return
     * @throws Exception
     */
    void createSourceEngineSchema(Model model) throws Exception;

    /**
     * 删除源引擎Schema
     *
     * @param model
     * @return
     * @throws Exception
     */
    void dropSourceEngineSchema(Model model) throws Exception;

    /**
     * 创建源引擎Schema（只针对非暴力查询模式时使用）
     *
     * @param model
     * @param engineSchemaName
     * @throws Exception
     */
    void createSourceEngineSchema(Model model, String engineSchemaName) throws Exception;
}
