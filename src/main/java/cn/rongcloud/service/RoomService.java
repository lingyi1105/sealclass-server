package cn.rongcloud.service;

import cn.rongcloud.common.ApiException;
import cn.rongcloud.common.JwtUser;
import cn.rongcloud.pojo.*;

import java.util.List;

/**
 * Created by weiqinxiao on 2019/2/28.
 */
public interface RoomService {
    RoomResult joinRoom(JwtUser jwtUser, String roomId, String schoolId, boolean isDisableCamera) throws ApiException, Exception;
    Boolean leaveRoom(JwtUser jwtUser, String roomId, String schoolId) throws ApiException, Exception;
    Boolean destroyRoom(JwtUser jwtUser, String roomId, String schoolId) throws ApiException, Exception;
    Boolean kickMember(JwtUser jwtUser, String roomId, String schoolId, String userId) throws ApiException, Exception;
    List<MemberResult> getMembers(JwtUser jwtUser, String roomId, String schoolId) throws  ApiException, Exception;

    Boolean display(JwtUser jwtUser, String roomId, String schoolId, int type, String userId, String uri) throws ApiException, Exception;

    String createWhiteBoard(JwtUser jwtUser, String roomId, String schoolId) throws ApiException, Exception;
    Boolean deleteWhiteboard(JwtUser jwtUser, String roomId, String schoolId, String whiteBoardId) throws ApiException, Exception;
    List<WhiteboardResult> getWhiteboard(JwtUser jwtUser, String roomId, String schoolId) throws ApiException, Exception;
    Boolean turnWhiteBoardPage(JwtUser jwtUser, String roomId, String schoolId, String whiteBoardId, int page) throws ApiException, Exception;

    Boolean controlDevice(JwtUser jwtUser, String roomId, String schoolId, String userId, DeviceTypeEnum type, boolean enable) throws ApiException, Exception;
    Boolean approveControlDevice(JwtUser jwtUser, String roomId, String schoolId, String ticket) throws ApiException, Exception;
    Boolean rejectControlDevice(JwtUser jwtUser, String roomId, String schoolId, String ticket) throws ApiException, Exception;
    Boolean syncDeviceState(JwtUser jwtUser, String roomId, String schoolId, DeviceTypeEnum type, boolean enable) throws ApiException, Exception;
}
