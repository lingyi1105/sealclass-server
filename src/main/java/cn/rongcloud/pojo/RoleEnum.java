package cn.rongcloud.pojo;

import cn.rongcloud.common.ApiException;
import cn.rongcloud.common.ErrorEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by weiqinxiao on 2019/2/28.
 */
public enum RoleEnum {
    RoleSuperAdmin("RoleSuperAdmin", 1),
    RoleAdmin("RoleAdmin", 2),
    RoleTeacher("RoleTeacher", 10),
    RoleStudent("RoleStudent", 20);

    private @Getter
    @Setter(AccessLevel.PRIVATE) String msg;
    private @Getter
    @Setter(AccessLevel.PRIVATE) int value;

    RoleEnum(String msg, int value) {
        this.msg = msg;
        this.value = value;
    }

    public static RoleEnum getEnumByValue(int v) {
        for(RoleEnum item : RoleEnum.values()) {
            if(item.getValue() == v) {
                return item;
            }
        }

        throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, v + " not valid role");
    }

    public static boolean isValid(int v) {
        for(RoleEnum item : RoleEnum.values()) {
            if(item.getValue() == v) {
                return true;
            }
        }
        return false;
    }
}
