package com.atguigu.gmall.common.utiles;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Xiaoxin
 * @Date 2022/9/8 19:24
 * @Version 1.0
 */
public class AuthUtils {

    /**
     * 获取用户id或临时用户id
     * @return
     */
    public static UserAuthInfo getCurrentAuthInfo() {
        //1、拿到老请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //2.再去请求头中获取用户id或临时用户id  设置给UserAuthInfo对象
        UserAuthInfo authInfo = new UserAuthInfo();

        String userId = request.getHeader(SysRedisConst.USERID_HEADER);
        //判断是否传来了用户id
        if (!StringUtils.isEmpty(userId)){
            authInfo.setUserId(Long.parseLong(userId));
        }

        String userTempId = request.getHeader(SysRedisConst.USERTEMPID_HEADER);
        authInfo.setUserTempId(userTempId);


        return authInfo;
    }
}
