package com.hex.bigdata.udsp.im.converter;

import com.hex.bigdata.udsp.im.converter.model.MetadataCol;
import com.hex.bigdata.udsp.im.converter.model.Model;

import java.util.List;

/**
 * Created by JunjieM on 2017-9-5.
 */
public interface SourceConverter extends Converter {

    /**
     * 获取字段信息
     *
     * @param model
     * @return
     */
    List<MetadataCol> columnInfo(Model model);

}
