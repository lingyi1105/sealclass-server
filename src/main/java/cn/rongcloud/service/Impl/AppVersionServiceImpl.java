package cn.rongcloud.service.Impl;

import cn.rongcloud.common.ApiException;
import cn.rongcloud.dao.AppVersionDao;
import cn.rongcloud.permission.DeclarePermissions;
import cn.rongcloud.pojo.AppVersion;
import cn.rongcloud.pojo.AppVersionResult;
import cn.rongcloud.pojo.RoleEnum;
import cn.rongcloud.service.AppVersionService;
import cn.rongcloud.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AppVersionServiceImpl implements AppVersionService {
    @Autowired
    private AppVersionDao appVersionDao;

    @DeclarePermissions(RoleEnum.RoleSuperAdmin)
    @Override
    public Boolean update(Integer platform, String url, String version) throws ApiException, Exception {
        List<AppVersion> versions = appVersionDao.findByPlatform(platform);
        AppVersion v = versions.isEmpty()?new AppVersion():versions.get(0);
        v.setPlatform(platform);
        if (url != null) {
            v.setUrl(url);
        }
        if (version != null) {
            v.setVersion(version);
        }
        v.setUpdateDt(DateTimeUtils.currentUTC());
        appVersionDao.save(v);
        return Boolean.TRUE;
    }

    @DeclarePermissions(RoleEnum.RoleSuperAdmin)
    @Override
    public Boolean delete(Integer platform) throws ApiException, Exception {
        return appVersionDao.deleteByPlatform(platform) > 0;
    }

    @Override
    public List<AppVersionResult> getVersions(Integer platform) throws ApiException, Exception {
        List<AppVersionResult> results = new ArrayList<>();
        List<AppVersion> versions;
        if (platform == null) {
            versions = appVersionDao.findAll();
        } else {
            versions = appVersionDao.findByPlatform(platform);
        }
        for (AppVersion v: versions) {
            results.add(AppVersionResult.generate(v));
        }
        return results;
    }
}
