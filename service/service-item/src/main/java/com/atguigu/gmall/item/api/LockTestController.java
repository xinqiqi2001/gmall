package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.lock.RedisDistLock;
import jdk.nashorn.internal.parser.Token;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Xiaoxin
 * @Date 2022/8/31 1:02
 * @Version 1.0
 */

@RequestMapping("/lock")
@RestController
public class LockTestController {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    ReentrantLock lock=new ReentrantLock();

    /**
     *     分布式锁
     */
    @Autowired
    RedisDistLock redisDistLock;

    /**
     * Redisson
     */
    @Autowired
    RedissonClient redissonClient;

    /**
     * redisson普通锁
     * 加锁和解锁需要是原子的(redisson底层是lua脚本 都是原子的)
     * 默认过期时间是30s
     * 有自动续期机制 如果业务时间超长  会自动续锁到30s 如果指定了锁的过期时间 那么看门狗机制就会失效不会自动续期
     * @return
     */
    @GetMapping("/common")
    public Result redissonLock() throws InterruptedException {
        //名字相同就代表是一把锁
        RLock lock = redissonClient.getLock("lock-hello");//获取一个普通的锁 可重入锁
        //1加锁
        lock.lock();


        System.out.println(" 得到锁 ");
        Thread.sleep(2000);
        System.out.println(" 执行结束 ");
        //2解锁
        lock.unlock();

        return Result.ok();
    }

//    //本地锁
//    @GetMapping("/incr")
//    public Result increment(){
//        lock.lock();
//    return Result.ok();
//    }

    //分布式锁
    @GetMapping("/incr")
    public Result increment(){
        //加分布式锁
        String token = redisDistLock.lock();

        String a = stringRedisTemplate.opsForValue().get("a");
        int i = Integer.parseInt(a);

        i++;

        stringRedisTemplate.opsForValue().set("a",a+"");

        //解锁 解锁时需要指定锁的token
        redisDistLock.unlock(token);

        return Result.ok();
    }
}
