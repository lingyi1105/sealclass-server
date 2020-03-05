package cn.rongcloud.service;

import cn.rongcloud.common.ApiException;
import cn.rongcloud.common.JwtUser;
import cn.rongcloud.common.UserAgentTypeEnum;
import cn.rongcloud.pojo.ReqMemberOnlineStatus;
import cn.rongcloud.pojo.UserInfoResult;
import cn.rongcloud.pojo.UserLoginResult;

import java.util.List;

/**
 * Created by weiqinxiao on 2019/2/25.
 */
public interface UserService {
    JwtUser loginCheck(String schoolId, String phone, int role, String password, String deviceId, UserAgentTypeEnum platform) throws ApiException, Exception;
    UserLoginResult login(String schoolId, String phone, int role, String password, String deviceId, UserAgentTypeEnum platform) throws ApiException, Exception;
    Boolean logout(JwtUser jwtUser) throws ApiException, Exception;

    String refreshIMToken(JwtUser jwtUser) throws ApiException, Exception;
    String getIMToken(JwtUser jwtUser) throws ApiException, Exception;
    Boolean onlineStatus(String appkey, List<ReqMemberOnlineStatus> statusList, String nonce, String timestamp, String signature) throws ApiException, Exception;
    void imOfflineKick(String userId);

    UserInfoResult create(String schoolId, String phone, String name, String portrait, int role, String password, JwtUser jwtUser) throws ApiException, Exception;
    UserInfoResult modify(String userId, String schoolId, String phone, String name, String portrait, Integer role, String password, JwtUser jwtUser) throws ApiException, Exception;
    Boolean delete(String userId, JwtUser jwtUser) throws ApiException, Exception;
    UserInfoResult getUser(String userId, JwtUser jwtUser) throws  ApiException, Exception;
    List<UserInfoResult> getUsers(String schoolId, Integer role, JwtUser jwtUser) throws  ApiException, Exception;

    void autoCreate(String schoolId, String phone, String name, String portrait, int role, String password) throws ApiException, Exception;
    Boolean autoDelete(String userId) throws ApiException, Exception;
}