package com.xwl.learn.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xiewanlin
 * @Date: 2019/4/24
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        return "xwl";
    }
}
