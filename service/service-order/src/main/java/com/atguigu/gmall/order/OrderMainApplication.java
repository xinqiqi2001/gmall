package com.atguigu.gmall.order;

import com.atguigu.gmall.annotation.EnableAppRabbit;
import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAppRabbit //自定义注解 自定义了mq操作类
@EnableTransactionManagement//事务管理器
@EnableAutoExceptionHandler
@EnableAutoFeignInterceptor //开启feign 用户id透传拦截器
@EnableFeignClients({
        "com.atguigu.gmall.feign.cart",
        "com.atguigu.gmall.feign.user",
        "com.atguigu.gmall.feign.product",
        "com.atguigu.gmall.feign.ware"
})
@MapperScan("com.atguigu.gmall.order.mapper")
@SpringCloudApplication
public class OrderMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class,args);
    }
}
