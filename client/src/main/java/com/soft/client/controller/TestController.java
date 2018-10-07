package com.soft.client.controller;
import com.soft.client.service.ServiceRegistry;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController

public class TestController {

    @Autowired
    public ServiceRegistry serviceRegistry;

    @RequestMapping(name="web",method = RequestMethod.GET,path = "/hello")
    public String hello(){
        return "hello";
    }

    @RequestMapping(value="/mongo", method=RequestMethod.GET)
    public JSONObject world(){
      JSONObject obj = serviceRegistry.getChildrenValue("/registry/mongodb");
      return obj;
    }
    @RequestMapping(value="/regist/{serviceName}/{num}",method = RequestMethod.POST)
    public boolean ServiceRegistry(@PathVariable  String serviceName, @PathVariable  String num,@RequestBody Map<String,String> req) {
        return serviceRegistry.register(serviceName,num,JSONObject.fromObject(req));
    }

    @RequestMapping(value = "/update/{service}/{num}/{key}={value}",method = RequestMethod.GET)
    public boolean ServiceUpdate(@PathVariable String service,@PathVariable String num,@PathVariable String key,@PathVariable String value){
        boolean flag = serviceRegistry.updateData(service,num,key,value);
        return flag;
    }
    @RequestMapping(value = "/update/{service}/{key}={value}",method = RequestMethod.GET)
    public boolean ServiceUpdate(@PathVariable String service,@PathVariable String key,@PathVariable String value){
        boolean flag = serviceRegistry.updateData(service,key,value);
        return flag;
    }
}
