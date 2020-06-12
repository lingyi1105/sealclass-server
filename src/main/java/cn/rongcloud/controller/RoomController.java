package cn.rongcloud.controller;

import cn.rongcloud.common.*;
import cn.rongcloud.filter.JwtFilter;
import cn.rongcloud.pojo.*;
import cn.rongcloud.service.RoomService;
import cn.rongcloud.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static cn.rongcloud.filter.JwtFilter.USER_AGENT_TYPE;

/**
 * Created by weiqinxiao on 2019/2/25.
 */
@Api("房间, 白板, 设备相关 API")
@RestController
@RequestMapping("/room")
@Validated
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @ApiOperation("加入房间")
    @RequestMapping(value = "/join", method = RequestMethod.POST)
    public BaseResponse<RoomResult> joinRoom(@RequestBody @Validated ReqRoomJoinData data,
                                             @RequestHeader(value = "user-agent", required = false) String userAgent)
            throws ApiException, Exception {
        if (!RoleEnum.isValid(data.getRole())) {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR);
        }
        if (RoleEnum.getEnumByValue(data.getRole()) != RoleEnum.RoleTeacher
                && RoleEnum.getEnumByValue(data.getRole()) != RoleEnum.RoleStudent) {
            throw new ApiException(ErrorEnum.ERR_INVALID_AUTH);
        }
        userService.autoCreate(data.getSchoolId(), data.getPhone(), data.getPhone(), "", data.getRole(), data.getPassword());
        JwtUser jwtUser = userService.loginCheck(data.getSchoolId(), data.getPhone(),data.getRole(), data.getPassword(), data.getDeviceId(), UserAgentTypeEnum.getEnumByUserAgent(userAgent));
        RoomResult roomResult = roomService.joinRoom(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), data.isDisableCamera());
        return new BaseResponse<>(roomResult);
    }

    @ApiOperation("离开房间")
    @RequestMapping(value = "/leave", method = RequestMethod.POST)
    public BaseResponse<Boolean> leaveRoom(@RequestBody @Validated ReqRoomIdData data,
                                           @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result = roomService.leaveRoom(jwtUser, data.getRoomId(), jwtUser.getSchoolId());
        userService.autoDelete(jwtUser.getUserId());
        return new BaseResponse<>(result);
    }

    @ApiOperation(value = "销毁房间", notes = "仅老师能操作")
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public BaseResponse<Boolean> destroyRoom(@RequestBody @Validated ReqRoomIdData data,
                                             @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        List<MemberResult> memberResults = roomService.getMembers(jwtUser, data.getRoomId(), jwtUser.getSchoolId());
        Boolean result = roomService.destroyRoom(jwtUser, data.getRoomId(), jwtUser.getSchoolId());
        for(MemberResult m: memberResults) {
            userService.autoDelete(m.getUserId());
        }
        return new BaseResponse<>(result);
    }

    @ApiOperation(value = "将人踢出房间", notes = "仅老师能操作")
    @RequestMapping(value = "/kick", method = RequestMethod.POST)
    public BaseResponse<Boolean> kickMember(@RequestBody @Validated ReqRoomKickData data,
                                            @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result = roomService.kickMember(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), data.getUserId());
        userService.autoDelete(data.getUserId());
        return new BaseResponse<>(result);
    }

    @ApiOperation("获取房间所有用户信息")
    @RequestMapping(value = "/members", method = RequestMethod.GET)
    public BaseResponse<List<MemberResult>> getMembers(@ApiParam("房间号") @RequestParam @NotBlank String roomId,
                                                       @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA, required = false) JwtUser jwtUser)
            throws ApiException, Exception {
        List<MemberResult> results = roomService.getMembers(jwtUser, roomId, jwtUser.getSchoolId());
        return new BaseResponse<>(results);
    }

    //only teacher
    @ApiOperation(value = "设置主要屏幕显示", notes = "仅老师能操作")
    @RequestMapping(value = "/display", method = RequestMethod.POST)
    public BaseResponse<Boolean> display(@RequestBody @Validated ReqDisplayData data,
                                         @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result = roomService.display(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), data.getType(), data.getUserId(), data.getUri());
        return new BaseResponse<>(result);
    }

    @ApiOperation(value = "新建白板", notes = "仅老师能操作")
    @RequestMapping(value = "/whiteboard/create", method = RequestMethod.POST)
    public BaseResponse<WhiteboardInfo> createWhiteBoard(@RequestBody @Validated ReqRoomIdData data,
                                                 @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        WhiteboardInfo result = roomService.createWhiteBoard(jwtUser, data.getRoomId(), jwtUser.getSchoolId());
        return new BaseResponse<WhiteboardInfo>(result);
    }

    @ApiOperation(value = "删除白板", notes = "仅老师能操作")
    @RequestMapping(value = "/whiteboard/delete", method = RequestMethod.POST)
    public BaseResponse<Boolean> destroyWhiteBoard(@RequestBody @Validated ReqWhiteboardDeleteData data,
                                                   @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        boolean result = roomService.deleteWhiteboard(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), data.getWhiteboardId());
        return new BaseResponse<>(result);
    }

    @ApiOperation("获取房间内的所有白板")
    @RequestMapping(value = "/whiteboard/list", method = RequestMethod.GET)
    public BaseResponse<List<WhiteboardResult>> getWhiteBoard(@ApiParam("房间号") @RequestParam @NotBlank String roomId,
                                                              @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        List<WhiteboardResult> whiteboards = roomService.getWhiteboard(jwtUser, roomId, jwtUser.getSchoolId());
        return new BaseResponse<>(whiteboards);
    }

    @ApiOperation("白板翻页")
    @RequestMapping(value = "/whiteboard/turn-page", method = RequestMethod.POST)
    public BaseResponse<Boolean> turnPage(@RequestBody @Validated ReqWhiteboardTurnPageData data,
                                          @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result = roomService.turnWhiteBoardPage(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), data.getWhiteboardId(), data.getPage());
        return new BaseResponse<>(result);
    }

    @ApiOperation(value = "请求操作他人设备", notes = "仅老师能操作")
    @RequestMapping(value = "/device/control", method = RequestMethod.POST)
    public BaseResponse<Boolean> controlDevice(@RequestBody @Validated ReqDeviceControlData data,
                                               @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result;
        if (data.getCameraOn() != null) {
            result = roomService.controlDevice(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), data.getUserId(), DeviceTypeEnum.Camera, data.getCameraOn());
        } else if (data.getMicrophoneOn() != null) {
            result = roomService.controlDevice(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), data.getUserId(), DeviceTypeEnum.Microphone, data.getMicrophoneOn());
        } else {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR);
        }
        return new BaseResponse<>(result);
    }

    @ApiOperation("接受其他的设备操作请求")
    @RequestMapping(value = "/device/approve", method = RequestMethod.POST)
    public BaseResponse<Boolean> approveControlDevice(@RequestBody @Validated ReqDeviceControlResData data,
                                                      @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result = roomService.approveControlDevice(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), data.getTicket());
        return new BaseResponse<>(result);
    }

    @ApiOperation("拒绝其他的设备操作请求")
    @RequestMapping(value = "/device/reject", method = RequestMethod.POST)
    public BaseResponse<Boolean> rejectControlDevice(@RequestBody @Validated ReqDeviceControlResData data,
                                                     @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result = roomService.rejectControlDevice(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), data.getTicket());
        return new BaseResponse<>(result);
    }

    @ApiOperation("设备操作同步")
    @RequestMapping(value = "/device/sync", method = RequestMethod.POST)
    public BaseResponse<Boolean> syncDeviceState(@RequestBody @Validated ReqDeviceSyncData data,
                                                 @ApiIgnore @RequestAttribute(value = JwtFilter.JWT_AUTH_DATA) JwtUser jwtUser)
            throws ApiException, Exception {
        Boolean result;
        if (data.getCameraOn() != null) {
            result = roomService.syncDeviceState(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), DeviceTypeEnum.Camera, data.getCameraOn());
        } else if (data.getMicrophoneOn() != null) {
            result = roomService.syncDeviceState(jwtUser, data.getRoomId(), jwtUser.getSchoolId(), DeviceTypeEnum.Microphone, data.getMicrophoneOn());
        } else {
            throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR);
        }
        return new BaseResponse<>(result);
    }
}
