package com.atguigu.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author Xiaoxin
 * @Date 2022/9/7 0:27
 * @Version 1.0
 */
@Controller
public class LoginController {

    /**
     * 跳转到登录页
     * @return
     */
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("originUrl") String originUrl,Model model){

        //登录成功还要重新跳转回去
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
