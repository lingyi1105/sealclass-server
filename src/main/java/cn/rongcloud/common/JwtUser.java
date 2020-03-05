package cn.rongcloud.common;

import cn.rongcloud.pojo.School;
import cn.rongcloud.pojo.UserInfo;
import lombok.Data;

/**
 * Created by weiqinxiao on 2019/2/27.
 */
@Data
public class JwtUser {
    private String schoolId;
    private String userId;
    private String phone;
    private String userName;
    private String portrait;
    private int role;
    private String appkey;
    private String secret;
    private String deviceId;

    public static JwtUser generate(UserInfo userInfo, School school) {
        JwtUser user = new JwtUser();
        user.setSchoolId(userInfo.getSid());
        user.setUserId(userInfo.getUid());
        user.setPhone(userInfo.getPhone());
        user.setUserName(userInfo.getName());
        user.setPortrait(userInfo.getPortrait());
        user.setRole(userInfo.getRole());
        user.setAppkey(school.getAppkey());
        user.setSecret(school.getSecret());
        user.setDeviceId(user.getDeviceId());
        return user;
    }
}