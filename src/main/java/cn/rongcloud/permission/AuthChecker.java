package cn.rongcloud.permission;

import cn.rongcloud.common.ApiException;
import cn.rongcloud.common.ErrorEnum;
import cn.rongcloud.common.JwtUser;
import cn.rongcloud.dao.RoomMemberDao;
import cn.rongcloud.filter.JwtFilter;
import cn.rongcloud.pojo.RoleEnum;
import cn.rongcloud.pojo.RoomMember;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by weiqinxiao on 2019/2/26.
 */
@Slf4j
@Aspect
@Component
public class AuthChecker {
    @Autowired
    RoomMemberDao roomMemberDao;

    @Before("@annotation(permAnno)")
    public void checkPermission(JoinPoint joinPoint, DeclarePermissions permAnno) throws ApiException {
        log.debug("checkPermission:" + joinPoint.getSignature());

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        JwtUser jwtUser = (JwtUser) request.getAttribute(JwtFilter.JWT_AUTH_DATA);
        if (jwtUser == null || StringUtils.isBlank(jwtUser.getSchoolId()) || StringUtils.isBlank(jwtUser.getUserId()) || !RoleEnum.isValid(jwtUser.getRole())) {
            throw new ApiException(ErrorEnum.ERR_INVALID_AUTH);
        }

        //Authorization
        RoleEnum[] permEnums = permAnno.value();
        if (permEnums.length > 0) {//进行权限验证
            List<RoleEnum> perms = new LinkedList<>(Arrays.asList(permEnums));
            RoleEnum roleEnum = RoleEnum.getEnumByValue(jwtUser.getRole());
            if (!perms.contains(roleEnum)) {
                log.error("{} has not permission:{}, apiPerm={}", jwtUser, roleEnum, perms);
                throw new ApiException(ErrorEnum.ERR_ACCESS_DENIED);
            }
        }
    }
}
