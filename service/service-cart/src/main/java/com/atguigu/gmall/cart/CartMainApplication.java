package com.atguigu.gmall.cart;

import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author Xiaoxin
 * @Date 2022/9/8 0:52
 * @Version 1.0
 */
@EnableAutoFeignInterceptor
@EnableThreadPool
@EnableAutoExceptionHandler
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@SpringCloudApplication
public class CartMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartMainApplication.class,args);
    }
}
