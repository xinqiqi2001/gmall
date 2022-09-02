package com.atguigu.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * 缓存注解
 * @Author Xiaoxin
 * @Date 2022/9/1 20:34
 * @Version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GmallCache {

    String cacheKey() default "";//代表cacheKey(要存入redis缓存数据库的key)

    String bloomName() default "";//如果指定了布隆过滤器的名字，就用

    String bloomValue() default "";//指定布隆过滤器如果需要判定的话，用什么表达式计算出的值进行判定

    String lockName() default ""; //传入精确锁就用精确的，否则用全局默认的
}
