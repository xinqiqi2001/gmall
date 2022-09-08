package com.atguigu.gmall.cart;

import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author Xiaoxin
 * @Date 2022/9/8 0:52
 * @Version 1.0
 */
@EnableAutoExceptionHandler
@SpringCloudApplication
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
public class CartMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartMainApplication.class,args);
    }
}
