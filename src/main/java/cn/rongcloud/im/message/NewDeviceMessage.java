package cn.rongcloud.im.message;

import cn.rongcloud.common.UserAgentTypeEnum;
import cn.rongcloud.im.BaseMessage;
import cn.rongcloud.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class NewDeviceMessage extends BaseMessage {

    private @Setter @Getter String deviceId;
    private @Setter @Getter String deviceType;
    private @Setter @Getter Integer platform;
    private @Getter @Setter Date updateDt;

    public NewDeviceMessage(String deviceId, UserAgentTypeEnum platform) {
        this.deviceId = deviceId;
        this.deviceType = platform.getAgent();
        this.platform = platform.getType();
        this.updateDt = DateTimeUtils.currentUTC();
    }

    @Override
    public String getObjectName() {
        return "SC:NewDeviceMsg";
    }
}
