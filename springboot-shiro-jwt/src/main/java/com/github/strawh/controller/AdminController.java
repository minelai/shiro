package com.github.strawh.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 * @author: straw
 * @date: 2020/6/6 23:06
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/list")
    public String list(){
        return "admin-list";
    }

    @GetMapping("/delete")
    public String delete(){
        return "admin-delete";
    }

    @GetMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password){
        // 1、获取Subject
        Subject subject = SecurityUtils.getSubject();
        // 2、封装用户数据
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        try {
            // 4、执行登录
            subject.login(usernamePasswordToken);
        }catch (UnknownAccountException e){
            log.info("用户不存在");
            return "用户不存在";
        }catch (IncorrectCredentialsException e){
            log.info("密码错误");
            return "密码错误";
        }
        return "admin-home";
    }

    @GetMapping("/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return HttpStatus.OK.toString();
    }
}
