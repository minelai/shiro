package com.github.strawh.config.jwt;

import org.apache.shiro.authc.HostAuthenticationToken;

/**
 * @author: straw
 * @date: 2020/6/7 13:53
 */
public class JWTToken implements HostAuthenticationToken {

    private String token;
    private String host;

    public JWTToken(String token) {
        this(token, null);
    }

    public JWTToken(String token, String host) {
        this.token = token;
        this.host = host;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public Object getPrincipal() {
        return this.token;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public String toString(){
        return token + ':' + host;
    }
}
