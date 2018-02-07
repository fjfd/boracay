package com.hex.bigdata.udsp.rc.dto;

import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.bigdata.udsp.rc.model.RcUserService;

import java.util.List;

/**
 * Created by JunjieM on 2017-11-22.
 */
public class RcUserServiceDto {
    private RcUserService rcUserService;
    private List<ComProperties> alarmProperties; // 告警配置

    public RcUserService getRcUserService() {
        return rcUserService;
    }

    public void setRcUserService(RcUserService rcUserService) {
        this.rcUserService = rcUserService;
    }

    public List<ComProperties> getAlarmProperties() {
        return alarmProperties;
    }

    public void setAlarmProperties(List<ComProperties> alarmProperties) {
        this.alarmProperties = alarmProperties;
    }
}
