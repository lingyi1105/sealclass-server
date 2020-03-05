package cn.rongcloud.dao;

import cn.rongcloud.pojo.Whiteboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by weiqinxiao on 2019/2/25.
 */
@Repository
public interface WhiteboardDao extends JpaRepository<Whiteboard, Long> {
    List<Whiteboard> findByRidAndSid(String rid, String sid);
    List<Whiteboard> findByRidAndSidAndCreator(String rid, String sid, String creator);
    List<Whiteboard> findByRidAndSidAndWbid(String rid, String sid, String wbid);

    int deleteByRidAndSid(String rid, String sid);

    @Transactional
    @Modifying
    int deleteByWbid(String wbid);

    @Transactional
    @Modifying
    @Query(value = "delete from t_whiteboard where rid=?1 and sid=?2 and creator=?3", nativeQuery = true)
    int deleteByRidAndSidAndCreator(String rid, String sid, String creator);

    @Transactional
    @Modifying
    @Query(value = "update t_whiteboard set cur_pg=?4 where wbid=?3 and rid=?1 and sid=?2", nativeQuery = true)
    int updatePageByRidAndWbid(String rid, String sid, String wbid, int page);
}