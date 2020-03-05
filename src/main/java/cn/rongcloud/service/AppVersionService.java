package cn.rongcloud.service;

import cn.rongcloud.common.ApiException;
import cn.rongcloud.pojo.AppVersionResult;

import java.util.List;

public interface AppVersionService {
    Boolean update(Integer platform, String url, String version) throws ApiException, Exception;
    Boolean delete(Integer platform) throws ApiException, Exception;
    List<AppVersionResult> getVersions(Integer platform) throws ApiException, Exception;
}
