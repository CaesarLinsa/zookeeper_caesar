package com.soft.client.service;

import net.sf.json.JSONObject;

public interface ServiceRegistry {
    /**
     * 注册服务信息
     *
     * @param serviceName    服务名称
     * @param obj   服务配置
     */
    boolean register(String serviceName, String num, JSONObject obj);
    JSONObject getChildrenValue(String path);
    JSONObject getValue(String path);
    boolean updateData(String service, String num, String key, String value);
    boolean updateData(String service, String key, String value);
}
