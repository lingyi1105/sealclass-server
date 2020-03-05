package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("修改学校参数")
@Data
public class ReqSchoolModifyData {
    @ApiModelProperty("学校 ID")
    @NotBlank(message = "schoolId should not be blank")
    private String schoolId;

    @ApiModelProperty("名称")
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
