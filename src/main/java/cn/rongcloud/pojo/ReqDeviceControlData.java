package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by weiqinxiao on 2019/3/7.
 */
@ApiModel("操作设备参数")
@Data
public class ReqDeviceControlData {
    @ApiModelProperty("是否开启摄像头")
    private Boolean cameraOn;

    @ApiModelProperty("是否开启麦克风")
    private Boolean microphoneOn;

    @ApiModelProperty("房间 ID")
    @NotBlank(message = "roomId should not be blank")
    private String roomId;

    @ApiModelProperty("用户 ID")
    @NotBlank(message = "userId should not be blank")
    private String userId;
}
