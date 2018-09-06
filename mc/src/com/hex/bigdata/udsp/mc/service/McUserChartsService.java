package com.hex.bigdata.udsp.mc.service;

import com.hex.bigdata.udsp.mc.dao.McUserChartsMapper;
import com.hex.bigdata.udsp.mc.dto.McChartsView;
import com.hex.bigdata.udsp.mc.dto.McUserChartsView;
import com.hex.goframe.model.Page;
import com.hex.goframe.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * Author: tomnic.wang
 * DATE:2017/8/28
 * TIME:11:08
 */
@Service
public class McUserChartsService extends BaseService {

    @Autowired
    private McUserChartsMapper userChartsMapper;

    public List<McUserChartsView> selectUserStatis (McUserChartsView userChartsView, Page page){
        return this.userChartsMapper.selectUserStatis(userChartsView,page);
    }

    /**
     * 指定服务+用户，秒级别统计
     * @param timeStart
     * @param timeEnd
     * @param serverName
     * @param userName
     * @return
     */
    public List<McChartsView> userChart1Second(String timeStart, String timeEnd, String serverName,String userName) {
        return userChartsMapper.userChart1Second(new McChartsView(timeStart, timeEnd, serverName, userName));
    }

    /**
     * 指定服务+用户，分钟级别统计
     * @param timeStart
     * @param timeEnd
     * @param serverName
     * @param userName
     * @return
     */
    public List<McChartsView> userChart1Minute(String timeStart, String timeEnd, String serverName,String userName) {
        return userChartsMapper.userChart1Minute(new McChartsView(timeStart, timeEnd, serverName, userName));
    }

    /**
     * 指定服务+用户，小时级别统计
     * @param timeStart
     * @param timeEnd
     * @param serverName
     * @param userName
     * @return
     */
    public List<McChartsView> userChart1Hour(String timeStart, String timeEnd, String serverName,String userName) {
        return userChartsMapper.userChart1Hour(new McChartsView(timeStart, timeEnd, serverName, userName));
    }

    /**
     * 指定服务+用户，天级别统计
     * @param timeStart
     * @param timeEnd
     * @param serverName
     * @param userName
     * @return
     */
    public List<McChartsView> userChart1Day(String timeStart, String timeEnd, String serverName,String userName) {
        return userChartsMapper.userChart1Day(new McChartsView(timeStart, timeEnd, serverName, userName));
    }

    public List<McChartsView> statsServiceGroupByUserName(String dtStartStr, String dtEndStr, String userName) {
        return userChartsMapper.statsServiceGroupByUserName(new McChartsView(dtStartStr, dtEndStr, "",userName));
    }


    public List<McChartsView> serviceTimeMinute(String dtStartStr, String dtEndStr, String serviceName, String userName) {
        return userChartsMapper.serviceTimeMinute(new McChartsView(dtStartStr, dtEndStr, serviceName, userName));
    }

    public List<McChartsView> serviceTimeHour(String dtStartStr, String dtEndStr, String serviceName, String userName) {
        return userChartsMapper.serviceTimeHour(new McChartsView(dtStartStr, dtEndStr, serviceName, userName));
    }

    public List<McChartsView> serviceTimeDay(String dtStartStr, String dtEndStr, String serviceName, String userName) {
        return userChartsMapper.serviceTimeDay(new McChartsView(dtStartStr, dtEndStr, serviceName, userName));
    }
}
