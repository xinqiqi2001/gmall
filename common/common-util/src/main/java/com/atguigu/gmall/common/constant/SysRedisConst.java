package com.atguigu.gmall.common.constant;

public class SysRedisConst {

    //空值  缓存一个x
    public static final String NULL_VAL = "x";
    //锁名前缀key
    public static final String LOCK_SKU_DETAIL = "lock:sku:detail:";
    //空值就存入x时间是30分钟
    public static final Long NULL_VAL_TTL = 60*30L;
    //七天
    public static final Long SKUDETAIL_TTL = 60*60*24*7L;
    //存入redis的key
    public static final String SKU_INFO_PREFIX = "sku:info:";

    //布隆过滤器存id的
    public static final String BLOOM_SKUID = "bloom:skuid";

    public static final String LOCK_PREFIX = "lock:";
    public static final String CACHE_CATEGORYS = "categorys";
    public static final int SEARCH_PAGE_SIZE = 8;
    public static final String SKU_HOTSCORE_PREFIX = "sku:hotscore:"; //49
    public static final String LOGIN_USER = "user:login:"; //拼接token
    public static final String USERID_HEADER = "userid";
}
