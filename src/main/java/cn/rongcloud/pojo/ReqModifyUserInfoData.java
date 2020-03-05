package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("修改用户参数")
@Data
public class ReqModifyUserInfoData {
    @ApiModelProperty("用户 ID")
    @NotBlank(message = "userId should not be blank")
    private String userId;

    @ApiModelProperty(value = "学校 ID", notes = "superadmin 为 rongcloud")
    private String schoolId;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("头像")
    private String portrait;

    @ApiModelProperty(value = "角色", notes = "superadmin:1, admin:2, 老师: 10, 学生: 20")
    private Integer role;

    @ApiModelProperty("密码")
    private String password;
}
