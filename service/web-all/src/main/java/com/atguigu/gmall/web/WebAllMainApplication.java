package com.atguigu.gmall.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 *
 * @EnableFeignClients开启远程调用功能
 * @Author Xiaoxin
 * @Date 2022/8/26 9:11
 * @Version 1.0
 */

@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign"
})
@SpringCloudApplication
public class WebAllMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllMainApplication.class,args);
    }
}
