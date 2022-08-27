package com.atguigu.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author Xiaoxin
 * @Date 2022/8/26 14:38
 * @Version 1.0
 */
@EnableFeignClients
@SpringCloudApplication
public class ServiceItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceItemApplication.class,args);
    }
}