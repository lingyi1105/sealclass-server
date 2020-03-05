package cn.rongcloud.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("App 版本号信息")
@Data
public class AppVersionResult {
    @ApiModelProperty(value = "平台", notes = "Android: 1, iOS: 2")
    private Integer platform;
    @ApiModelProperty("下载链接")
    private String url;
    @ApiModelProperty("版本号")
    private String version;
    @ApiModelProperty("更新时间")
    private Date updateDt;

    public static AppVersionResult generate(AppVersion v) {
        AppVersionResult result = new AppVersionResult();
        result.setPlatform(v.getPlatform());
        result.setUrl(v.getUrl());
        result.setVersion(v.getVersion());
        result.setUpdateDt(v.getUpdateDt());
        return result;
    }
}
