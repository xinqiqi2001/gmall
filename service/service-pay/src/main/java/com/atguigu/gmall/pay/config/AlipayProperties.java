package com.atguigu.gmall.pay.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.alipay")
public class AlipayProperties {
    //网关地址
    private String gatewayUrl;
    //应用id
    private String appId;
    //商户私钥
    private String merchantPrivateKey;
    //字符编码
    private String charset;
    //阿里公钥
    private String alipayPublicKey;
    //签名方式(类型)
    private String signType;
    //同步通知地址
    private String returnUrl;
    //回调地址
    private String notifyUrl;

}
