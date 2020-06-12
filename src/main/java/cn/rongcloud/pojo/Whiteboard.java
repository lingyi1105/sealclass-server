package cn.rongcloud.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by weiqinxiao on 2019/2/28.
 */
@Entity
@Table(name = "t_whiteboard")
@DynamicInsert
@DynamicUpdate
public class Whiteboard implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Getter long id;

    private @Getter @Setter String rid;
    private @Getter @Setter String sid;
    private @Getter @Setter String wbRoom;
    private @Getter @Setter String wbid;
    private @Getter @Setter String wbRoomToken;
    private @Getter @Setter String name;
    private @Getter @Setter String creator;
    private @Getter @Setter int pgCount;
    private @Getter @Setter int curPg;
    private @Getter @Setter Date createDt;
    private @Getter @Setter Date updateDt;

    @Override
    public String toString() {
        return "Whiteboard{" +
                "id=" + id +
                ", rid='" + rid + '\'' +
                ", sid='" + sid + '\'' +
                ", wbRoom='" + wbRoom + '\'' +
                ", wbid='" + wbid + '\'' +
                ", wbRoomToken='" + wbRoomToken + '\'' +
                ", name='" + name + '\'' +
                ", creator='" + creator + '\'' +
                ", pgCount=" + pgCount +
                ", curPg=" + curPg +
                ", createDt=" + createDt +
                ", updateDt=" + updateDt +
                '}';
    }
}
