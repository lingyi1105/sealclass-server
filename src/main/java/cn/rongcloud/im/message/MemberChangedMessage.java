package cn.rongcloud.im.message;

import cn.rongcloud.im.BaseMessage;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by weiqinxiao on 2019/3/6.
 */
public class MemberChangedMessage extends BaseMessage {
    public final static int Action_Join = 1;
    public final static int Action_Leave = 2;
    public final static int Action_Kick = 3;
    public final static int Action_Destroy = 4;

    private @Getter @Setter int action;
    private @Getter @Setter String schoolId;
    private @Getter @Setter String roomId;
    private @Getter @Setter String userId;
    private @Getter @Setter String userName;
    private @Getter @Setter boolean camera;
    private @Getter @Setter boolean microphone;
    private @Getter @Setter int role;
    private @Getter @Setter Date timestamp;

    public MemberChangedMessage(int action, String userId) {
        this.action = action;
        this.userId = userId;
    }

    @Override
    public String getObjectName() {
        return "SC:RMCMsg";
    }
}
