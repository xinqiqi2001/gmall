# gmall
第一天 初始化
第二天 初始化
第三天  8.24 写完品牌增删改查
第四天  8.25 后台接口文档 写完
第五天 8.26 商品详情-价格、分类、基本信息、图片展示完成  指定商品销售属性的名和值高亮显示完成
第六天 8.27 根据销售属性跳转指定商品页面  线程池优化终于都完成了
第七天 8.29 查询商品详细信息 用缓存数据库缓存
第八天 8.29 查询商品详细信息 优化高并发请求  
使用布隆过滤器(流程 先查缓存 缓存中没有查询布隆过滤器有没有  布隆过滤器说有 那么加锁(配置好的redisson的锁)回源 布隆说没有 直接返回null)
第九天 9.1 布隆过滤器定时重建  抽取aop环绕通知  
使用表达式动态获取缓存key 动态获取返回值 开启动态布隆 动态指定分布式锁名 缓存自定义starter未完成 缓存抽取未完成

第十二天 检索商品信息成功
第十三天 单点登录完成