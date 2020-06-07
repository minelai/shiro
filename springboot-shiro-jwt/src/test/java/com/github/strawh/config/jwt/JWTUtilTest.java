package com.github.strawh.config.jwt;

import com.github.strawh.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

/**
 * @author: straw
 * @date: 2020/6/7 9:21
 */
@Slf4j
public class JWTUtilTest extends BaseTest {

    /**
     * 生成Token测试
     */
    @Test
    public void TestCreateToken() throws InterruptedException {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String salt = JWTUtil.generateSalt();
        String token = JWTUtil.sign(uuid, salt, 120);
        log.info("UUID: {} SALT：{} TOKEN：{}", uuid, salt, token);
        // 签发时间
        Date issuedAt = JWTUtil.getIssuedAt(token);
        // uuid
        String userId = JWTUtil.getUserId(token);
        Thread.sleep(1000);
        // 是否过期
        boolean timeState = JWTUtil.expireToken(token);
        log.info("TOKEN：{} issuedAt：{} userId：{} 过期：{}", token, issuedAt, userId, timeState);
    }

    /**
     * Token是否过期测试
     */
    @Test
    public void TestExpireToken() throws InterruptedException {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1OTE0OTMzNTIsInV1aWQiOiJiMGFmYTBiMTkyZTg0NzBiOGVkOTI0ZjVjMjk2ZWIyZiIsImlhdCI6MTU5MTQ5MzIzMn0.9i4tBUhtYe8mKfFH1G4RycfOJE7gaAV2CF8A-PRDIu0";
        // 签发时间
        Date issuedAt = JWTUtil.getIssuedAt(token);
        boolean state = false;
        do {
            state = JWTUtil.expireToken(token);
            log.info("TOKEN：{} 是否过期：{} issuedAt：{}", token, state, issuedAt);
            Thread.sleep(2000);
        }while (!state);
        log.info("->TOKEN：{} 是否过期：{}", token, state);
    }

}