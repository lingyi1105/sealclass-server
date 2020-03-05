package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by weiqinxiao on 2019/3/7.
 */
@ApiModel("白板翻页参数")
@Data
public class ReqWhiteboardTurnPageData {
    @ApiModelProperty("房间 ID")
    @NotBlank(message = "roomId should not be blank")
    private String roomId;

    @ApiModelProperty("白板 ID")
    @NotBlank(message = "whiteboardId should not be blank")
    private String whiteboardId;

    @ApiModelProperty("页数")
    @NotNull(message = "page should not be null")
    @Min(0)
    private Integer page;
}
