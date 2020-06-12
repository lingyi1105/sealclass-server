package cn.rongcloud.whiteboard;

import cn.rongcloud.config.HereWhiteProperties;
import cn.rongcloud.http.HttpHelper;
import cn.rongcloud.pojo.HereWhiteApiResultInfo;
import cn.rongcloud.pojo.HereWhiteBanInfo;
import cn.rongcloud.pojo.HereWhiteCreateInfo;
import com.alibaba.fastjson.JSON;
import java.net.HttpURLConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HereWhiteHelper {

    @Autowired
    HttpHelper httpHelper;

    @Autowired
    HereWhiteProperties wbProperties;

    public HereWhiteApiResultInfo create(String name) throws Exception {
        if (name == null) {
            throw new IllegalArgumentException("Paramer 'name' is required");
        }

        HereWhiteCreateInfo wbCreateInfo = new HereWhiteCreateInfo();
        wbCreateInfo.setLimit(0);
        wbCreateInfo.setModel("persistent");
        wbCreateInfo.setName(name);

        String body = JSON.toJSONString(wbCreateInfo);

        HttpURLConnection connection= httpHelper.netlessWBPostHttpConnection(wbProperties.getHost(), "/room", "application/json", wbProperties.getToken());
        httpHelper.setBodyParameter(body, connection);

        return JSON
            .parseObject(httpHelper.returnResult(connection, body), HereWhiteApiResultInfo.class);
    }

    public HereWhiteApiResultInfo banRoom(Boolean ban, String uuid) throws Exception {
        if (ban == null) {
            throw new IllegalArgumentException("Paramer 'ban' is required");
        }
        if (uuid == null) {
            throw new IllegalArgumentException("Paramer 'uuid' is required");
        }

        HereWhiteBanInfo wbBanInfo = new HereWhiteBanInfo();
        wbBanInfo.setBan(ban);
        wbBanInfo.setUuid(uuid);
        String body = JSON.toJSONString(wbBanInfo);

        HttpURLConnection connection= httpHelper.netlessWBPostHttpConnection(wbProperties.getHost(), "/banRoom", "application/json", wbProperties.getToken());
        httpHelper.setBodyParameter(body, connection);

        return JSON.parseObject(httpHelper.returnResult(connection, body), HereWhiteApiResultInfo.class);
    }
}
