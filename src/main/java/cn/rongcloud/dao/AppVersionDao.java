package cn.rongcloud.dao;

import cn.rongcloud.pojo.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AppVersionDao extends JpaRepository<AppVersion, Long> {
    List<AppVersion> findByPlatform(int platform);

    @Transactional
    @Modifying
    int deleteByPlatform(int platform);
}