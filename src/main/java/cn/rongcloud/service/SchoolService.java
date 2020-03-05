package cn.rongcloud.service;

import cn.rongcloud.common.ApiException;
import cn.rongcloud.common.JwtUser;
import cn.rongcloud.pojo.SchoolResult;

import java.util.List;

public interface SchoolService {
    SchoolResult create(String name, String portrait, String manager, String appkey, String secret, JwtUser jwtUser) throws ApiException, Exception;
    SchoolResult modify(String schoolId, String name, String portrait, String manager, String appkey, String secret, JwtUser jwtUser) throws ApiException, Exception;
    Boolean delete(String schoolId, JwtUser jwtUser) throws ApiException, Exception;
    SchoolResult getSchool(String schoolId, JwtUser jwtUser) throws  ApiException, Exception;
    List<SchoolResult> getSchools(JwtUser jwtUser) throws  ApiException, Exception;
}
