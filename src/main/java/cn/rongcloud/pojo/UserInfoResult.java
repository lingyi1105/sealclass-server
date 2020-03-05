package cn.rongcloud.pojo;

import cn.rongcloud.common.JwtUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("用户信息")
public class UserInfoResult {
    @ApiModelProperty("学校 ID")
    private @Getter @Setter String schoolId;
    @ApiModelProperty("用户 ID")
    private @Getter @Setter String userId;
    @ApiModelProperty("手机号")
    private @Getter @Setter String phone;
    @ApiModelProperty("用户名")
    private @Getter @Setter String userName;
    @ApiModelProperty("头像")
    private @Getter @Setter String portrait;
    @ApiModelProperty("角色")
    private @Getter @Setter int role;

    public static UserInfoResult generate(JwtUser jwtUser) {
        UserInfoResult infoResult = new UserInfoResult();
        infoResult.setSchoolId(jwtUser.getSchoolId());
        infoResult.setUserId(jwtUser.getUserId());
        infoResult.setPhone(jwtUser.getPhone());
        infoResult.setUserName(jwtUser.getUserName());
        infoResult.setPortrait(jwtUser.getPortrait());
        infoResult.setRole(jwtUser.getRole());
        return infoResult;
    }

    public static UserInfoResult generate(UserInfo userInfo) {
        UserInfoResult infoResult = new UserInfoResult();
        infoResult.setSchoolId(userInfo.getSid());
        infoResult.setUserId(userInfo.getUid());
        infoResult.setPhone(userInfo.getPhone());
        infoResult.setUserName(userInfo.getName());
        infoResult.setPortrait(userInfo.getPortrait());
        infoResult.setRole(userInfo.getRole());
        return infoResult;
    }
}
