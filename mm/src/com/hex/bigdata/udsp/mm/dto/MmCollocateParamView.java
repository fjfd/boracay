package com.hex.bigdata.udsp.mm.dto;

import com.hex.bigdata.udsp.mm.model.MmModelInfo;
import com.hex.bigdata.udsp.mm.model.MmModelParam;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TZBank on 17-4-19.
 */
public class MmCollocateParamView implements Serializable {
    private MmModelInfo modelInfo;

    private List<MmModelParam> modelExcuteParam;

    private List<MmModelParam> modelReturnParam;

    public MmModelInfo getModelInfo() {
        return modelInfo;
    }

    public void setModelInfo(MmModelInfo modelInfo) {
        this.modelInfo = modelInfo;
    }


    public List<MmModelParam> getModelExcuteParam() {
        return modelExcuteParam;
    }

    public void setModelExcuteParam(List<MmModelParam> modelExcuteParam) {
        this.modelExcuteParam = modelExcuteParam;
    }

    public List<MmModelParam> getModelReturnParam() {
        return modelReturnParam;
    }

    public void setModelReturnParam(List<MmModelParam> modelReturnParam) {
        this.modelReturnParam = modelReturnParam;
    }
}
