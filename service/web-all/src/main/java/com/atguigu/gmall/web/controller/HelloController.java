package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.constant.SysRedisConst;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

//    测试

    @GetMapping("/order/hhh")
    public String orderHaha(@RequestHeader(value=SysRedisConst.USERID_HEADER,
            defaultValue = "没有") String uid){


        return "ok:" +uid;
    }
}
