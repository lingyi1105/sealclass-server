package cn.rongcloud.common;

// Display 定义格式如下：
// display://type=1?userId=xxx?uri=xxxxx
// 0，1，3 时，userId 有效，对应此人的 id，uri 无效
// 2 时，展示白板，必须携带白板 uri
// 4 时，清空当前 room 的 display

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public enum DisplayEnum {
    Teacher(1),
    WhiteBoard( 2),
    Screen(3),
    None(4);

    private @Getter
    @Setter(AccessLevel.PRIVATE) int value;

    DisplayEnum(int value) {
        this.value = value;
    }

    public static DisplayEnum getEnumByValue(int v) {
        for(DisplayEnum item : DisplayEnum.values()) {
            if(item.getValue() == v) {
                return item;
            }
        }

        throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, v + " not valid display");
    }

    public static boolean isValid(int v) {
        for(DisplayEnum item : DisplayEnum.values()) {
            if(item.getValue() == v) {
                return true;
            }
        }
        return false;
    }
}
