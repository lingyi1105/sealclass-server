package cn.rongcloud.service.Impl;

import cn.rongcloud.common.*;
import cn.rongcloud.dao.RoomMemberDao;
import cn.rongcloud.dao.SchoolDao;
import cn.rongcloud.dao.UserDao;
import cn.rongcloud.im.IMHelper;
import cn.rongcloud.im.message.NewDeviceMessage;
import cn.rongcloud.job.ScheduleManager;
import cn.rongcloud.permission.DeclarePermissions;
import cn.rongcloud.pojo.*;
import cn.rongcloud.service.RoomService;
import cn.rongcloud.service.UserService;
import cn.rongcloud.utils.DateTimeUtils;
import cn.rongcloud.utils.IdentifierUtils;
import cn.rongcloud.utils.SecurityUtils;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by weiqinxiao on 2019/2/25.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private SchoolDao schoolDao;

    @Autowired
    private RoomMemberDao roomMemberDao;

    @Autowired
    private JwtTokenHelper tokenHelper;

    @Autowired
    private IMHelper imHelper;

    @Autowired
    private ScheduleManager scheduleManager;

    @Autowired
    @Lazy
    private RoomService roomService;

    @Override
    public JwtUser loginCheck(String schoolId, String phone, int role, String password, String deviceId, UserAgentTypeEnum platform) throws ApiException, Exception {
        log.info("loginCheck: schoolId={}, phone={}, role={}, password={}", schoolId, phone, role, password);

        List<UserInfo> userInfos = userDao.findBySidAndPhone(schoolId, phone);
        if (Collections.isEmpty(userInfos)) {
            throw new ApiException(ErrorEnum.ERR_USER_NOT_EXIST);
        }
        UserInfo userInfo = userInfos.get(0);

        List<School> schools = schoolDao.findBySid(schoolId);
        if (Collections.isEmpty(schools)) {
            throw new ApiException(ErrorEnum.ERR_SCHOOL_NOT_EXIST);
        }
        School school = schools.get(0);

        String enc = SecurityUtils.byte2hex(SecurityUtils.encryptHMAC(password, userInfo.getSalt(), SecurityUtils.HmacAlgorithm.HMAC_MD5));
        if (!enc.equals(userInfo.getPassword())) {
            throw new ApiException(ErrorEnum.ERR_USER_PASSWORD_ERROR);
        }

        if (userInfo.getRole() != role) {
            throw new ApiException(ErrorEnum.ERR_USER_ROLE_ERROR, "user role error");
        }

        if (RoleEnum.getEnumByValue(userInfo.getRole()) != RoleEnum.RoleSuperAdmin &&
                StringUtils.isNotBlank(userInfo.getDeviceId()) && !userInfo.getDeviceId().equals(deviceId)) {
            NewDeviceMessage msg = new NewDeviceMessage(deviceId, platform);
            imHelper.publishPrivateMessage(school.getAppkey(), school.getSecret(), userInfo.getUid(), userInfo.getUid(), msg);
        }
        if (StringUtils.isNotBlank(deviceId)) {
            userInfo.setDeviceId(deviceId);
            userDao.save(userInfo);
        }

        return JwtUser.generate(userInfo, school);
    }

    @Override
    public UserLoginResult login(String schoolId, String phone, int role, String password, String deviceId, UserAgentTypeEnum platform) throws ApiException, Exception {
        log.info("login: schoolId={}, phone={}, role={}, password={}", schoolId, phone, role, password);

        JwtUser jwtUser = loginCheck(schoolId, phone, role, password, deviceId, platform);
        JwtToken jwtToken = tokenHelper.createJwtToken(jwtUser);

        UserLoginResult result = new UserLoginResult();
        result.setAppkey(jwtUser.getAppkey());
        result.setUserInfo(UserInfoResult.generate(jwtUser));
        result.setAuthorization(jwtToken.getToken());
        if (RoleEnum.getEnumByValue(jwtUser.getRole()) == RoleEnum.RoleTeacher
            || RoleEnum.getEnumByValue(jwtUser.getRole()) == RoleEnum.RoleStudent) {
            result.setImToken(getIMToken(jwtUser));
        }
        return result;
    }

    @Override
    public Boolean logout(JwtUser jwtUser) throws ApiException, Exception {
        return logout(jwtUser.getUserId());
    }

    private Boolean logout(String userId) throws ApiException, Exception {
        List<UserInfo> userInfos = userDao.findByUid(userId);
        if (userInfos.isEmpty()) {
            log.info("userId={} not exist", userId);
            return Boolean.TRUE;
        }
        UserInfo userInfo = userInfos.get(0);

        List<School> schools = schoolDao.findBySid(userInfo.getSid());
        if (schools.isEmpty()) {
            log.info("schoolId={} not exist", userInfo.getSid());
            return Boolean.TRUE;
        }
        School school = schools.get(0);

        List<RoomMember> members = roomMemberDao.findByUid(userId);
        for (RoomMember member : members) {
            roomService.leaveRoom(JwtUser.generate(userInfo, school), member.getRid(), member.getSid());
        }

        return Boolean.TRUE;
    }

    @DeclarePermissions({RoleEnum.RoleTeacher, RoleEnum.RoleStudent})
    @Override
    public String refreshIMToken(JwtUser jwtUser) throws ApiException, Exception  {
        return getIMToken(jwtUser);
    }

    @Override
    public String getIMToken(JwtUser jwtUser) throws ApiException, Exception  {
        log.info("{} request token", jwtUser);
        IMTokenInfo tokenInfo = imHelper.getToken(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), jwtUser.getUserName(), jwtUser.getPortrait());
        if (!tokenInfo.isSuccess()) {
            throw new ApiException(ErrorEnum.ERR_IM_TOKEN_ERROR, tokenInfo.getErrorMessage());
        }
        return tokenInfo.getToken();
    }

    @Override
    public Boolean onlineStatus(String appkey, List<ReqMemberOnlineStatus> statusList, String nonce, String timestamp, String signature) throws ApiException, Exception {
        // uncheck
//        String sign = imProperties.getSecret() + nonce + timestamp;
//        String signSHA1 = CodeUtil.hexSHA1(sign);
//        if (!signSHA1.equals(signature)) {
//            log.info("memberOnlineStatus signature error");
//            return true;
//        }

        for (ReqMemberOnlineStatus status : statusList) {
            int s = Integer.parseInt(status.getStatus());
            String userId = status.getUserId();

            log.info("memberOnlineStatus, userId={}, status={}", userId, status);
            //1：offline 离线； 0: online 在线
            if (s == 1) {
                List<RoomMember> members = roomMemberDao.findByUid(userId);
                if (!members.isEmpty()) {
                    scheduleManager.userIMOffline(userId);
                }
            } else if (s == 0) {
                scheduleManager.userIMOnline(userId);
            }
        }

        return Boolean.TRUE;
    }

    @Override
    public void imOfflineKick(String userId) {
        try {
            logout(userId);
            autoDelete(userId);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    @DeclarePermissions({RoleEnum.RoleSuperAdmin, RoleEnum.RoleAdmin})
    @Override
    public UserInfoResult create(String schoolId, String phone, String name, String portrait, int role, String password, JwtUser jwtUser) throws ApiException, Exception {
        return internalCreate(schoolId, phone, name, portrait, role, password, jwtUser);
    }

    public UserInfoResult internalCreate(String schoolId, String phone, String name, String portrait, int role, String password, JwtUser jwtUser) throws ApiException, Exception {
        List<School> schools = schoolDao.findBySid(schoolId);
        if (Collections.isEmpty(schools)) {
            throw new ApiException(ErrorEnum.ERR_SCHOOL_NOT_EXIST);
        }

        List<UserInfo> userInfos = userDao.findBySidAndPhone(schoolId, phone);
        if (!userInfos.isEmpty()) {
            throw new ApiException(ErrorEnum.ERR_USER_HAS_EXIST);
        }

        String userId = IdentifierUtils.uuid32();
        String salt = IdentifierUtils.random(6);
        String enc = SecurityUtils.byte2hex(SecurityUtils.encryptHMAC(password, salt, SecurityUtils.HmacAlgorithm.HMAC_MD5));
        Date curTime = DateTimeUtils.currentUTC();

        UserInfo userInfo = new UserInfo();
        userInfo.setUid(userId);
        userInfo.setSid(schoolId);
        userInfo.setPhone(phone);
        userInfo.setName(name);
        userInfo.setPortrait(portrait);
        userInfo.setRole(role);
        userInfo.setPassword(enc);
        userInfo.setSalt(salt);
        userInfo.setCreateDt(curTime);
        userInfo.setUpdateDt(curTime);
        userDao.save(userInfo);
        return UserInfoResult.generate(userInfo);
    }

    @DeclarePermissions({RoleEnum.RoleSuperAdmin, RoleEnum.RoleAdmin})
    @Override
    public UserInfoResult modify(String userId, String schoolId, String phone, String name, String portrait, Integer role, String password, JwtUser jwtUser) throws ApiException, Exception {
        List<UserInfo> userInfos = userDao.findByUidAndSid(userId, schoolId);
        if (userInfos.isEmpty()) {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "User not found");
        }
        UserInfo userInfo = userInfos.get(0);

        if (StringUtils.isNotBlank(schoolId)) {
            userInfo.setSid(schoolId);
        }
        if (StringUtils.isNotBlank(phone)) {
            userInfo.setPhone(phone);
        }
        if (StringUtils.isNotBlank(name)) {
            userInfo.setName(name);
        }
        if (StringUtils.isNotBlank(portrait)) {
            userInfo.setPortrait(portrait);
        }
        if (role != null) {
            userInfo.setRole(role);
        }
        if (StringUtils.isNotBlank(password)) {
            String enc = SecurityUtils.byte2hex(SecurityUtils.encryptHMAC(password, userInfo.getSalt(), SecurityUtils.HmacAlgorithm.HMAC_MD5));
            userInfo.setPassword(enc);
        }
        userInfo.setUpdateDt(DateTimeUtils.currentUTC());
        userDao.save(userInfo);
        return UserInfoResult.generate(userInfo);
    }

    @DeclarePermissions({RoleEnum.RoleSuperAdmin, RoleEnum.RoleAdmin})
    @Override
    public Boolean delete(String userId, JwtUser jwtUser) throws ApiException, Exception {
        if (RoleEnum.getEnumByValue(jwtUser.getRole()) == RoleEnum.RoleSuperAdmin) {
            return userDao.deleteByUidAndRoleGreaterThan(userId, jwtUser.getSchoolId()) > 0;
        } else {
            return userDao.deleteByUidAndSidAndRoleGreaterThan(userId, jwtUser.getSchoolId(), jwtUser.getRole()) > 0;
        }
    }

    @Override
    public UserInfoResult getUser(String userId, JwtUser jwtUser) throws ApiException, Exception {
        List<UserInfo> userInfos;
        if (RoleEnum.getEnumByValue(jwtUser.getRole()) == RoleEnum.RoleSuperAdmin) {
            userInfos = userDao.findByUid(userId);
        } else {
            userInfos = userDao.findByUidAndSid(userId, jwtUser.getSchoolId());
        }
        if (userInfos.isEmpty()) {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "User not found");
        }
        return UserInfoResult.generate(userInfos.get(0));
    }

    @Override
    public List<UserInfoResult> getUsers(String schoolId, Integer role, JwtUser jwtUser) throws ApiException, Exception {
        List<UserInfo> userInfos;
        if (RoleEnum.getEnumByValue(jwtUser.getRole()) == RoleEnum.RoleSuperAdmin) {
            if (schoolId != null && role != null) {
                userInfos = userDao.findBySidAndRole(schoolId, role);
            } else if (schoolId != null) {
                userInfos = userDao.findBySid(schoolId);
            } else if (role != null) {
                userInfos = userDao.findByRole(role);
            } else {
                userInfos = userDao.findAll();
            }
        } else {
            if (role != null) {
                userInfos = userDao.findBySidAndRole(schoolId, role);
            } else {
                userInfos = userDao.findBySid(schoolId);
            }
        }

        List<UserInfoResult> results = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            results.add(UserInfoResult.generate(userInfo));
        }
        return results;
    }

    @Override
    public void autoCreate(String schoolId, String phone, String name, String portrait, int role, String password) throws ApiException, Exception {
        try {
            internalCreate(schoolId, phone, name, portrait, role, password, null);
        } catch (ApiException e) {
            if (e.getError() != ErrorEnum.ERR_USER_HAS_EXIST) {
                throw e;
            }
        }
    }

    @Override
    public Boolean autoDelete(String userId) throws ApiException, Exception {
        return userDao.deleteByUid(userId) > 0;
    }
}
