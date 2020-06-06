package com.github.strawh.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

/**
 * 自定义Realm
 * @author: straw
 * @date: 2020/6/6 22:44
 */
@Slf4j
public class UserRealm extends AuthorizingRealm {

    /**
     * 执行授权逻辑
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("AuthorizationInfo in 执行授权逻辑");
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // 添加资源授权字符串
        simpleAuthorizationInfo.addStringPermission("user:add");

        // 获取admin对象
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();

        // 进行添加授权字符串

        return simpleAuthorizationInfo;
    }

    /**
     * 执行认证逻辑-登录
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("AuthenticationInfo in 执行认证逻辑");
        String username = "admin";
        String password = "123456";
        // 处理用户登录
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        // 1、判断用户名
        if(!username.equals(usernamePasswordToken.getUsername())){
            throw new UnknownAccountException();
        }

        // 2、判断密码 - 放入admin登录数据
        return new SimpleAuthenticationInfo(username, password, "");
    }
}
