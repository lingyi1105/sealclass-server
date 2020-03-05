package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by weiqinxiao on 2019/3/7.
 */
@ApiModel("响应设备被操作参数")
@Data
public class ReqDeviceControlResData {
    @ApiModelProperty("房间 ID")
    @NotBlank(message = "roomId should not be blank")
    private String roomId;

    @ApiModelProperty("令牌")
    @NotBlank(message = "ticket should not be blank")
    private String ticket;
}
