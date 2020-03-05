package cn.rongcloud.dao;

import cn.rongcloud.pojo.Room;
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
public interface RoomDao extends JpaRepository<Room, Long> {
    List<Room> findByRidAndSid(String rid, String sid);

    @Transactional
    @Modifying
    int deleteByRidAndSid(String rid, String sid);

    boolean existsByRidAndSid(String rid, String sid);

    @Transactional
    @Modifying
    @Query(value = "update t_room set display=?3 where rid=?1 and sid=?2", nativeQuery = true)
    int updateDisplayByRidAndSid(String rid, String sid, String display);

    @Transactional
    @Modifying
    @Query(value = "update t_room set whiteboard_name_index=?3 where rid=?1 and sid=?2", nativeQuery = true)
    int updateWhiteboardNameIndexByRidAndSid(String rid, String sid, int whiteboardNameIndex);
}