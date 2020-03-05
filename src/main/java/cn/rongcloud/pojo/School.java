package cn.rongcloud.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_school")
@DynamicInsert
@DynamicUpdate
public class School {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private @Getter @Setter String sid;
    private @Getter @Setter String name;
    private @Getter @Setter String portrait;
    private @Getter @Setter String manager;
    private @Getter @Setter String appkey;
    private @Getter @Setter String secret;
    private @Getter @Setter Date createDt;
    private @Getter @Setter Date updateDt;

    @Override
    public String toString() {
        return "School{" +
                "id=" + id +
                ", sid='" + sid + '\'' +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
                ", manager='" + manager + '\'' +
                ", appkey='" + appkey + '\'' +
                ", secret='" + secret + '\'' +
                ", createDt=" + createDt +
                ", updateDt=" + updateDt +
                '}';
    }
}
