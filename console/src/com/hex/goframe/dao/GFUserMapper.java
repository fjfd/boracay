package com.hex.goframe.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.goframe.model.GFLoginUser;
import com.hex.goframe.model.GFUser;
import com.hex.goframe.model.Page;
import com.hex.goframe.util.MapUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GFUserMapper extends SyncMapper<GFUser> {

    @Autowired
    private GFUserForAppIdAndUserIdMapper mapper;

    public int deleteByPrimaryKey(String id) {
        GFUser user = select(id);
        try {
            return delete(id) ? 1 : 0;
        } finally {
            String key = user.getAppId() + "|" + user.getUserId();
            mapper.delete(key);
        }
    }

    public GFUser selectByUserId(String userId, String appId) {
        String key = appId + "|" + userId;
        return mapper.select(key);
    }

    public GFLoginUser selectFullUserByUserId(String userId, String appId) {
        HashMap map = new HashMap();
        map.put("userId", userId);
        map.put("appId", appId);
        return (GFLoginUser) this.sqlSessionTemplate.selectOne("com.hex.goframe.dao.GFUserMapper.selectFullUserByUserId", map);
    }

    public int insert(GFUser user) {
        try {
            return insert(user.getId(), user) ? 1 : 0;
        } finally {
            String key = user.getAppId() + "|" + user.getUserId();
            mapper.insert(key, user);
        }
    }

    public GFUser selectByPrimaryKey(String id) {
        return select(id);
    }

    public List<GFUser> selectAll() {
        return this.sqlSessionTemplate.selectList("com.hex.goframe.dao.GFUserMapper.selectAll");
    }

    public int updateByPrimaryKey(GFUser user) {
        try {
            return update(user.getId(), user) ? 1 : 0;
        } finally {
            String key = user.getAppId() + "|" + user.getUserId();
            mapper.update(key, user);
        }
    }

    public int updateStatus(String id, int status) {
        return this.sqlSessionTemplate.update("com.hex.goframe.dao.GFUserMapper.updateStatus", Integer.valueOf(status));
    }

    public List<GFUser> queryUsers(GFUser user, Page page, String authId) {
        Map map = MapUtil.ConvertObjToMap(user);
        if (authId != null && !"".equals(authId)) {
            map.put("authId", authId);
        }

        List list = this.sqlSessionTemplate.selectList("com.hex.goframe.dao.GFUserMapper.queryUsers", map, page.toPageBounds());
        page.totalCount(list);
        return list;
    }

    public List<GFUser> queryUsersInOrg(GFUser user, Page page, String orgId) {
        Map map = MapUtil.ConvertObjToMap(user);
        if (orgId != null && !"".equals(orgId)) {
            map.put("orgId", orgId);
        }

        List list = this.sqlSessionTemplate.selectList("com.hex.goframe.dao.GFUserMapper.queryUsersInOrg", map, page.toPageBounds());
        page.totalCount(list);
        return list;
    }

    public int resetPassword(String id) {
        String passWord = "000000";
        GFUser user = select(id);
        user.setPassword(DigestUtils.md5Hex(passWord));
        try {
            return update(id, user) ? 1 : 0;
        } finally {
            String key = user.getAppId() + "|" + user.getUserId();
            mapper.update(key, user);
        }
    }

    public boolean checkPassword(String id, String oldPassword) {
        HashMap map = new HashMap();
        map.put("id", id);
        map.put("oldPassword", DigestUtils.md5Hex(oldPassword));
        int result = ((Integer) this.sqlSessionTemplate.selectOne("com.hex.goframe.dao.GFUserMapper.checkPassword", map)).intValue();
        return result == 1;
    }

    public boolean changePassword(String id, String newPassword) {
        GFUser user = select(id);
        user.setPassword(DigestUtils.md5Hex(newPassword));
        try {
            return update(id, user);
        } finally {
            String key = user.getAppId() + "|" + user.getUserId();
            mapper.update(key, user);
        }
    }

    public List<GFUser> queryUsersByEmpId(String empId) {
        HashMap map = new HashMap();
        map.put("empId", empId);
        return this.sqlSessionTemplate.selectList("com.hex.goframe.dao.GFUserMapper.queryUsersByEmpId", map);
    }

    public List<String> queryUserRolesByUserId(String userId) {
        return this.sqlSessionTemplate.selectList("com.hex.goframe.dao.GFUserMapper.getUserRolesByUserId", userId);
    }

    public int updateUserNameByEmpId(String empId, String userName) throws Exception {
        HashMap params = new HashMap();
        params.put("empId", empId);
        params.put("userName", userName);
        return this.sqlSessionTemplate.update("com.hex.goframe.dao.GFUserMapper.updateUserNameByEmpId", params);
    }

    public String getMasterOrgId(String appId, String userId) {
        HashMap params = new HashMap();
        params.put("userId", userId);
        params.put("appId", appId);
        return (String) this.sqlSessionTemplate.selectOne("com.hex.goframe.dao.GFUserMapper.getMasterOrgId", params);
    }

    public Integer deleteLoginUserByEmpId(String empId) {
        return Integer.valueOf(this.sqlSessionTemplate.delete("com.hex.goframe.dao.GFUserMapper.deleteLoginUserByEmpId", empId));
    }

    //  -----------------------------------------------------------------------------------------

    @Override
    protected boolean insertExe(GFUser user) {
        return this.sqlSessionTemplate.insert("com.hex.goframe.dao.GFUserMapper.insert", user) == 1;
    }

    @Override
    protected boolean updateExe(GFUser user) {
        return this.sqlSessionTemplate.update("com.hex.goframe.dao.GFUserMapper.updateByPrimaryKey", user) == 1;
    }

    @Override
    protected boolean deleteExe(String id) {
        return this.sqlSessionTemplate.delete("com.hex.goframe.dao.GFUserMapper.deleteByPrimaryKey", id) == 1;
    }

    @Override
    protected GFUser selectExe(String id) {
        return (GFUser) this.sqlSessionTemplate.selectOne("com.hex.goframe.dao.GFUserMapper.selectByPrimaryKey", id);
    }

    @Override
    protected boolean deleteListExe(String id) {
        return false;
    }

    @Override
    protected List<GFUser> selectListExe(String id) {
        return null;
    }

}
