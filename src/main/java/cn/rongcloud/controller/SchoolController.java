package cn.rongcloud.controller;

import cn.rongcloud.common.ApiException;
import cn.rongcloud.common.BaseResponse;
import cn.rongcloud.common.ErrorEnum;
import cn.rongcloud.common.JwtUser;
import cn.rongcloud.filter.JwtFilter;
import cn.rongcloud.pojo.ReqSchoolCreateData;
import cn.rongcloud.pojo.ReqSchoolModifyData;
import cn.rongcloud.pojo.RoleEnum;
import cn.rongcloud.pojo.SchoolResult;
import cn.rongcloud.service.SchoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Api("学校相关 API")
@RestController
@RequestMapping("/school")
@Validated
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @ApiOperation("新建学校")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public BaseResponse<SchoolResult> createSchool(@RequestBody @Validated ReqSchoolCreateData data,
                                                   @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        SchoolResult schoolResult= schoolService.create(data.getName(), data.getPortrait(), data.getManager(), data.getAppkey(), data.getSecret(), jwtUser);
        return new BaseResponse<>(schoolResult);
    }

    @ApiOperation("修改学校信息")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public BaseResponse<SchoolResult> modifySchool(@RequestBody @Validated ReqSchoolModifyData data,
                                                   @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        SchoolResult schoolResult = schoolService.modify(data.getSchoolId(), data.getName(), data.getPortrait(), data.getManager(), data.getAppkey(), data.getSecret(), jwtUser);
        return new BaseResponse<>(schoolResult);
    }

    @ApiOperation("删除学校")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseResponse<Boolean> deleteSchool(@ApiParam("学校 ID") @RequestParam @NotBlank(message = "schoolId should not be blank") String schoolId,
                                              @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        boolean result = schoolService.delete(schoolId, jwtUser);
        return new BaseResponse<>(result);
    }

    @ApiOperation("查询单个学校信息")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public BaseResponse<SchoolResult> getSchool(@ApiParam("学校 ID") @RequestParam @NotBlank(message = "schoolId should not be blank") String schoolId,
                                                @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        if (RoleEnum.getEnumByValue(jwtUser.getRole()) != RoleEnum.RoleSuperAdmin
                && !schoolId.equals(jwtUser.getSchoolId())) {
            throw new ApiException(ErrorEnum.ERR_ACCESS_DENIED);
        }
        SchoolResult schoolResult = schoolService.getSchool(schoolId, jwtUser);
        return new BaseResponse<>(schoolResult);
    }

    @ApiOperation("查询所有学校的信息")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public BaseResponse<List<SchoolResult>> getSchools(@ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        List<SchoolResult> schoolResults = schoolService.getSchools(jwtUser);
        return new BaseResponse<>(schoolResults);
    }
}
