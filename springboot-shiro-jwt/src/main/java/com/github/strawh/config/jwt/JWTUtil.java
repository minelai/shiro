package com.github.strawh.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;

import java.util.Calendar;
import java.util.Date;

/**
 * JSON Web Token工具
 * @author: straw
 * @date: 2020/5/30 12:43
 */
public class JWTUtil {

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的签发时间
     */
    public static Date getIssuedAt(String token) {
        try {
            return JWT.decode(token).getIssuedAt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("uuid").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名,expireTime后过期
     * @param userid 用户名
     * @param time 过期时间
     * @return 加密的token
     */
    public static String sign(String userid, String salt, long time) {
        return JWT.create()
                .withClaim("uuid", userid) // 存储数据
                .withExpiresAt(new Date(System.currentTimeMillis()+time*1000)) // 设置过期时间
                .withIssuedAt(new Date()) // 生成签名时间
                .sign(Algorithm.HMAC256(salt));
    }

    /**
     * token是否过期
     * @return true：过期
     */
    public static boolean expireToken(String token) {
        Date now = Calendar.getInstance().getTime();
        return JWT.decode(token).getExpiresAt().before(now);
    }

    /**
     * 生成随机盐,长度32位
     * @return
     */
    public static String generateSalt(){
        SecureRandomNumberGenerator secureRandom = new SecureRandomNumberGenerator();
        return secureRandom.nextBytes(16).toHex();
    }
}
