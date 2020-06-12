package cn.rongcloud.enums;

public enum WhiteBoardType {
    /**
     * 融云白板
     */
    RONGCLOUD(1),

    /**
     * herewhite
     */
    HEREWHITW(2);

    private int type;

    WhiteBoardType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
