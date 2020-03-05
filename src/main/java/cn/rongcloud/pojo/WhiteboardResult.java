package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("房间内的白板信息")
@Data
public class WhiteboardResult {
    @ApiModelProperty("房间 ID")
    String roomId;
    @ApiModelProperty("学校 ID")
    String schoolId;
    @ApiModelProperty("白板 ID")
    String whiteboardId;
    @ApiModelProperty("白板名称")
    String name;
    @ApiModelProperty("当前显示页")
    int curPg;
}
