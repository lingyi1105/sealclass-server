package cn.rongcloud.dao;

import cn.rongcloud.pojo.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SchoolDao extends JpaRepository<School, Long> {
    List<School> findBySid(String sid);
    List<School> findByManager(String manager);
    List<School> findByAppkey(String appkey);

    @Transactional
    @Modifying
    int deleteBySid(String sid);
}