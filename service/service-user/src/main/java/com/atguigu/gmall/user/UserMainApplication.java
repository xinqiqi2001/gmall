package com.atguigu.gmall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author Xiaoxin
 * @Date 2022/9/7 0:11
 * @Version 1.0
 */
@MapperScan("com.atguigu.gmall.user.mapper")
@SpringCloudApplication
public class UserMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserMainApplication.class,args);
    }
}

