package cn.rongcloud.service.Impl;

import cn.rongcloud.common.ApiException;
import cn.rongcloud.common.ErrorEnum;
import cn.rongcloud.common.JwtUser;
import cn.rongcloud.dao.SchoolDao;
import cn.rongcloud.dao.UserDao;
import cn.rongcloud.permission.DeclarePermissions;
import cn.rongcloud.pojo.RoleEnum;
import cn.rongcloud.pojo.School;
import cn.rongcloud.pojo.SchoolResult;
import cn.rongcloud.pojo.UserInfo;
import cn.rongcloud.service.SchoolService;
import cn.rongcloud.utils.DateTimeUtils;
import cn.rongcloud.utils.IdentifierUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SchoolServiceImpl implements SchoolService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private SchoolDao schoolDao;

    @DeclarePermissions(RoleEnum.RoleSuperAdmin)
    @Override
    public SchoolResult create(String name, String portrait, String manager, String appkey, String secret, JwtUser jwtUser) throws ApiException, Exception {
        if (StringUtils.isNotBlank(manager)) {
            List<UserInfo> userInfos = userDao.findByUid(manager);
            if (userInfos.isEmpty()) {
                throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "Manager not found");
            }
            UserInfo userInfo = userInfos.get(0);
            if (RoleEnum.getEnumByValue(userInfo.getRole()) != RoleEnum.RoleAdmin) {
                throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "Manager role error");
            }
        }

        String schoolId = IdentifierUtils.random(6);
        Date curTime = DateTimeUtils.currentUTC();
        School school = new School();
        school.setSid(schoolId);
        school.setName(name);
        school.setPortrait(portrait);
        school.setManager(manager);
        school.setAppkey(appkey);
        school.setSecret(secret);
        school.setCreateDt(curTime);
        school.setUpdateDt(curTime);
        schoolDao.save(school);
        return SchoolResult.generate(school, RoleEnum.getEnumByValue(jwtUser.getRole()));
    }

    @DeclarePermissions(RoleEnum.RoleSuperAdmin)
    @Override
    public SchoolResult modify(String schoolId, String name, String portrait, String manager, String appkey, String secret, JwtUser jwtUser) throws ApiException, Exception {
        List<School> schools = schoolDao.findBySid(schoolId);
        if (schools.isEmpty()) {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "School not found");
        }
        School school = schools.get(0);

        if (StringUtils.isNotBlank(manager)) {
            List<UserInfo> userInfos = userDao.findByUid(manager);
            if (userInfos.isEmpty()) {
                throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "Manager not found");
            }
            UserInfo userInfo = userInfos.get(0);
            if (RoleEnum.getEnumByValue(userInfo.getRole()) != RoleEnum.RoleAdmin) {
                throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "Manager role error");
            }
        }

        if (StringUtils.isNotBlank(name)) {
            school.setName(name);
        }
        if (StringUtils.isNotBlank(portrait)) {
            school.setPortrait(portrait);
        }
        if (StringUtils.isNotBlank(manager)) {
            school.setManager(manager);
        }
        if (StringUtils.isNotBlank(appkey)) {
            school.setAppkey(appkey);
        }
        if (StringUtils.isNotBlank(secret)) {
            school.setSecret(secret);
        }
        school.setUpdateDt(DateTimeUtils.currentUTC());
        schoolDao.save(school);
        return SchoolResult.generate(school, RoleEnum.getEnumByValue(jwtUser.getRole()));
    }

    @DeclarePermissions(RoleEnum.RoleSuperAdmin)
    @Override
    public Boolean delete(String schoolId, JwtUser jwtUser) throws ApiException, Exception {
        return schoolDao.deleteBySid(schoolId) > 0;
    }

    @Override
    public SchoolResult getSchool(String schoolId, JwtUser jwtUser) throws ApiException, Exception {
        List<School> schools = schoolDao.findBySid(schoolId);
        if (schools.isEmpty()) {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "School not found");
        }
        School school = schools.get(0);

        return SchoolResult.generate(school, RoleEnum.getEnumByValue(jwtUser.getRole()));
    }

    @DeclarePermissions(RoleEnum.RoleSuperAdmin)
    @Override
    public List<SchoolResult> getSchools(JwtUser jwtUser) throws ApiException, Exception {
        List<SchoolResult> results = new ArrayList<>();

        List<School> schools = schoolDao.findAll();
        for (School school: schools) {
            results.add(SchoolResult.generate(school, RoleEnum.getEnumByValue(jwtUser.getRole())));
        }

        return results;
    }
}
