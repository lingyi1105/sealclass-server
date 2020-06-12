package cn.rongcloud.pojo;

import lombok.Data;

/**
 * Created by weiqinxiao on 2019/3/7.
 */
@Data
public class HereWhiteApiResultInfo {
    private int code;
    private HereWhiteMsg msg;

    public boolean isSuccess() {
        return code == 200;
    }

    @Data
    public static class HereWhiteMsg {
        private HereWhiteRoom room;
        private Hare hare;
        private String roomToken;
        private String code;

    }

    @Data
    public static class Hare {
        private String uuid;
        private String appIdentifier;
        private String appVersion;
        private String appHash;
        private String akkoVersion;
        private String teamId;
        private String mode;
        private String region;
        private Boolean isBan;
        private Long createdAt;
        private Long updatedAt;
        private Integer usersMaxCount;
        private Long survivalDuration;
    }

    @Data
    public static class HereWhiteRoom {
        private Integer id;
        private String name;
        private Integer limit;
        private Integer teamId;
        private Integer adminId;
        private String mode;
        private String template;
        private String region;
        private String uuid;
        private String updatedAt;
        private String createdAt;
    }
}
