package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("创建学校参数")
@Data
public class ReqSchoolCreateData {
    @ApiModelProperty("名称")
    @NotBlank(message = "name should not be blank")
    private String name;

    @ApiModelProperty("头像")
    private String portrait;

    @ApiModelProperty("管理员用户 ID")
    private String manager;

    @ApiModelProperty("用户 ID")
    private String appkey;

    @ApiModelProperty("用户 ID")
    private String secret;
}
