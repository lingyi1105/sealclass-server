package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiqinxiao on 2019/2/28.
 */
@ApiModel("房间操作响应")
public class RoomResult {
    @ApiModelProperty("房间 ID")
    private @Getter @Setter String roomId;
    @ApiModelProperty("AppKey")
    private @Getter @Setter String appkey;
    @ApiModelProperty("IM token")
    private @Getter @Setter String imToken;
    @ApiModelProperty(value = "鉴权 token", notes = "后续请求需要在 Header 中携带该字段")
    private @Getter @Setter String authorization;
    @ApiModelProperty("房间内所有用户的用户信息")
    private @Getter @Setter List<MemberResult> members = new ArrayList<>();
    @ApiModelProperty("房间的主屏显示内容")
    private @Getter @Setter String display;
    @ApiModelProperty("房间内的白板信息")
    private @Getter @Setter List<WhiteboardResult> whiteboards = new ArrayList<>();
    @ApiModelProperty("当前用户的用户信息")
    private @Getter @Setter MemberResult userInfo;

    public void setWhiteboards(List<Whiteboard> whiteboardList) {
        for (Whiteboard wb : whiteboardList) {
            WhiteboardResult r = new WhiteboardResult();
            r.setName(wb.getName());
            r.setWhiteboardId(wb.getWbid());
            r.setCurPg(wb.getCurPg());
            whiteboards.add(r);
        }
    }
}
