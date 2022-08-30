package com.atguigu.gmall.item;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @Author Xiaoxin
 * @Date 2022/8/29 22:59
 * @Version 1.0
 */
@SpringBootTest
public class RedisTest {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void test1(){
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set("hello","word");
        System.out.println("保存完成");
        String va = stringValueOperations.get("hello");
        System.out.println("根据key获取的value = " + va);
    }
}
