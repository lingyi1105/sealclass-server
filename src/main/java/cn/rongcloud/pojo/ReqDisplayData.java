package cn.rongcloud.pojo;

import cn.rongcloud.common.DisplayEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by weiqinxiao on 2019/3/1.
 */
@ApiModel("房间主屏显示参数")
@Data
public class ReqDisplayData {
    @ApiModelProperty("房间 ID")
    @NotBlank(message = "roomId should not be blank")
    private String roomId;

    @ApiModelProperty("显示类型")
    @NotNull(message = "type should not be blank")
    @Max(4)
    @Min(1)
    private Integer type;

    @ApiModelProperty("即将展示的对应用户的 ID")
    private String userId;

    @ApiModelProperty("要显示的 URI")
    private String uri;
}
