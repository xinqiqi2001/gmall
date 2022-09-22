package com.atguigu.gmall.seckill;

import com.atguigu.gmall.annotation.EnableAppRabbit;
import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author Xiaoxin
 * @Date 2022/9/20 18:49
 * @Version 1.0
 */
@EnableAutoExceptionHandler//全局异常处理器
@FeignClient
@EnableScheduling
@MapperScan("com.atguigu.gmall.seckill.mapper")
@SpringCloudApplication
@EnableAppRabbit
public class SecKillMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecKillMainApplication.class,args);
    }
}
