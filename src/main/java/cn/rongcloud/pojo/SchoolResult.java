package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@ApiModel("学校响应信息")
public class SchoolResult {
    @ApiModelProperty("学校 ID")
    private @Getter @Setter String schoolId;
    @ApiModelProperty("学校名称")
    private @Getter @Setter String name;
    @ApiModelProperty("学校头像")
    private @Getter @Setter String portrait;
    @ApiModelProperty("学校管理员用户 ID")
    private @Getter @Setter String manager;
    @ApiModelProperty("学校的 IM AppKey")
    private @Getter @Setter String appkey;
    @ApiModelProperty("学校的 IM Secret")
    private @Getter @Setter String secret;
    @ApiModelProperty("创建时间")
    private @Getter @Setter Date createDt;
    @ApiModelProperty("更新时间")
    private @Getter @Setter Date updateDt;

    public static SchoolResult generate(School school, RoleEnum roleEnum) {
        SchoolResult schoolResult = new SchoolResult();
        schoolResult.setSchoolId(school.getSid());
        schoolResult.setName(school.getName());
        schoolResult.setPortrait(school.getPortrait());
        schoolResult.setManager(school.getManager());
        if (roleEnum == RoleEnum.RoleSuperAdmin) {
            schoolResult.setAppkey(school.getAppkey());
            schoolResult.setSecret(school.getSecret());
        }
        schoolResult.setCreateDt(school.getCreateDt());
        schoolResult.setUpdateDt(school.getUpdateDt());
        return schoolResult;
    }
}
