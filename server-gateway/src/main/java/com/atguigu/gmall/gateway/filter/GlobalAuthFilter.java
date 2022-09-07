package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.gateway.properties.AuthUrlProperties;
import com.atguigu.gmall.model.user.UserInfo;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * @Author Xiaoxin
 * @Date 2022/9/7 18:21
 * @Version 1.0
 */
@Component
public class GlobalAuthFilter implements GlobalFilter {
    //路径匹配器 有match方法判断某个路径是否匹配某个规则
    AntPathMatcher matcher = new AntPathMatcher();
    @Autowired
    AuthUrlProperties urlProperties;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        //1.前置拦截
        String path = exchange.getRequest().getURI().getPath();
        String uri = exchange.getRequest().getURI().toString();


        //2.静态资源 直接放行
        //判断路径是否包含静态路径   静态资源带了token 不用校验token 故放行
        for (String url : urlProperties.getNoAuthUrl()) {
            //match方法判断某个路径是否匹配某个规则
            boolean match = matcher.match(url, path);
            if (match) {
                //放行
                return chain.filter(exchange);
            }

        }

        //走到这儿，说明不是直接放行的资源(需要登录)

        //3.只要是 /api/inner/的全部直接拒绝 因为这些路径需要登录才能访问
        for (String url : urlProperties.getDenyUrl()) {
            boolean match = matcher.match(url, path);
            if (match) {
                //包含该路径
                Result<String> result = Result.build("",
                        ResultCodeEnum.PERMISSION);
                return responseResult(result, exchange);
            }
        }


        //4.需要登录的请求 进行权限验证  对登录后的请求进行user_id透传
        for (String url : urlProperties.getLoginAuthUrl()) {
            boolean match = matcher.match(url, path);
            //判断是否拿到了令牌(token)
            if(match){
                //登录校验
                //4.1获取token的信息【Cookie[token=xxx]】【Header[token=xxx]】
                String tokenValue = getTokenValue(exchange);
                //4.2校验token
                UserInfo info = getTokenUserInfo(tokenValue);
                //4.3判断用户信息是否正常
                if (info!=null){
                    //说明在redis中查到了指定用户 exchange的request头中新增一个userid

                    ServerWebExchange webExchange = userIdTransport(info, exchange);
                    return chain.filter(webExchange);

                }else {
                    //redis中无此用户 [假令牌 token没有  没登录 ]
                    //重定向到自定义页面  此次响应加载一个状态码 响应结束
                    return redirectToCustomPage(urlProperties.getLoginPage()+"?originUrl="+uri,exchange);
                }


            }
        }

        //走到这说明是一个普通请求 放行 既不是静态资源直接放行 也不是必须要登录的
        String tokenValue = getTokenValue(exchange);
        UserInfo tokenUserInfo = getTokenUserInfo(tokenValue);
        if (tokenUserInfo!=null){
            exchange=userIdTransport(tokenUserInfo, exchange);
        }else {
            //如果前端带了token，还是没用户信息，代表这是假令牌
            if(!StringUtils.isEmpty(tokenValue)){
                //重定向到登录. 可以不带token,要带就得带正确
                return redirectToCustomPage(urlProperties.getLoginPage()+"?originUrl="+uri,exchange);
            }
        }

        return chain.filter(exchange);
    }

    /**
     * 重定向到自定义页面
     * @param
     * @param exchange
     * @return
     */
    private Mono<Void> redirectToCustomPage(String location,
                                            ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();

        //1、重定向 设置响应状态码302【302状态码 + 响应头中 Location: 新位置】
        response.setStatusCode(HttpStatus.FOUND);
        // http://passport.gmall.com/login.html?originUrl=http://gmall.com/
        response.getHeaders().add(HttpHeaders.LOCATION,location);

        //2、清除旧的错误的Cookie[token]（同名cookie并max-age=0）解决无限重定向问题
        //更新token的声明周期 maxAge(0) 立即死亡  .domain(".gmall.com") 指定作用域的token
        ResponseCookie tokenCookie = ResponseCookie
                .from("token", "123")
                .maxAge(0)
                .path("/")
                .domain(".gmall.com")
                .build();
        response.getCookies().set("token",tokenCookie);

        //3、响应结束
        return response.setComplete();
    }

    /**
     * request的头会新增一个userid
     * 用户id透传
     * @param info
     * @param exchange
     * @return
     */
    private ServerWebExchange userIdTransport(UserInfo info, ServerWebExchange exchange) {
        if(info != null){
            //请求一旦发来，所有的请求数据是固定的，不能进行任何修改，只能读取
            ServerHttpRequest request = exchange.getRequest();

            //根据原来的请求，封装一个新情求
            ServerHttpRequest newReq = exchange.getRequest()
                    .mutate() //变一个新的
                    .header(SysRedisConst.USERID_HEADER, info.getId().toString())
                    .build();//添加自己的头


            //放行的时候传改掉的exchange
            ServerWebExchange webExchange = exchange
                    .mutate()
                    .request(newReq)
                    .response(exchange.getResponse())
                    .build();

            return webExchange;
        }
        return exchange;
    }

    /**
     * 校验token 去redis中查询是否有这个用户信息
     *
     * @param tokenValue
     * @return
     */
    private UserInfo getTokenUserInfo(String tokenValue) {
        String json = redisTemplate.opsForValue().get(SysRedisConst.LOGIN_USER + tokenValue);
        if(!StringUtils.isEmpty(json)){
            return Jsons.toObj(json,UserInfo.class);
        }
        return null;
    }

    /**
     * 获取token  token可能存在cookie中 或者在header
     *
     * @param exchange
     * @return
     */
    private String getTokenValue(ServerWebExchange exchange) {
        //前端可能将token存在了cookie中 也可能存在了header中
        String tokenValue = "";
        //检查Cookie中有无token
        HttpCookie token = exchange.getRequest()
                .getCookies()
                .getFirst("token");
        if(token != null){
            tokenValue = token.getValue();
            return tokenValue;
        }

        //检查header中有无token
        tokenValue = exchange.getRequest().getHeaders().getFirst("token");

        return tokenValue;
    }

    /**
     * 响应一个结果
     *
     * @param result
     * @param exchange
     * @return
     */
    private Mono<Void> responseResult(Result<String> result, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        String jsonStr = Jsons.toStr(result);

        //DataBuffer
        DataBuffer dataBuffer = response.bufferFactory()
                .wrap(jsonStr.getBytes());

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.just(dataBuffer));
    }


}
