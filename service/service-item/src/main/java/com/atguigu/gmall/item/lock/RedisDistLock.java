package com.atguigu.gmall.item.lock;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author Xiaoxin
 * @Date 2022/8/30 11:54
 * @Version 1.0
 */
@Service
public class RedisDistLock {
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 加锁
     */
    public String lock(){
        String token = UUID.randomUUID().toString();

        while (!redisTemplate.opsForValue().setIfAbsent("lock",token,10, TimeUnit.SECONDS)){
            //自旋加锁
        }

        //加锁成功
        return token;
    }
    /**
     * 解锁
     */
    public void unlock(String token){
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1]  then return redis.call('del',KEYS[1]); else  return 0;end;";
        //删除指定的锁(用传来的token来判断是不是自己的锁)
        redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class), Arrays.asList("lock"), token);


    }
}
