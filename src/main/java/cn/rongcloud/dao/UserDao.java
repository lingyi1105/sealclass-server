package cn.rongcloud.dao;

import cn.rongcloud.pojo.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by weiqinxiao on 2019/2/25.
 */
@Repository
public interface UserDao extends JpaRepository<UserInfo, Long> {
    List<UserInfo> findByUid(String uid);
    List<UserInfo> findBySid(String sid);
    List<UserInfo> findByRole(int role);
    List<UserInfo> findBySidAndPhone(String sid, String phone);
    List<UserInfo> findByUidAndSid(String uid, String sid);
    List<UserInfo> findBySidAndRole(String uid, int role);

    @Transactional
    @Modifying
    int deleteByUidAndRoleGreaterThan(String uid, String sid);

    @Transactional
    @Modifying
    int deleteByUidAndSidAndRoleGreaterThan(String uid, String sid, int role);

    @Transactional
    @Modifying
    int deleteByUid(String uid);
}