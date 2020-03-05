package cn.rongcloud.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_appversion")
@DynamicInsert
@DynamicUpdate
public class AppVersion {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private @Getter @Setter int platform;
    private @Getter @Setter String url;
    private @Getter @Setter String version;
    private @Getter @Setter Date updateDt;

    @Override
    public String toString() {
        return "AppVersion{" +
                "id=" + id +
                ", platform=" + platform +
                ", url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", updateDt=" + updateDt +
                '}';
    }
}
