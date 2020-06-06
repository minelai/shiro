package com.github.strawh.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置
 * @author: straw
 * @date: 2020/6/6 22:40
 */
@Slf4j
@Configuration
public class ShiroConfig {

    /**
     * 3、创建ShiroFilterFactoryBean
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier(value = "manager") DefaultWebSecurityManager manager){
        log.info("ShiroFilterFactoryBean in");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(manager);

        /**
         * 内置URL过滤器
         *  anon：无需认证
         *  authc：必须认证
         *  role：角色授权
         */
        Map<String, String> filterPath = new LinkedHashMap<>();
        filterPath.put("/admin/login","anon");
        filterPath.put("/**","authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterPath);

        // 设置跳转链接
        shiroFilterFactoryBean.setLoginUrl("/admin/login");
        // 未授权提示
        shiroFilterFactoryBean.setUnauthorizedUrl("/unAuthc");

        return shiroFilterFactoryBean;
    }

    /**
     * 2、创建DefaultWebSecurityManager
     * @return
     */
    @Bean(name = "manager")
    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier(value = "userRealm") UserRealm userRealm){
        log.info("DefaultWebSecurityManager in");

        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();

        // 关联Realm
        defaultWebSecurityManager.setRealm(userRealm);

        return defaultWebSecurityManager;
    }

    /**
     * 1、创建Realm
     * @return
     */
    @Bean(name = "userRealm")
    public UserRealm userRealm(){
        log.info("UserRealm in");
        return new UserRealm();
    }

}
