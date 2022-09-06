package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author Xiaoxin
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2022-09-07 00:22:57
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{
    @Resource
    UserInfoMapper userInfoMapper;


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginSuccessVo login(UserInfo userInfo) {
        LoginSuccessVo vo = new LoginSuccessVo();

        //查询数据库中这个账号密码是否存在
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInfo::getName,userInfo.getLoginName())
                .eq(UserInfo::getPasswd, MD5.encrypt(userInfo.getPasswd()));

        UserInfo userInfo1 = userInfoMapper.selectOne(lambdaQueryWrapper);

        if (userInfo1 !=null ){
            //不等于空说明数据库有这个数据 登录成功
            //生成令牌
            String token = UUID.randomUUID().toString().replace("-", "");

            //将登录成功的数据存入redis  保存7天
            stringRedisTemplate.opsForValue().set(SysRedisConst.LOGIN_USER+token,
                    Jsons.toStr(userInfo1),7, TimeUnit.DAYS);

            //将令牌和登录成功的名字返回给前端
            vo.setToken(token);
            vo.setNickName(userInfo1.getNickName());
            return vo;

        }
        return null;
    }

    @Override
    public void logout(String token) {
        stringRedisTemplate.delete(token);
    }
}




