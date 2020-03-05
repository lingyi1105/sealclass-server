package cn.rongcloud.pojo;

import cn.rongcloud.common.JwtUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("房间内的用户信息")
@Data
public class MemberResult {
    @ApiModelProperty("房间 ID")
    String schoolId;
    @ApiModelProperty("用户 ID")
    String userId;
    @ApiModelProperty("手机号")
    String phone;
    @ApiModelProperty("用户名")
    String userName;
    @ApiModelProperty("用户头像")
    String portrait;
    @ApiModelProperty("用户角色")
    int role;
    @ApiModelProperty("加入时间")
    Date joinTime;
    @ApiModelProperty("摄像头是否打开")
    boolean camera;
    @ApiModelProperty("麦克风是否打开")
    boolean microphone;

    public static MemberResult generate(JwtUser jwtUser, RoomMember member) {
        MemberResult result = new MemberResult();
        result.setSchoolId(jwtUser.getSchoolId());
        result.setUserId(jwtUser.getUserId());
        result.setPhone(jwtUser.getPhone());
        result.setUserName(jwtUser.getUserName());
        result.setPortrait(jwtUser.getPortrait());
        result.setRole(jwtUser.getRole());
        result.setJoinTime(member.getJoinDt());
        result.setCamera(member.isCamera());
        result.setMicrophone(member.isMic());
        return result;
    }

    public static MemberResult generate(UserInfo userInfo, RoomMember member) {
        MemberResult result = new MemberResult();
        result.setSchoolId(userInfo.getSid());
        result.setUserId(userInfo.getUid());
        result.setPhone(userInfo.getPhone());
        result.setUserName(userInfo.getName());
        result.setPortrait(userInfo.getPortrait());
        result.setRole(userInfo.getRole());
        result.setJoinTime(member.getJoinDt());
        result.setCamera(member.isCamera());
        result.setMicrophone(member.isMic());
        return result;
    }
}
