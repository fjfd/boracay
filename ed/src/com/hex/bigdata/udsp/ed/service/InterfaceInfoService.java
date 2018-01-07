package com.hex.bigdata.udsp.ed.service;

import com.hex.bigdata.udsp.ed.dao.InterfaceInfoMapper;
import com.hex.bigdata.udsp.ed.dto.InterfaceInfoDto;
import com.hex.bigdata.udsp.ed.dto.InterfaceInfoParamDto;
import com.hex.bigdata.udsp.ed.model.EdAppRequestParam;
import com.hex.bigdata.udsp.ed.model.InterfaceInfo;
import com.hex.goframe.model.MessageResult;
import com.hex.goframe.model.Page;
import com.hex.goframe.util.Util;
import com.hex.goframe.util.WebUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.server.quorum.QuorumCnxManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.callback.Callback;
import java.beans.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created by jc.zhao
 * Date:2018/1/3
 * Time:16:19
 */
@Service
public class InterfaceInfoService {
    @Autowired
    private InterfaceInfoMapper interfaceInfoMapper;

    @Autowired
    private EdInterfaceParamService edInterfaceParamService;

    public InterfaceInfo getInterfaceInfoByPkId(String pkId) {
        return interfaceInfoMapper.getInterfaceInfoByPkId(pkId);
    }

    public List<InterfaceInfo> getInterfaceInfoList(InterfaceInfoDto interfaceInfoDto, Page page) {
        return interfaceInfoMapper.getInterfaceInfoList(interfaceInfoDto, page);
    }

    public InterfaceInfo getInterfaceInfoByInterfaceCode(String interfaceCode) {
        return interfaceInfoMapper.getInterfaceInfoByInterfaceCode(interfaceCode);
    }

    public MessageResult addInterfaceInfo(InterfaceInfo interfaceInfo) {
        //检查是否已经存在
        InterfaceInfo interfaceInfo1 = getInterfaceInfoByInterfaceCode(interfaceInfo.getInterfaceCode());
        if (interfaceInfo1 != null) {
            return new MessageResult(false, "服务编码已存在，请重新输入！");
        }

        //添加数据
        String pkId = Util.uuid();
        interfaceInfo.setInterfaceCode(interfaceInfo.getInterfaceCode().trim());
        interfaceInfo.setReqUrl(interfaceInfo.getReqUrl().trim());
        interfaceInfo.setPkId(pkId);
        interfaceInfo.setCrtUser(WebUtil.getCurrentUserId());
        interfaceInfo.setCrtTime(new Date());
        int result = interfaceInfoMapper.addInterfaceInfo(interfaceInfo);

        //校验返回结果
        if (result != 1) {
            return new MessageResult(false, "添加数据失败，请重试！");
        }
        MessageResult messageResult = new MessageResult();
        messageResult.setStatus(true);
        messageResult.setMessage(pkId);
        return messageResult;
    }

    @Transient
    public MessageResult updateInterfaceInfoByPkId(InterfaceInfoParamDto interfaceInfoParamDto) {
        String interfaceId = interfaceInfoParamDto.getInterfaceInfo().getPkId();
        edInterfaceParamService.deleteByInterfaceId(interfaceId);
        MessageResult messageResult1 = updateInterfaceInfoByPkId(interfaceInfoParamDto.getInterfaceInfo());
        MessageResult messageResult2 = edInterfaceParamService.insertRequestColList(interfaceId,interfaceInfoParamDto.getEdInterfaceParamsRequest());
        MessageResult messageResult3 = edInterfaceParamService.insertResponseColList(interfaceId,interfaceInfoParamDto.getEdInterfaceParamsResponse());

        if(messageResult1.isStatus() && messageResult2.isStatus() && messageResult3.isStatus()){
            return new MessageResult(true,"修改成功");
        }
        return new MessageResult(false,"修改失败");
    }
    public MessageResult updateInterfaceInfoByPkId(InterfaceInfo interfaceInfo) {
        interfaceInfo.setInterfaceCode(interfaceInfo.getInterfaceCode().trim());
        interfaceInfo.setReqUrl(interfaceInfo.getReqUrl().trim());
        interfaceInfo.setUpdateUser(WebUtil.getCurrentUserId());
        interfaceInfo.setUpdateTime(new Date());
        int result = interfaceInfoMapper.updateInterfaceInfoByPkId(interfaceInfo);
        if (result != 1) {
            return new MessageResult(false, "更新数据失败！");
        }
        return new MessageResult(true, "更新数据成功！");
    }

    @Transient
    public MessageResult deleteInterfaceInfo(InterfaceInfo[] interfaceInfos) {
        int count = 0;
        for (InterfaceInfo interfaceInfo : interfaceInfos) {
            int result1 = interfaceInfoMapper.deleteInterfaceInfo(interfaceInfo.getPkId());
            int result2 = edInterfaceParamService.deleteByInterfaceId(interfaceInfo.getPkId());
            if (result1 == 1 && result2 > 0) {
                count++;
            }
        }
        if (count != interfaceInfos.length) {
            return new MessageResult(false, "删除失败，请重试！");
        }
        return new MessageResult(true, "删除成功！");
    }

    /**
     * 保存接口配置
     * @param interfaceInfoParamDto
     * @return
     */
    @Transient
    public MessageResult addInterfaceInfo(InterfaceInfoParamDto interfaceInfoParamDto) throws Exception{
        MessageResult messageResult1 = this.addInterfaceInfo(interfaceInfoParamDto.getInterfaceInfo());
        if(messageResult1.isStatus()){
            MessageResult messageResult2 = edInterfaceParamService.insertRequestColList(messageResult1.getMessage(),
                    interfaceInfoParamDto.getEdInterfaceParamsRequest());
            MessageResult messageResult3 = edInterfaceParamService.insertResponseColList(messageResult1.getMessage(),
                    interfaceInfoParamDto.getEdInterfaceParamsResponse());
            if(messageResult2.isStatus() && messageResult3.isStatus()) {
                return new MessageResult(true);
            }
        }
        return new MessageResult(false,"保存失败");
    }

    public List<InterfaceInfo> getInterfaceInfoList() {
        return interfaceInfoMapper.getInterfaceInfoList();
    }
}
