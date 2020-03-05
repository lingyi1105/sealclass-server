package cn.rongcloud.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by weiqinxiao on 2019/2/28.
 */
@Entity
@Table(name = "t_room_member")
@DynamicInsert
@DynamicUpdate
public class RoomMember {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private @Getter @Setter String rid;
    private @Getter @Setter String sid;
    private @Getter @Setter String uid;
    private @Getter @Setter int role;
    private @Getter @Setter boolean camera = true;
    private @Getter @Setter boolean mic = true;
    private @Getter @Setter Date joinDt;

    @Override
    public String toString() {
        return "RoomMember{" +
                "id=" + id +
                ", rid='" + rid + '\'' +
                ", sid='" + sid + '\'' +
                ", uid='" + uid + '\'' +
                ", role=" + role +
                ", camera=" + camera +
                ", mic=" + mic +
                ", joinDt=" + joinDt +
                '}';
    }
}
