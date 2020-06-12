package cn.rongcloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by sunyinglong on 2020/4/20.
 */
@Data
@Component
@ConfigurationProperties(prefix = "cn.rongcloud.whiteboard.herewhite")
public class HereWhiteProperties {
    private String host;
    private String token;
}
