package cn.rongcloud.dao;

import cn.rongcloud.pojo.RoomMember;
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
public interface RoomMemberDao extends JpaRepository<RoomMember, Long> {
    List<RoomMember> findByRidAndSid(String rid, String sid);
    List<RoomMember> findByRidAndSidAndUid(String rid, String sid, String uid);
    List<RoomMember> findByUid(String uid);
    List<RoomMember> findByRidAndSidAndRole(String rid, String sid, int role);
    boolean existsByRidAndSidAndUid(String rid, String sid, String uid);

    @Modifying
    @Transactional
    public int deleteByRidAndSid(String rid, String sid);

    @Query(value = "select count(*) from t_room_member where rid=?1 and sid=?2", nativeQuery = true)
    int countByRidAndSid(String rid, String sid);

    @Transactional
    @Modifying
    @Query(value = "delete from t_room_member where rid=?1 and sid=?2 and uid=?3", nativeQuery = true)
    int deleteUserByRidAndSidAndUid(String rid, String sid, String uid);

    @Transactional
    @Modifying
    @Query(value = "update t_room_member set camera=?4 where rid=?1 and sid=?2 and uid=?3", nativeQuery = true)
    int updateCameraByRidAndSidAndUid(String rid, String sid, String uid, boolean camera);

    @Transactional
    @Modifying
    @Query(value = "update t_room_member set mic=?4 where rid=?1 and sid=?2 and uid=?3", nativeQuery = true)
    int updateMicByRidAndSidAndUid(String rid, String sid, String uid, boolean mic);
}