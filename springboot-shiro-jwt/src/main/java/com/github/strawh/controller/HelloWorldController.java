package com.github.strawh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: straw
 * @date: 2020/6/6 22:15
 */
@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public String hello(){
        return "OK";
    }

    @GetMapping("/unAuthc")
    public String unAuthc(){
        return "你未经授权";
    }


}
