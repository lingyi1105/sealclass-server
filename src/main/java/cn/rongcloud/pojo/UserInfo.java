package cn.rongcloud.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_user")
@DynamicInsert
@DynamicUpdate
public class UserInfo {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private @Getter @Setter String uid;
    private @Getter @Setter String sid;
    private @Getter @Setter String phone;
    private @Getter @Setter String name;
    private @Getter @Setter String portrait;
    private @Getter @Setter int role;
    private @Getter @Setter String password;
    private @Getter @Setter String salt;
    private @Getter @Setter String deviceId;
    private @Getter @Setter Date createDt;
    private @Getter @Setter Date updateDt;

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", sid='" + sid + '\'' +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
                ", role=" + role +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", createDt=" + createDt +
                ", updateDt=" + updateDt +
                '}';
    }
}
