package com.myse1f.myrpc.registry;

import java.util.List;

/**
 * Created By Yufan Wu
 * 2021/7/18
 */

public interface ServiceRegistry {
    void register(String serviceName, String serviceAddress);
    List<String> getService(String serviceName);
}
