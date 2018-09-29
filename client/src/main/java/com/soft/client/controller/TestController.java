package com.soft.client.controller;
import core.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController

public class TestController {

    @Autowired
    public ServiceRegistry serviceRegistry;

    @RequestMapping(name="HelloService",method = RequestMethod.GET,path = "/hello")
    public String hello(){
        return "Hello";
    }

    @RequestMapping(value="/world", method=RequestMethod.GET)
    public String world(){
      ArrayList<String> arrays = serviceRegistry.getValue("/registry/HelloService");
      return arrays.toString();
    }
}
