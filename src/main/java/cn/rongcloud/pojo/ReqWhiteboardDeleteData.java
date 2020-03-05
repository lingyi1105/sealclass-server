package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by weiqinxiao on 2019/3/7.
 */
@ApiModel("白板删除参数")
@Data
public class ReqWhiteboardDeleteData {
    @ApiModelProperty("房间 ID")
    @NotBlank(message = "roomId should not be blank")
    private String roomId;

    @ApiModelProperty("白板 ID")
    @NotBlank(message = "whiteboardId should not be blank")
    private String whiteboardId;
}
