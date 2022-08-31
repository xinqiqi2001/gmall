package com.atguigu.gmall.item;

import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * @Author Xiaoxin
 * @Date 2022/8/26 14:38
 * @Version 1.0
 */

@Import(RedissonAutoConfiguration.class)
@EnableThreadPool //
@SpringCloudApplication
@EnableFeignClients
public class ServiceItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceItemApplication.class,args);
    }
}
