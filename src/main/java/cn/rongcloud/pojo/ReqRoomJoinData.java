package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by weiqinxiao on 2019/3/1.
 */
@ApiModel("加入房间参数")
@Data
public class ReqRoomJoinData {
    @ApiModelProperty("学校 ID")
    @NotBlank(message = "schoolId should not be blank")
    private String schoolId;

    @ApiModelProperty("手机号")
    @NotBlank(message = "phone should not be blank")
    private String phone;

    @ApiModelProperty("密码")
    @NotBlank(message = "password should not be blank")
    private String password;

    @ApiModelProperty(value = "角色", notes = "superadmin:1, admin:2, 老师: 10, 学生: 20")
    @NotNull(message = "role should not be null")
    private Integer role;

    @ApiModelProperty("是否关闭摄像头")
	private boolean disableCamera;

    @ApiModelProperty("房间 ID")
    @NotBlank(message = "roomId should not be blank")
    private String roomId;

    @ApiModelProperty("设备 ID")
    private String deviceId;
}
