package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by weiqinxiao on 2019/3/1.
 */
@ApiModel("用户登陆参数")
@Data
public class ReqUserLoginData {
    @ApiModelProperty("学校 id, superadmin 为 rongcloud")
    @NotBlank
    private String schoolId;

    @ApiModelProperty("手机号")
    @NotBlank
    private String phone;

    @ApiModelProperty("密码")
    @NotBlank
    private String password;

    @ApiModelProperty("角色, superadmin:1, admin:2, 老师: 10, 学生: 20")
    @NotNull
    @Max(20)
    @Min(1)
    private Integer role;

    @ApiModelProperty("设备 ID")
    private String deviceId;
}
