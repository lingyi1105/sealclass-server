package cn.rongcloud.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public enum UserAgentTypeEnum {
	AGENT_ANDROID("Android", 1),
	AGENT_IOS("iOS", 2),
	AGENT_WEB("Web", 3);

	private @Getter @Setter(AccessLevel.PRIVATE) String agent;
	private @Getter @Setter(AccessLevel.PRIVATE) int type;

	private UserAgentTypeEnum(String agent, int type) {
		this.agent = agent;
		this.type = type;
	}

	public static UserAgentTypeEnum getEnumByValue(int type) {
		for (UserAgentTypeEnum item : UserAgentTypeEnum.values()) {
			if (item.getType() == type) {
				return item;
			}
		}

		throw new ApiException(ErrorEnum.ERR_REQUEST_PARA_ERR, type + " not valid platform");
	}

	public static boolean isValid(int type) {
		for(UserAgentTypeEnum item : UserAgentTypeEnum.values()) {
			if(item.getType() == type) {
				return true;
			}
		}
		return false;
	}

	public static UserAgentTypeEnum getEnumByUserAgent(String userAgent) {
		UserAgentTypeEnum type = AGENT_WEB;
		
		if (null != userAgent) {
			userAgent = userAgent.toLowerCase();
			if(userAgent.contains("iphone")||userAgent.contains("ipod")||userAgent.contains("ipad")){
		        type = AGENT_IOS;
		    } else if(userAgent.contains("android")) {
		        type = AGENT_ANDROID;
		    }
		}

		return type;
	}

}
