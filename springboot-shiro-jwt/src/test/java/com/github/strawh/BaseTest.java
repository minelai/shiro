package com.github.strawh;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 基础测试类
 * @author: straw
 * @date: 2020/5/27 1:20
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MyApplication.class)//启动主类
public class BaseTest {

    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Before
    public void init() {
        this.setTime(System.currentTimeMillis());
        log.info("测试开始：");
    }

    @After
    public void after() {
        log.info("测试结束：程序运行时间，{}", System.currentTimeMillis() - this.getTime());
    }
}
