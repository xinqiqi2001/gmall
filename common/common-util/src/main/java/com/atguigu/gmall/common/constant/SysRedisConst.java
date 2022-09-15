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
    public static final String USERTEMPID_HEADER = "usertempid";
    public static final String CART_KEY = "cart:user:"; //后面拼上你决定使用的key(用户id或临时用户id)
    //购物车中商品条目总数限制
    public static final long CART_ITEMS_LIMIT = 200;
    //单个商品数量限制
    public static final Integer CART_ITEM_NUM_LIMIT = 200;

    //订单防重令牌。只需要保存15min
    public static final String ORDER_TEMP_TOKEN = "order:temptoken:"; //order:temptoken:交易号

    //订单超时关闭时间
    public static final Integer ORDER_CLOSE_TTL = 60*45; //秒为单位
    public static final Integer ORDER_REFUND_TTL = 60*60*24*30;
    public static final String MQ_RETRY = "mq:message:";
}
