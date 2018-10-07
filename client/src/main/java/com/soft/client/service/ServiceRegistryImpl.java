package com.soft.client.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.zookeeper.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
public class ServiceRegistryImpl implements ServiceRegistry,Watcher {

    private static CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;
    private static final int SESSION_TIMEOUT=5000;
    private static final String REGISTRY_PATH = "/registry";

    public ServiceRegistryImpl() {
    }

    public ServiceRegistryImpl(String zkServers) {
        try {
            zk = new ZooKeeper(zkServers,SESSION_TIMEOUT,this);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean register(String serviceName,String num, JSONObject obj) {
        try {
            String registryPath = REGISTRY_PATH;
            if (zk.exists(registryPath, false) == null) {
                zk.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //创建服务节点（持久节点）
            String servicePath = registryPath + "/" + serviceName;
            if (zk.exists(servicePath, false) == null) {
                zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String path = registryPath + "/" + serviceName+"/"+num;
            if (zk.exists(path, true) == null) {
                System.out.println(obj.toString());
                zk.create(path,  obj.toString().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public void process(WatchedEvent event) {
        // 连接状态
        Event.KeeperState keeperState = event.getState();
        // 事件类型
        Event.EventType eventType = event.getType();
        System.out.println(eventType);
        // 受影响的path
        String path = event.getPath();
        if (keeperState== Event.KeeperState.SyncConnected)
            System.out.println("连接zk ok");
            latch.countDown();
        if (Event.EventType.NodeDataChanged == eventType) {
            System.out.println("节点数据更新。。。。");
            SocketServerPoint point = new SocketServerPoint("196.168.1.111",8890);
            point.clientToServer("kill -9 `pidof mongod`");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public JSONObject getChildrenValue(String path){

        String data = null;
        JSONObject obj = null;
        try {
            List<String> children = zk.getChildren(path, true);
            obj = new JSONObject();
            for (String child: children
                 ) {
                data = new String(zk.getData(path+"/"+child,this,null));
                obj.put(path+"/"+child,data);
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public JSONObject getValue(String path){
        String data = null;
        try {
            data = new String(zk.getData(path,true,null));
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return JSONObject.fromObject(data);

    }
    public boolean updateData(String service,String num, String key, String value){
        String path = REGISTRY_PATH+"/"+service+"/"+num;
        try {
            if(zk.exists(path,true)!=null){
                JSONObject obj = getValue(path);
                if(obj.has(key)){
                    if(!obj.get(key).equals(value)){
                        obj.put(key,value);
                        zk.setData(path,obj.toString().getBytes(),-1);
                    }
                }

            }
        } catch (KeeperException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean updateData(String service, String key, String value){
        String path = REGISTRY_PATH+"/"+service;
        try {
            if(zk.exists(path,true)!=null){
                JSONObject obj = getChildrenValue(path);
                for (Object o: obj.entrySet()) {
                    Map.Entry<String,Map> ma= (Map.Entry<String,Map>) o;
                    System.out.println(ma.getValue().toString());
                    if(ma.getValue().containsKey(key)){
                        if(!ma.getValue().get(key).equals(value)){
                            ma.getValue().put(key,value);
                            JSONObject json=JSONObject.fromObject(ma.getValue().toString());
                            zk.setData(ma.getKey(),json.toString().getBytes(),-1);
                        }
                    }
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
