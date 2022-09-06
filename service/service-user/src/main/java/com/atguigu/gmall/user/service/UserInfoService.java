package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Xiaoxin
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2022-09-07 00:22:57
*/
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 登录
     * @param userInfo
     * @return
     */
    LoginSuccessVo login(UserInfo userInfo);

    /**
     * 退出登录(删除登录信息)
     * @param token
     */
    void logout(String token);
}
