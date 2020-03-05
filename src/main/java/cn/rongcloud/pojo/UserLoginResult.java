package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("用户登陆响应")
public class UserLoginResult {
    @ApiModelProperty("AppKey")
    private @Getter @Setter String appkey;
    @ApiModelProperty("IM token")
    private @Getter @Setter String imToken;
    @ApiModelProperty(value = "鉴权 token", notes = "后续请求需要在 Header 中携带该字段")
    private @Getter @Setter String authorization;
    @ApiModelProperty("用户信息")
    private @Getter @Setter UserInfoResult userInfo;
}
