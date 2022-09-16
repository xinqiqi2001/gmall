package com.atguigu.gmall.pay;

import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author Xiaoxin
 * @Date 2022/9/16 21:14
 * @Version 1.0
 */
@EnableAutoExceptionHandler
@SpringCloudApplication
@EnableAutoFeignInterceptor//id透传
@EnableFeignClients({
        "com.atguigu.gmall.feign.order"
})
public class PayMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayMainApplication.class,args);
    }
}
