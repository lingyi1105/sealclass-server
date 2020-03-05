package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel("App 版本号更新")
@Data
public class ReqUpdateAppVersionData {
    @ApiModelProperty(value = "平台", notes = "Android: 1, iOS: 2")
    @NotNull(message = "platform should not be null")
    @Max(2)
    @Min(1)
    private Integer platform;
    @ApiModelProperty("下载链接")
    @NotBlank(message = "url should not be blank")
    private String url;
    @ApiModelProperty("版本号")
    @NotBlank(message = "version should not be blank")
    private String version;
}
