package cn.rongcloud.controller;

import cn.rongcloud.common.*;
import cn.rongcloud.filter.JwtFilter;
import cn.rongcloud.pojo.*;
import cn.rongcloud.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static cn.rongcloud.filter.JwtFilter.USER_AGENT_TYPE;

/**
 * Created by weiqinxiao on 2019/2/25.
 */
@Api("用户相关 API")
@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户登陆")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public BaseResponse<UserLoginResult> login(@RequestBody @Validated ReqUserLoginData data,
                                               @RequestHeader(value = "user-agent", required = false) String userAgent)
            throws ApiException, Exception {
        if (!RoleEnum.isValid(data.getRole())) {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR);
        }
        UserLoginResult result = userService.login(data.getSchoolId(), data.getPhone(), data.getRole(), data.getPassword(), data.getDeviceId(), UserAgentTypeEnum.getEnumByUserAgent(userAgent));
        return new BaseResponse<>(result);
    }

    @ApiOperation("用户退出")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public BaseResponse<Boolean> logout(@ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result = userService.logout(jwtUser);
        return new BaseResponse<>(result);
    }

    @ApiOperation("刷新 IM token")
    @RequestMapping(value = "/refresh-token", method = RequestMethod.POST)
    public BaseResponse<String> refreshToken(@ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        String token = userService.refreshIMToken(jwtUser);
        BaseResponse<String> response = new BaseResponse<>();
        response.setData(token);
        return response;
    }

    @ApiOperation("IM 在线状态订阅")
    @RequestMapping(value = "/online-status", method = RequestMethod.POST)
    public BaseResponse<Boolean> memberOnlineStatus(@RequestParam(value = "appKey", required = false) String appkey,
                                                    @RequestBody List<ReqMemberOnlineStatus> statusList,
                                                    @RequestParam(value = "timestamp", required = false) String timestamp,
                                                    @RequestParam(value = "nonce", required = false) String nonce,
                                                    @RequestParam(value = "signature", required = false) String signature)
            throws ApiException, Exception {
        Boolean result = userService.onlineStatus(appkey, statusList, nonce, timestamp, signature);
        return new BaseResponse<>(result);
    }

    @ApiOperation("新建用户")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public BaseResponse<UserInfoResult> createUser(@RequestBody ReqCreateUserInfoData data,
                                                   @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        if (!RoleEnum.isValid(data.getRole())) {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR);
        }
        if (jwtUser.getRole() >= data.getRole()) {
            throw new ApiException(ErrorEnum.ERR_ACCESS_DENIED);
        }
        UserInfoResult result = userService.create(data.getSchoolId(), data.getPhone(), data.getName(), data.getPortrait(), data.getRole(), data.getPassword(), jwtUser);
        return new BaseResponse<>(result);
    }

    @ApiOperation("修改用户信息")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public BaseResponse<UserInfoResult> modifyUser(@RequestBody @Validated ReqModifyUserInfoData data,
                                                   @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        if (data.getRole() != null && !RoleEnum.isValid(data.getRole())) {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR);
        }
        if (jwtUser.getRole() >= data.getRole()) {
            throw new ApiException(ErrorEnum.ERR_ACCESS_DENIED);
        }
        UserInfoResult result = userService.modify(data.getUserId(), data.getSchoolId(), data.getPhone(), data.getName(), data.getPortrait(), data.getRole(), data.getPassword(), jwtUser);
        return new BaseResponse<>(result);
    }

    @ApiOperation("删除用户")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseResponse<Boolean> deleteUser(@ApiParam("用户 ID") @RequestParam @NotBlank(message = "userId should not be blank") String userId,
                                            @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        boolean result = userService.delete(userId, jwtUser);
        return new BaseResponse<>(result);
    }

    @ApiOperation("查询单个用户信息")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public BaseResponse<UserInfoResult> getUser(@ApiParam("用户 ID") @RequestParam @NotBlank(message = "userId should not be blank") String userId,
                                                @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        UserInfoResult result = userService.getUser(userId, jwtUser);
        return new BaseResponse<>(result);
    }

    @ApiOperation("查询所有用户的信息")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public BaseResponse<List<UserInfoResult>> getUsers(@ApiParam("用户 ID") @RequestParam(required = false) String schoolId,
                                                       @ApiParam("用户 ID") @RequestParam(required = false) Integer role,
                                                       @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        if (role != null && !RoleEnum.isValid(role)) {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR);
        }
        if (RoleEnum.getEnumByValue(jwtUser.getRole()) != RoleEnum.RoleSuperAdmin
                && schoolId != null && !schoolId.equals(jwtUser.getSchoolId())) {
            throw new ApiException(ErrorEnum.ERR_ACCESS_DENIED);
        }
        List<UserInfoResult> results = userService.getUsers(schoolId, role, jwtUser);
        return new BaseResponse<>(results);
    }
}
