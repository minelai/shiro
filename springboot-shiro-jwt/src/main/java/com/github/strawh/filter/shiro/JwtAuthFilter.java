package com.github.strawh.filter.shiro;

import com.github.strawh.config.jwt.JWTToken;
import com.github.strawh.config.jwt.JWTUtil;
import com.github.strawh.entity.AdminEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 自定义认证拦截器
 * @author: straw
 * @date: 2020/6/7 13:24
 */
@Slf4j
public class JwtAuthFilter extends AuthenticatingFilter {

    private static final int tokenRefreshInterval = 300;//TOKEN过期时间
    private static final int tokenOverTimeInterval = 7200;//TOKEN超时时间


    /**
     * 1、使用自定义的token，提交给shiro
     * 返回null抛出异常，进入isAccessAllowed（）的异常处理逻辑
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // 1、获取请求头中的token
        HttpServletRequest request = WebUtils.toHttp(servletRequest);
        String header = request.getHeader("x-auth-token");
        String token = header.replace("Bearer ", "");

        // 2、不为空、未过期
        if(StringUtils.isNoneBlank(token) && !JWTUtil.expireToken(token)){
            return new JWTToken(token);
        }
        return null;
    }

    /**
     * 2、拦截所有请求
     * 在请求进入拦截器后调用该方法，返回true则继续，
     * 返回false则会调用onAccessDenied()。
     * 这里在不通过时，还调用了isPermissive()方法
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        boolean loginRequest = this.isLoginRequest(request, response);
        if(loginRequest){
            return true;
        }
        boolean allowed = false;
        try {
            allowed = executeLogin(request, response);
        } catch(IllegalStateException e){ //not found any token
            log.error("Not found any token");
        }catch (Exception e) {
            log.error("Error occurs when login", e);
        }
        return allowed || super.isPermissive(mappedValue);
    }

    /**
     * 3、isAccessAllowed()返回false时进入，返回错误响应
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse response = WebUtils.toHttp(servletResponse);
        HttpServletRequest request = WebUtils.toHttp(servletRequest);

        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,HEAD,PUT,DELETE");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        response.sendError(HttpStatus.OK.value(), "登录信息异常，请重新登录");
        return false;
    }

    /**
     * 4.1、Shiro login认证成功时执行（用户名密码登录成功）是否需要刷新token
     * @param token
     * @param subject
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);

        if(token instanceof JWTToken){
            // 保存刷新后的token
            String newToken = "";

            // 1、获取登录信息
            JWTToken jwtToken = (JWTToken) token;
            AdminEntity adminEntity = (AdminEntity)subject.getPrincipal();
            // 2、判断是否需要刷新token
            String adminTokne = jwtToken.getToken();
            // 2.1、获取token签发时间
            Date issuedAt = JWTUtil.getIssuedAt(adminTokne);
            boolean flag = this.refreshToken(issuedAt);
            if(flag){
                // 生成新的token
                newToken = JWTUtil.sign("1234","123",3600);
            }else{
                newToken = adminTokne;
            }

            // 3、设置新的token
            if(StringUtils.isNotBlank(newToken) && !this.isOverTime(issuedAt)){
                httpResponse.setHeader("x-auth-token", newToken);
            }else{
                return false;
            }
        }
        return true;
    }

    /**
     * 4.2、Shiro login认证失败时执行，具体逻辑查看onAccessDenied()
     * @param token
     * @param e
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        log.error("Validate token fail, token:{}, error:{}", token.toString(), e.getMessage());
        return false;
    }



    /**
     * 是否刷新token
     * @param issueAt 签发时间
     * @return true 刷新
     */
    private boolean refreshToken(Date issueAt){
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().minusSeconds(tokenRefreshInterval).isAfter(issueTime);
    }

    /**
     * 判断token是否过时
     * @param issueAt
     * @return
     */
    protected boolean isOverTime(Date issueAt){
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().minusSeconds(tokenOverTimeInterval).isAfter(issueTime);
    }
}
