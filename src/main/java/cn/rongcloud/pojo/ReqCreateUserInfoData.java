package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("创建用户参数")
@Data
public class ReqCreateUserInfoData {
    @ApiModelProperty(value = "学校 ID", notes = "superadmin 为 rongcloud")
    @NotBlank(message = "schoolId should not be blank")
    private String schoolId;

    @ApiModelProperty("手机号")
    @NotBlank(message = "phone should not be blank")
    private String phone;

    @ApiModelProperty("用户名")
    @NotBlank(message = "name should not be blank")
    private String name;

    @ApiModelProperty("头像")
    private String portrait;

    @ApiModelProperty(value = "角色", notes = "superadmin:1, admin:2, 老师: 10, 学生: 20")
    @NotNull(message = "role should not be null")
    @Max(20)
    @Min(1)
    private Integer role;

    @ApiModelProperty("密码")
    @NotBlank(message = "password should not be blank")
    private String password;
}
