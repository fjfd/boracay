package com.hex.bigdata.udsp.im.converter;

import com.hex.bigdata.udsp.im.converter.model.Metadata;
import com.hex.bigdata.udsp.im.converter.model.MetadataCol;

import java.util.List;

/**
 * Created by JunjieM on 2017-9-5.
 */
public interface TargetConverter extends Converter {

    /**
     * 获取字段信息
     *
     * @param metadata
     * @return
     */
    List<MetadataCol> columnInfo(Metadata metadata);

    /**
     * 创建Schema
     *
     * @param metadata
     * @return
     * @throws Exception
     */
    void createSchema(Metadata metadata) throws Exception;

    /**
     * 删除Schema
     *
     * @param metadata
     * @return
     * @throws Exception
     */
    void dropSchema(Metadata metadata) throws Exception;

    /**
     * 检查Schema
     *
     * @param metadata
     * @return
     * @throws Exception
     */
    boolean checkSchema(Metadata metadata) throws Exception;
}
