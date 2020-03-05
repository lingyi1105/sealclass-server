package cn.rongcloud.controller;

import cn.rongcloud.common.*;
import cn.rongcloud.filter.JwtFilter;
import cn.rongcloud.pojo.*;
import cn.rongcloud.service.AppVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Api("App 版本号 API")
@RestController
@RequestMapping("/appversion")
@Validated
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;

    @ApiOperation("更新版本号")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseResponse<Boolean> updateVersion(@RequestBody @Validated ReqUpdateAppVersionData data,
                                               @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result = appVersionService.update(data.getPlatform(), data.getUrl(), data.getVersion());
        return new BaseResponse<>(result);
    }

    @ApiOperation("更新版本")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseResponse<Boolean> deleteVersion(@ApiParam("平台, Android:1, iOS:2") @RequestParam @NotNull(message = "platform should not be blank") @Max(2) @Min(1) Integer platform,
                                               @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result = appVersionService.delete(platform);
        return new BaseResponse<>(result);
    }

    @ApiOperation("查询版本信息")
    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public BaseResponse<List<AppVersionResult>> getVersions(@ApiParam("平台, Android:1, iOS:2") @RequestParam(required = false) Integer platform)
            throws ApiException, Exception {
        if (platform != null && !UserAgentTypeEnum.isValid(platform)) {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR);
        }
        List<AppVersionResult> results = appVersionService.getVersions(platform);
        return new BaseResponse<>(results);
    }
}
