package cn.rongcloud.common;

import lombok.Getter;

/**
 * Created by weiqinxiao on 2019/2/25.
 */
public enum ErrorEnum {
    ERR_SUCCESS(0x0000, "OK"),
    ERR_OTHER(0x00FF, "Error"),
    ERR_REQUEST_PARA_ERR(1, "Missing or invalid parameter"),
    ERR_INVALID_AUTH(2, "Invalid or expired authorization"),
    ERR_ACCESS_DENIED(3, "Access denied"),
    ERR_BAD_REQUEST(4, "Bad request"),

    //IM error
    ERR_IM_TOKEN_ERROR(10, "IM token error"),
    ERR_CREATE_ROOM_ERROR(11, "Create room error"),
    ERR_JOIN_ROOM_ERROR(12, "Join room error"),
    ERR_MESSAGE_ERROR(13, "IM Message send error"),

    //room error
    ERR_ROOM_NOT_EXIST(20, "Room not exist"),
    ERR_USER_NOT_EXIST_IN_ROOM(21, "User not exist in room"),
    ERR_EXIT_ROOM_ERROR(22, "Exit room error"),
    ERR_TEACHER_NOT_EXIST_IN_ROOM(23, "Teacher not exist in room"),
    ERR_ASSISTANT_NOT_EXIST_IN_ROOM(24, "Assistant not exist in room"),
    ERR_CREATE_WHITE_BOARD(25, "Create whiteboard error"),
    ERR_WHITE_BOARD_NOT_EXIST(26, "Whiteboard not exist"),
    ERR_DELETE_WHITE_BOARD(27, "Delete whiteboard error"),
    ERR_USER_EXIST_IN_ROOM(28, "User exist in room"),
    ERR_CHANGE_SELF_ROLE(29, "Can not change self role"),
    ERR_APPLY_TICKET_INVALID(30, "Apply ticket invalid"),
    ERR_OVER_MAX_COUNT(31, "Over max count"),
    ERR_TEACHER_EXIST_IN_ROOM(32, "Teacher exist in room"),
    ERR_DOWNGRADE_ROLE(33, "Can't downgrade role"),
    ERR_CHANGE_ROLE(34, "Only change student to teacher"),
    ERR_STUDENT_FULL(35, "Class students are too many"),

    //user error
    ERR_USER_NOT_EXIST(40, "User or school not exist"),
    ERR_USER_PASSWORD_ERROR(41, "User password error"),
    ERR_USER_HAS_EXIST(42, "User has exist"),
    ERR_USER_ROLE_ERROR(43, "User role error"),

    //school error
    ERR_SCHOOL_NOT_EXIST(50, "school not exist"),
    ;

    private @Getter int errCode;
    private @Getter String errMsg;
    private ErrorEnum(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public static ErrorEnum getEnumByValue(long errCode) {
        for(ErrorEnum item : ErrorEnum.values()) {
            if(item.getErrCode() == errCode) {
                return item;
            }
        }

        throw new IllegalArgumentException();
    }
}
