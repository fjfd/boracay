package com.hex.bigdata.udsp.rts.dto;

import com.hex.bigdata.udsp.rts.model.RtsMetadata;
import com.hex.bigdata.udsp.rts.model.RtsMetadataCol;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tomnic on 2017/2/28.
 */
public class RtsMetadataColsView implements Serializable {

    private RtsMetadata rtsMetadata;
    private List<RtsMetadataCol> rtsMetadataColList;

    public RtsMetadata getRtsMetadata() {
        return rtsMetadata;
    }

    public void setRtsMetadata(RtsMetadata rtsMetadata) {
        this.rtsMetadata = rtsMetadata;
    }

    public List<RtsMetadataCol> getRtsMetadataColList() {
        return rtsMetadataColList;
    }

    public void setRtsMetadataColList(List<RtsMetadataCol> rtsMetadataColList) {
        this.rtsMetadataColList = rtsMetadataColList;
    }
}
