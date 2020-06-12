package cn.rongcloud.service.Impl;

import cn.rongcloud.common.*;
import cn.rongcloud.config.RoomProperties;
import cn.rongcloud.config.WhiteBoardProperties;
import cn.rongcloud.dao.*;
import cn.rongcloud.enums.WhiteBoardType;
import cn.rongcloud.im.IMHelper;
import cn.rongcloud.im.message.*;
import cn.rongcloud.job.ScheduleManager;
import cn.rongcloud.permission.DeclarePermissions;
import cn.rongcloud.pojo.*;
import cn.rongcloud.service.RoomService;
import cn.rongcloud.service.UserService;
import cn.rongcloud.utils.CheckUtils;
import cn.rongcloud.utils.DateTimeUtils;
import cn.rongcloud.utils.IdentifierUtils;
import cn.rongcloud.whiteboard.HereWhiteHelper;
import cn.rongcloud.whiteboard.WhiteBoardHelper;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by weiqinxiao on 2019/2/28.
 */
@Slf4j
@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private IMHelper imHelper;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private RoomMemberDao roomMemberDao;

    @Autowired
    private JwtTokenHelper tokenHelper;

    @Autowired
    private WhiteBoardHelper whiteBoardHelper;

    @Autowired
    private HereWhiteHelper hereWhiteHelper;

    @Autowired
    private WhiteboardDao whiteboardDao;

    @Autowired
    private WhiteBoardProperties whiteBoardProperties;

    @Autowired
    private RoomProperties roomProperties;

    @Autowired
    @Lazy
    private ScheduleManager scheduleManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    @Lazy
    private UserService userService;

    public Room createRoomIfNeed(JwtUser jwtUser, String roomId, String schoolId, boolean isDisableCamera) throws ApiException, Exception {
        log.info("createRoomIfNeed: jwtUser={}, roomId={}, schoolId={}, isDisableCamera={}", jwtUser, roomId, schoolId, isDisableCamera);

        List<Room> rooms = roomDao.findByRidAndSid(roomId, schoolId);
        if (!rooms.isEmpty()) {
            return rooms.get(0);
        }

        IMApiResultInfo resultInfo = imHelper.createGroup(jwtUser.getAppkey(), jwtUser.getSecret(), new String[]{jwtUser.getUserId()}, roomId, roomId);
        if (!resultInfo.isSuccess()) {
            log.error("joinRoom roomId={}, schoolId={}, IM error:{}", roomId, schoolId, resultInfo.getErrorMessage());
            throw new ApiException(ErrorEnum.ERR_CREATE_ROOM_ERROR, resultInfo.getErrorMessage());
        }

        Date curTime = DateTimeUtils.currentUTC();

        Room room = new Room();
        room.setRid(roomId);
        room.setSid(schoolId);
        room.setName(roomId);
        room.setPortrait(null);
        room.setDisplay("");
        room.setWhiteboardNameIndex(0);
        room.setCreateDt(curTime);
        room.setUpdateDt(curTime);
        roomDao.save(room);
        return room;
    }

    @Transactional
    @Override
    public RoomResult joinRoom(JwtUser jwtUser, String roomId, String schoolId, boolean isDisableCamera) throws ApiException, Exception {
        log.info("joinRoom: jwtUser={}, roomId={}, schoolId={}, isDisableCamera={}", jwtUser, roomId, schoolId, isDisableCamera);

        Room room = createRoomIfNeed(jwtUser, roomId, schoolId, isDisableCamera);
        Date curTime = DateTimeUtils.currentUTC();

        RoomMember roomMember = null;
        List<RoomMember> members = roomMemberDao.findByRidAndSidAndRole(roomId, schoolId, jwtUser.getRole());
        if (RoleEnum.getEnumByValue(jwtUser.getRole()) == RoleEnum.RoleTeacher) {
            if (!members.isEmpty()) {
                if (!jwtUser.getUserId().equals(members.get(0).getUid())) {
                    throw new ApiException(ErrorEnum.ERR_TEACHER_EXIST_IN_ROOM);
                }
                roomMember = members.get(0);
            }
        } else {
            for (RoomMember m: members) {
                if (jwtUser.getUserId().equals(m.getUid())) {
                    roomMember = m;
                    break;
                }
            }
//            if (roomMember != null && members.size() >= roomProperties.getMaxCount() - 1) {
//                throw new ApiException(ErrorEnum.ERR_STUDENT_FULL);
//            } else if (roomMember == null && members.size() >= roomProperties.getMaxCount() - 2) {
//                throw new ApiException(ErrorEnum.ERR_STUDENT_FULL);
//            }
        }

        if (RoleEnum.getEnumByValue(jwtUser.getRole()) == RoleEnum.RoleTeacher) {
            log.info("joinRoom, display changed: roomId={}, schoolId={}, userId={}", roomId, schoolId, jwtUser.getUserId());

            String display = "display://type=1?userId=" + jwtUser.getUserId() + "?uri=";
            room.setDisplay(display);
            room.setUpdateDt(curTime);
            roomDao.save(room);

            imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, new DisplayMessage(display), 0);
        }

        if (roomMember == null) {
            log.info("jwtUser {} join the room: roomId={}, schoolId={}, camera {}", jwtUser, roomId, schoolId, !isDisableCamera);

            IMApiResultInfo resultInfo = imHelper.joinGroup(jwtUser.getAppkey(), jwtUser.getSecret(), new String[]{jwtUser.getUserId()}, roomId, roomId);
            if (!resultInfo.isSuccess()) {
                throw new ApiException(ErrorEnum.ERR_CREATE_ROOM_ERROR, resultInfo.getErrorMessage());
            }

            roomMember = new RoomMember();
            roomMember.setRid(roomId);
            roomMember.setSid(schoolId);
            roomMember.setUid(jwtUser.getUserId());
            roomMember.setJoinDt(curTime);
        }

        roomMember.setRole(jwtUser.getRole());
        roomMember.setCamera(!isDisableCamera);
        roomMember.setMic(true);
        roomMemberDao.save(roomMember);

        MemberChangedMessage msg = new MemberChangedMessage(MemberChangedMessage.Action_Join, jwtUser.getUserId());
        msg.setSchoolId(schoolId);
        msg.setRoomId(roomId);
        msg.setUserId(jwtUser.getUserId());
        msg.setUserName(jwtUser.getUserName());
        msg.setCamera(roomMember.isCamera());
        msg.setMicrophone(roomMember.isMic());
        msg.setRole(jwtUser.getRole());
        msg.setTimestamp(curTime);
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, msg);

        RoomResult roomResult = new RoomResult();
        roomResult.setUserInfo(MemberResult.generate(jwtUser, roomMember));
        roomResult.setDisplay(room.getDisplay());
        roomResult.setRoomId(roomId);
        roomResult.setWhiteboards(whiteboardDao.findByRidAndSid(roomId, schoolId));

        List<RoomMember> roomMemberList = roomMemberDao.findByRidAndSid(roomId, schoolId);
        for (RoomMember member: roomMemberList) {
            UserInfo userInfo = userDao.findByUid(member.getUid()).get(0);
            roomResult.getMembers().add(MemberResult.generate(userInfo, member));
        }

        JwtToken jwtToken = tokenHelper.createJwtToken(jwtUser);
        if (RoleEnum.getEnumByValue(jwtUser.getRole()) == RoleEnum.RoleTeacher
                || RoleEnum.getEnumByValue(jwtUser.getRole()) == RoleEnum.RoleStudent) {
            roomResult.setImToken(userService.getIMToken(jwtUser));
        }

        roomResult.setAppkey(jwtUser.getAppkey());
        roomResult.setAuthorization(jwtToken.getToken());

        log.info("join success: roomId = {}, jwtUser = {}", roomId, jwtUser);

        return roomResult;
    }

    @DeclarePermissions({RoleEnum.RoleTeacher, RoleEnum.RoleStudent})
    @Transactional
    @Override
    public Boolean leaveRoom(JwtUser jwtUser, String roomId, String schoolId) throws Exception {
        log.info("leaveRoom: jwtUser={}, roomId={}, schoolId={}", jwtUser, roomId, schoolId);

        List<Room> roomList = roomDao.findByRidAndSid(roomId, schoolId);
        if (roomList.isEmpty()) {
            log.error("room:{}, schoolId:{} not exist ", roomId, schoolId);
            throw new ApiException(ErrorEnum.ERR_ROOM_NOT_EXIST);
        }
        Room room = roomList.get(0);

        List<RoomMember> roomMemberList = roomMemberDao.findByRidAndSidAndUid(roomId, schoolId, jwtUser.getUserId());
        if (roomMemberList.isEmpty()) {
            log.error("{} not exist in room: {}, schoolId {}", jwtUser, roomId, schoolId);
            throw new ApiException(ErrorEnum.ERR_USER_NOT_EXIST_IN_ROOM);
        }
        RoomMember member = roomMemberList.get(0);

        Date curTime = DateTimeUtils.currentUTC();

        if (RoleEnum.getEnumByValue(jwtUser.getRole()) == RoleEnum.RoleTeacher && isUserDisplay(room, jwtUser.getUserId())) {
            log.info("leave clear display: roomId={}, schoolId={}, userId={}", roomId, schoolId, jwtUser.getUserId());

            String display = "";
            room.setDisplay(display);
            room.setUpdateDt(curTime);
            roomDao.save(room);

            imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, new DisplayMessage(display), 0);
        }

        MemberChangedMessage msg = new MemberChangedMessage(MemberChangedMessage.Action_Leave, jwtUser.getUserId());
        msg.setSchoolId(schoolId);
        msg.setUserId(jwtUser.getUserId());
        msg.setRoomId(roomId);
        msg.setUserName(jwtUser.getUserName());
        msg.setCamera(member.isCamera());
        msg.setMicrophone(member.isMic());
        msg.setRole(jwtUser.getRole());
        msg.setTimestamp(curTime);
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, msg);
        imHelper.quit(jwtUser.getAppkey(), jwtUser.getSecret(), new String[]{jwtUser.getUserId()}, roomId);

        roomMemberDao.deleteUserByRidAndSidAndUid(roomId, schoolId, jwtUser.getUserId());

        whiteboardDao.deleteByRidAndSidAndCreator(roomId, schoolId, jwtUser.getUserId());

        return Boolean.TRUE;
    }

    @DeclarePermissions({RoleEnum.RoleSuperAdmin, RoleEnum.RoleAdmin, RoleEnum.RoleTeacher})
    @Transactional
    @Override
    public Boolean destroyRoom(JwtUser jwtUser, String roomId, String schoolId) throws Exception {
        log.info("destroyRoom: roomId={}, schoolId={}", roomId, schoolId);

        List<Room> roomList = roomDao.findByRidAndSid(roomId, schoolId);
        if (roomList.isEmpty()) {
            log.error("room:{}, schoolId:{} not exist ", roomId, schoolId);
            throw new ApiException(ErrorEnum.ERR_ROOM_NOT_EXIST);
        }

        roomDao.deleteByRidAndSid(roomId, schoolId);

        List<Whiteboard> whiteboardList = whiteboardDao.findByRidAndSid(roomId, schoolId);
        for (Whiteboard wb : whiteboardList) {
            remoteDestroyWhiteBoard(jwtUser.getAppkey(), wb.getWbRoom(), wb.getWbid());
        }
        whiteboardDao.deleteByRidAndSid(roomId, schoolId);

        MemberChangedMessage msg = new MemberChangedMessage(MemberChangedMessage.Action_Destroy, jwtUser.getUserId());
        msg.setSchoolId(schoolId);
        msg.setUserId(jwtUser.getUserId());
        msg.setRoomId(roomId);
        msg.setUserName(jwtUser.getUserName());
        msg.setRole(jwtUser.getRole());
        msg.setTimestamp(DateTimeUtils.currentUTC());
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, msg);
        imHelper.dismiss(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId);

        roomMemberDao.deleteByRidAndSid(roomId, schoolId);

        return Boolean.TRUE;
    }

    @DeclarePermissions(RoleEnum.RoleTeacher)
    @Override
    public Boolean kickMember(JwtUser jwtUser, String roomId, String schoolId, String userId) throws ApiException, Exception {
        log.info("kickMember: jwtUser={}, roomId={}, schoolId={}, userId={}", jwtUser, roomId, schoolId, userId);

        List<UserInfo> userInfos = userDao.findByUid(userId);
        if (userInfos.isEmpty()) {
            log.error("userId:{} not exist", userId);
            throw new ApiException(ErrorEnum.ERR_USER_NOT_EXIST);
        }
        UserInfo userInfo = userInfos.get(0);

        List<Room> roomList = roomDao.findByRidAndSid(roomId, schoolId);
        if (roomList.isEmpty()) {
            log.error("room:{}, schoolId:{} not exist", roomId, schoolId);
            throw new ApiException(ErrorEnum.ERR_ROOM_NOT_EXIST);
        }
        Room room = roomList.get(0);

        List<RoomMember> roomMemberList = roomMemberDao.findByRidAndSidAndUid(roomId, schoolId, userId);
        if (roomMemberList.isEmpty()) {
            log.error("{} not exist in room: {}, schoolId {}", userId, roomId, schoolId);
            throw new ApiException(ErrorEnum.ERR_USER_NOT_EXIST_IN_ROOM);
        }
        RoomMember member = roomMemberList.get(0);

        Date curTime = DateTimeUtils.currentUTC();

        if (RoleEnum.getEnumByValue(userInfo.getRole()) == RoleEnum.RoleTeacher && isUserDisplay(room, userId)) {
            log.info("kick clear display: roomId={}, schoolId={}, userId={}", roomId, schoolId, userId);

            String display = "";
            room.setDisplay(display);
            room.setUpdateDt(curTime);
            roomDao.save(room);

            imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), userId, roomId, new DisplayMessage(display), 0);
        }

        MemberChangedMessage msg = new MemberChangedMessage(MemberChangedMessage.Action_Kick, userId);
        msg.setSchoolId(schoolId);
        msg.setRoomId(roomId);
        msg.setUserId(userId);
        msg.setUserName(userInfo.getName());
        msg.setCamera(member.isCamera());
        msg.setMicrophone(member.isMic());
        msg.setRole(userInfo.getRole());
        msg.setTimestamp(curTime);
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, msg);
        imHelper.quit(jwtUser.getAppkey(), jwtUser.getSecret(), new String[]{userId}, roomId);

        roomMemberDao.deleteUserByRidAndSidAndUid(roomId, schoolId, userId);

        whiteboardDao.deleteByRidAndSidAndCreator(roomId, schoolId, userId);

        return Boolean.TRUE;
    }

    @DeclarePermissions(RoleEnum.RoleTeacher)
    @Override
    public Boolean display(JwtUser jwtUser, String roomId, String schoolId, int type, String userId, String uri) throws ApiException, Exception {
        log.info("jwtUser {} display in room={}, schoolId={}, type={}, uri={}", jwtUser, roomId, schoolId, type, uri);

        String display;
        DisplayEnum displayEnum = DisplayEnum.getEnumByValue(type);
        switch (displayEnum) {
            case Teacher:
                if (roomMemberDao.findByRidAndSidAndUid(roomId, jwtUser.getSchoolId(), userId).isEmpty()
                        || RoleEnum.getEnumByValue(userDao.findByUid(userId).get(0).getRole()) != RoleEnum.RoleTeacher) {
                    throw new ApiException(ErrorEnum.ERR_TEACHER_NOT_EXIST_IN_ROOM);
                }
                display = "display://type=" + type + "?userId=" + userId + "?uri=";
                break;
            case WhiteBoard:
                if (whiteBoardProperties.getType() == WhiteBoardType.RONGCLOUD.getType() && StringUtils.isBlank(uri)) {
                    throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "uri must't be null");
                }
                if (whiteBoardProperties.getType() == WhiteBoardType.RONGCLOUD.getType()) {
                    if (whiteboardDao.findByRidAndSidAndWbid(roomId, jwtUser.getSchoolId(), uri).isEmpty()) {
                        throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "whiteboard not exist");
                    }
                    display = getWhiteBoardDisplay("", uri, null);
                } else if (whiteBoardProperties.getType() == WhiteBoardType.HEREWHITW.getType()) {
                    List<Whiteboard> whiteboards = whiteboardDao.findByRidAndSid(roomId, jwtUser.getSchoolId());
                    if (whiteboards.isEmpty()) {
                        throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, "whiteboard not exist");
                    }
                    Whiteboard whiteboard = whiteboards.get(0);
                    display = getWhiteBoardDisplay("", whiteboard.getWbid(), whiteboard.getWbRoomToken());
                } else {
                    display = "";
                }
                break;
            case Screen:
                display = "display://type=" + type + "?userId=" + userId + "?uri=";
                break;
            case None:
            default:
                display = "";
                break;
        }
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, new DisplayMessage(display), 1);
        roomDao.updateDisplayByRidAndSid(roomId, schoolId, display);

        log.info("jwtUser {} display {} in room={}, schoolId={}, type={}, uri={}", jwtUser, display, roomId, schoolId, type, uri);
        return true;
    }

    @DeclarePermissions(RoleEnum.RoleTeacher)
    @Override
    public WhiteboardInfo createWhiteBoard(JwtUser jwtUser, String roomId, String schoolId) throws ApiException, Exception {
        log.info("createWhiteBoard: jwtUser={}, roomId ={}, schoolId={}", jwtUser, roomId, schoolId);
        List<Room> roomList = roomDao.findByRidAndSid(roomId, schoolId);
        if (roomList.isEmpty()) {
            log.error("room:{}, schoolId:{} not exist", roomId, schoolId);
            throw new ApiException(ErrorEnum.ERR_ROOM_NOT_EXIST);
        }
        Room room = roomList.get(0);

        List<RoomMember> memberList = roomMemberDao.findByRidAndSidAndUid(roomId, schoolId, jwtUser.getUserId());
        if (memberList.isEmpty()) {
            log.error("jwtUser={} not exist in room:{}, schoolId:{}", jwtUser, roomId, schoolId);
            throw new ApiException(ErrorEnum.ERR_USER_NOT_EXIST_IN_ROOM);
        }
        RoomMember member = memberList.get(0);

        String wbRoom = IdentifierUtils.uuid();
        Date date = DateTimeUtils.currentUTC();

        int whiteboardNameIndex = room.getWhiteboardNameIndex() + 1;
        String name = "白板" + whiteboardNameIndex;
        WhiteboardInfo resultInfo = remoteCreateWhiteBoard(jwtUser.getAppkey(), wbRoom, name);

        roomDao.updateWhiteboardNameIndexByRidAndSid(roomId, schoolId, whiteboardNameIndex);

        if (whiteBoardProperties.getType() == WhiteBoardType.RONGCLOUD.getType()) {
            // 当白板服务为融云白板时，才需要发送此通知
            WhiteboardMessage wbmsg = new WhiteboardMessage(WhiteboardMessage.Create);
            wbmsg.setWhiteboardId(resultInfo.getId());
            wbmsg.setWhiteboardName(name);
            wbmsg.setWhiteboardType(WhiteBoardType.RONGCLOUD.getType());
            imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, wbmsg);
        }

        Whiteboard wb = new Whiteboard();
        wb.setRid(roomId);
        wb.setSid(schoolId);
        wb.setWbRoom(wbRoom);
        wb.setWbid(resultInfo.getId());
        wb.setWbRoomToken(resultInfo.getRoomToken());
        wb.setName(name);
        wb.setCreator(jwtUser.getUserId());
        wb.setCurPg(0);
        wb.setCreateDt(date);
        wb.setUpdateDt(date);
        whiteboardDao.save(wb);

        String display = getWhiteBoardDisplay(jwtUser.getUserId(), resultInfo.getId(), resultInfo.getRoomToken());
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, new DisplayMessage(display), 1);
        roomDao.updateDisplayByRidAndSid(roomId, schoolId, display);

        return resultInfo;
    }
    
    private String getWhiteBoardDisplay(String userId, String wbId, String wbRoomToken){
        String display = "display://type=" + DisplayEnum.WhiteBoard.getValue() + "?whiteboardType=" + whiteBoardProperties.getType() + "?userId=" + userId;
        if (whiteBoardProperties.getType() == WhiteBoardType.RONGCLOUD.getType()) {
            display = display + "?uri=" + wbId;
        } else if (whiteBoardProperties.getType() == WhiteBoardType.HEREWHITW.getType()) {
            display = display + "?whiteboardId=" + wbId + "?whiteboardRoomToken=" + wbRoomToken;
        }
        return display;
    }

    private WhiteboardInfo remoteCreateWhiteBoard(String appKey, String roomId, String name)
        throws Exception {
        WhiteboardInfo wbInfo = new WhiteboardInfo();
        if (whiteBoardProperties.getType() == WhiteBoardType.RONGCLOUD.getType()) {
            WhiteBoardApiResultInfo resultInfo = whiteBoardHelper.create(appKey, roomId);
            if (!resultInfo.isSuccess()) {
                throw new ApiException(ErrorEnum.ERR_CREATE_WHITE_BOARD, resultInfo.getMsg());
            }
            String wbId = resultInfo.getData();
            wbId = wbId.replace(whiteBoardProperties.getOrigin(), whiteBoardProperties.getReplace());
            wbInfo.setId(wbId);
        } else if (whiteBoardProperties.getType() == WhiteBoardType.HEREWHITW.getType()){
            HereWhiteApiResultInfo resultInfo = hereWhiteHelper.create(name);
            if (!resultInfo.isSuccess()) {
                throw new ApiException(ErrorEnum.ERR_CREATE_WHITE_BOARD);
            }
            wbInfo.setId(resultInfo.getMsg().getRoom().getUuid());
            wbInfo.setRoomToken(resultInfo.getMsg().getRoomToken());
        }
        wbInfo.setType(whiteBoardProperties.getType());
        return wbInfo;
    }

    @DeclarePermissions(RoleEnum.RoleTeacher)
    @Override
    public Boolean deleteWhiteboard(JwtUser jwtUser, String roomId, String schoolId, String whiteBoardId) throws ApiException, Exception {
        log.info("deleteWhiteboard: jwtUser={}, roomId={}, schoolId={}, whiteBoardId={}", jwtUser, roomId, schoolId, whiteBoardId);

        List<Whiteboard> whiteboardList = new ArrayList<>();
        if (whiteBoardProperties.getType() == WhiteBoardType.RONGCLOUD.getType()) {
            whiteboardList = whiteboardDao.findByRidAndSidAndWbid(roomId, schoolId, whiteBoardId);
        }
        if (whiteBoardProperties.getType() == WhiteBoardType.HEREWHITW.getType()) {
            whiteboardList = whiteboardDao.findByRidAndSid(roomId, schoolId);
        }
        if (whiteboardList.isEmpty()) {
            throw new ApiException(ErrorEnum.ERR_WHITE_BOARD_NOT_EXIST);
        }

        List<Room> roomList = roomDao.findByRidAndSid(roomId, schoolId);
        if (roomList.isEmpty()) {
            throw new ApiException(ErrorEnum.ERR_ROOM_NOT_EXIST);
        }
        Room room = roomList.get(0);

        Whiteboard whiteboard = whiteboardList.get(0);
        whiteBoardId = whiteboard.getWbid();
        remoteDestroyWhiteBoard(jwtUser.getAppkey(), whiteboard.getWbRoom(), whiteBoardId);

        if (room.getDisplay().contains("uri=" + whiteBoardId) || room.getDisplay().contains("whiteboardId=" + whiteBoardId)) {
            String display = "";
            roomDao.updateDisplayByRidAndSid(roomId, jwtUser.getSchoolId(), display);
            imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId,  new DisplayMessage(display), 1);
        }

        whiteboardDao.deleteByWbid(whiteBoardId);

        if (whiteBoardProperties.getType() == WhiteBoardType.RONGCLOUD.getType()) {
            // 当白板服务为融云白板时，才需要发送此通知
            WhiteboardMessage wbmsg = new WhiteboardMessage(WhiteboardMessage.Delete);
            wbmsg.setWhiteboardId(whiteBoardId);
            wbmsg.setWhiteboardType(WhiteBoardType.RONGCLOUD.getType());
            imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, wbmsg, 1);
        }

        return Boolean.TRUE;
    }

    private void remoteDestroyWhiteBoard(String appKey, String roomId, String wbId)
        throws Exception {
        if (whiteBoardProperties.getType() == WhiteBoardType.RONGCLOUD.getType()) {
            WhiteBoardApiResultInfo resultInfo = whiteBoardHelper.destroy(appKey, roomId);
            if (!resultInfo.isSuccess()) {
                throw new ApiException(ErrorEnum.ERR_DELETE_WHITE_BOARD, resultInfo.getMsg());
            }
        } else if (whiteBoardProperties.getType() == WhiteBoardType.HEREWHITW.getType()) {
            HereWhiteApiResultInfo resultInfo = hereWhiteHelper.banRoom(true, wbId);
            if (!resultInfo.isSuccess()) {
                throw new ApiException(ErrorEnum.ERR_DELETE_WHITE_BOARD);
            }
        }
    }

    @Override
    public List<WhiteboardResult> getWhiteboard(JwtUser jwtUser, String roomId, String schoolId) throws ApiException, Exception {
        List<Room> rooms = roomDao.findByRidAndSid(roomId, schoolId);
        if (rooms.isEmpty()) {
            throw new ApiException(ErrorEnum.ERR_ROOM_NOT_EXIST);
        }
        Room room = rooms.get(0);

        List<Whiteboard> whiteboards = whiteboardDao.findByRidAndSid(roomId, schoolId);
        List<WhiteboardResult> results = new ArrayList<>();
        for (Whiteboard wb : whiteboards) {
            WhiteboardResult result = new WhiteboardResult();
            result.setRoomId(roomId);
            result.setRoomId(schoolId);
            result.setName(wb.getName());
            result.setCurPg(wb.getCurPg());
            result.setWhiteboardId(wb.getWbid());
            results.add(result);
        }
        return results;
    }

    @DeclarePermissions(RoleEnum.RoleTeacher)
    @Override
    public Boolean turnWhiteBoardPage(JwtUser jwtUser, String roomId, String schoolId, String whiteBoardId, int page) throws ApiException, Exception {
        log.info("turnWhiteBoardPage jwtUser={}, roomId={}, schoolId={}, whiteBoardId={}, page={}", jwtUser, roomId, schoolId, whiteBoardId, page);

        List<Room> roomList = roomDao.findByRidAndSid(roomId, schoolId);
        if (roomList.isEmpty()) {
            throw new ApiException(ErrorEnum.ERR_ROOM_NOT_EXIST);
        }

        whiteboardDao.updatePageByRidAndWbid(roomId, schoolId, whiteBoardId, page);

        TurnPageMessage turnPageMessage = new TurnPageMessage(roomId, schoolId, whiteBoardId, jwtUser.getUserId(), page);
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, turnPageMessage);

        return Boolean.TRUE;
    }

    @DeclarePermissions(RoleEnum.RoleTeacher)
    @Override
    public Boolean controlDevice(JwtUser jwtUser, String roomId, String schoolId, String userId, DeviceTypeEnum typeEnum, boolean enable) throws ApiException, Exception {
        log.info("controlDevice: {}, userId={}, typeEnum={}, onOff={}", jwtUser, userId, typeEnum, enable);

        CheckUtils.checkArgument(roomDao.existsByRidAndSid(roomId, jwtUser.getSchoolId()), "room not exist");
        CheckUtils.checkArgument(roomMemberDao.existsByRidAndSidAndUid(roomId, jwtUser.getSchoolId(), userId), "room member not exist");

        if (enable) {
            String ticket = IdentifierUtils.uuid();

            ControlDeviceTaskInfo taskInfo = new ControlDeviceTaskInfo();
            taskInfo.setRoomId(roomId);
            taskInfo.setTypeEnum(typeEnum);
            taskInfo.setOnOff(true);
            taskInfo.setApplyUserId(jwtUser.getUserId());
            taskInfo.setTargetUserId(userId);
            taskInfo.setTicket(ticket);
            scheduleManager.addTask(jwtUser.getAppkey(), jwtUser.getSecret(), taskInfo);

            ControlDeviceNotifyMessage msg = new ControlDeviceNotifyMessage(ActionEnum.Invite.ordinal());
            msg.setTicket(ticket);
            msg.setType(taskInfo.getTypeEnum().ordinal());
            msg.setOpUserId(jwtUser.getUserId());
            msg.setOpUserName(jwtUser.getUserName());
            imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), userId, roomId, msg);
        } else {
            if (typeEnum.equals(DeviceTypeEnum.Camera)) {
                roomMemberDao.updateCameraByRidAndSidAndUid(roomId, jwtUser.getSchoolId(), jwtUser.getUserId(), false);
            } else {
                roomMemberDao.updateMicByRidAndSidAndUid(roomId, jwtUser.getSchoolId(), jwtUser.getUserId(), false);
            }
            DeviceStateChangedMessage deviceResourceMessage = new DeviceStateChangedMessage(typeEnum.ordinal(), false);
            deviceResourceMessage.setUserId(userId);
            List<UserInfo> userInfoList = userDao.findByUid(userId);
            if (!userInfoList.isEmpty()) {
                deviceResourceMessage.setUserName(userInfoList.get(0).getName());
            }
            imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, deviceResourceMessage, 1);
        }
        return true;
    }

    @DeclarePermissions({RoleEnum.RoleTeacher, RoleEnum.RoleStudent})
    @Override
    public Boolean approveControlDevice(JwtUser jwtUser, String roomId, String schoolId, String ticket) throws ApiException, Exception {
        log.info("approveControlDevice: jwtUser={}, roomId={}, schoolId={}, ticket={}", jwtUser, roomId, schoolId, ticket);

        ControlDeviceTaskInfo taskInfo = (ControlDeviceTaskInfo) scheduleManager.cancelTask(ticket);
        if (taskInfo.getTypeEnum().equals(DeviceTypeEnum.Camera)) {
            roomMemberDao.updateCameraByRidAndSidAndUid(roomId, jwtUser.getSchoolId(), jwtUser.getUserId(), taskInfo.isOnOff());
        } else {
            roomMemberDao.updateMicByRidAndSidAndUid(roomId, jwtUser.getSchoolId(), jwtUser.getUserId(), taskInfo.isOnOff());
        }

        ControlDeviceNotifyMessage msg = new ControlDeviceNotifyMessage(ActionEnum.Approve.ordinal());
        msg.setType(taskInfo.getTypeEnum().ordinal());
        msg.setOpUserId(jwtUser.getUserId());
        msg.setOpUserName(jwtUser.getUserName());
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), taskInfo.getApplyUserId(), roomId, msg);

        DeviceStateChangedMessage deviceResourceMessage = new DeviceStateChangedMessage(taskInfo.getTypeEnum().ordinal(), taskInfo.isOnOff());
        deviceResourceMessage.setUserId(jwtUser.getUserId());
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, deviceResourceMessage, 1);

        return Boolean.TRUE;
    }

    @DeclarePermissions({RoleEnum.RoleTeacher, RoleEnum.RoleStudent})
    @Override
    public Boolean rejectControlDevice(JwtUser jwtUser, String roomId, String schoolId, String ticket) throws ApiException, Exception {
        log.info("rejectControlDevice: jwtUser={}, roomId={}, schoolId={}, ticket={}", jwtUser, roomId, schoolId, ticket);

        CheckUtils.checkArgument(ticket != null, "ticket must't be null");

        ControlDeviceTaskInfo taskInfo = (ControlDeviceTaskInfo) scheduleManager.cancelTask(ticket);
        ControlDeviceNotifyMessage msg = new ControlDeviceNotifyMessage(ActionEnum.Reject.ordinal());
        msg.setType(taskInfo.getTypeEnum().ordinal());
        msg.setOpUserId(jwtUser.getUserId());
        msg.setOpUserName(jwtUser.getUserName());
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), taskInfo.getApplyUserId(), roomId, msg);

        return Boolean.TRUE;
    }

    @DeclarePermissions({RoleEnum.RoleTeacher, RoleEnum.RoleStudent})
    @Override
    public Boolean syncDeviceState(JwtUser jwtUser, String roomId, String schoolId, DeviceTypeEnum type, boolean enable) throws ApiException, Exception {
        CheckUtils.checkArgument(roomId != null, "roomId must't be null");
        CheckUtils.checkArgument(roomDao.existsByRidAndSid(roomId, schoolId), "room not exist");

        int result;
        DeviceStateChangedMessage deviceResourceMessage;
        if (type.equals(DeviceTypeEnum.Camera)) {
            result = roomMemberDao.updateCameraByRidAndSidAndUid(roomId, jwtUser.getSchoolId(), jwtUser.getUserId(), enable);
            deviceResourceMessage = new DeviceStateChangedMessage(type.ordinal(), enable);
        } else {
            result = roomMemberDao.updateMicByRidAndSidAndUid(roomId, jwtUser.getSchoolId(), jwtUser.getUserId(), enable);
            deviceResourceMessage = new DeviceStateChangedMessage(type.ordinal(), enable);
        }
        deviceResourceMessage.setUserId(jwtUser.getUserId());
        imHelper.publishMessage(jwtUser.getAppkey(), jwtUser.getSecret(), jwtUser.getUserId(), roomId, deviceResourceMessage, 1);
        log.info("syncDeviceState : {}, {}, result = {}, jwtUser={}", roomId, enable, result, jwtUser);
        return true;
    }

    @Override
    public List<MemberResult> getMembers(JwtUser jwtUser, String roomId, String schoolId) throws ApiException, Exception {
        CheckUtils.checkArgument(roomId != null, "roomId must't be null");
        CheckUtils.checkArgument(schoolId != null, "schoolId must't be null");

        List<MemberResult> memberResults = new ArrayList<>();
        List<RoomMember> roomMemberList = roomMemberDao.findByRidAndSid(roomId, schoolId);
        for (RoomMember member: roomMemberList) {
            UserInfo userInfo = userDao.findByUid(member.getUid()).get(0);
            memberResults.add(MemberResult.generate(userInfo, member));
        }

        return memberResults;
    }

    private boolean isUserDisplay(Room room, String userId) {
        boolean result = false;
        if (!room.getDisplay().isEmpty() && room.getDisplay().contains("userId=" + userId)) {
            if (room.getDisplay().contains("type=0") || room.getDisplay().contains("type=1") || room.getDisplay().contains("type=3")) {
                result = true;
            }
        }
        return result;
    }
}
