package com.atguigu.gmall.item;

import io.lettuce.core.RedisClient;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author Xiaoxin
 * @Date 2022/8/31 16:26
 * @Version 1.0
 */
@SpringBootTest
public class RedissonTest {
    @Autowired
    RedissonClient redissonClient;

    @Test
    public void test1(){
        System.out.println("redissonClient = " + redissonClient);
    }
}
