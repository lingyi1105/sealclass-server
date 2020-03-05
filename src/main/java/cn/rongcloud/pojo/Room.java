package cn.rongcloud.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Created by weiqinxiao on 2019/2/28.
 */
@Entity
@Table(name = "t_room")
@DynamicInsert
@DynamicUpdate
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private @Getter @Setter String rid;
    private @Getter @Setter String sid;
    private @Getter @Setter String name;
    private @Getter @Setter String portrait;
    private @Getter @Setter String display;
    private @Getter @Setter int whiteboardNameIndex;
    private @Getter @Setter Date createDt;
    private @Getter @Setter Date updateDt;

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", rid='" + rid + '\'' +
                ", sid='" + sid + '\'' +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
                ", display='" + display + '\'' +
                ", whiteboardNameIndex=" + whiteboardNameIndex +
                ", createDt=" + createDt +
                ", updateDt=" + updateDt +
                '}';
    }
}
