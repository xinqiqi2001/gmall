package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.model.to.ValueSkuJsonTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Xiaoxin
 * @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
 * @createDate 2022-08-23 20:48:38
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
        implements SkuInfoService {

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SkuInfoService skuInfoService;

    @Resource
    SkuInfoMapper skuInfoMapper;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @Resource
    BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    SearchFeignClient searchFeignClient;
    /**
     * 保存sku
     *
     * @param info
     */
    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo info) {
        //1、sku基本信息保存到 sku_info
        save(info);
        Long skuId = info.getId();

        //2、sku的图片信息保存到 sku_image
        info.getSkuImageList().forEach(skuImage -> {
            skuImage.setSkuId(skuId);

        });

        skuImageService.saveBatch(info.getSkuImageList());

        //3、sku的平台属性名和值的关系保存到 sku_attr_value
        List<SkuAttrValue> attrValueList = info.getSkuAttrValueList();

        attrValueList.forEach(attrValue -> {
            attrValue.setSkuId(skuId);
        });

        skuAttrValueService.saveBatch(attrValueList);

        //4、sku的销售属性名和值的关系保存到 sku_sale_attr_value
        List<SkuSaleAttrValue> saleAttrValueList = info.getSkuSaleAttrValueList();

        saleAttrValueList.forEach(saleAttrValue -> {
            saleAttrValue.setSkuId(skuId);
            saleAttrValue.setSpuId(info.getSpuId());
        });

        skuSaleAttrValueService.saveBatch(saleAttrValueList);

        //数据库每新增一条数据 那么就把这个商品的id设置新布隆里面
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        filter.add(skuId);
    }

    /**
     * 查询商品信息
     * 不用这个总查询了  执行单一策略 每个请求对应一个接口
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
//        //TODO 最后将这个方法里的其他方法拆分成其他方法
//
//        SkuDetailTo detailTo = new SkuDetailTo();
//        //0、查询到商品的基本信息 skuInfo  v
//        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
//
//
//        //2、商品（sku）的基本信息   对应的表是sku_info
//        //把查询到的数据一定放到 SkuDetailTo 中
//        detailTo.setSkuInfo(skuInfo);
//
//        //3、商品（sku）的图片        对应的表是sku_image   v
//        List<SkuImage> imageList = skuImageService.getSkuImage(skuId);
//        skuInfo.setSkuImageList(imageList);
//
//
//        //1、商品（sku）所属的完整分类信息：  base_category1、base_category2、base_category3
//        CategoryViewTo categoryViewTo = baseCategory3Mapper.getCategoryView(skuInfo.getCategory3Id());
//        detailTo.setCategoryView(categoryViewTo);
//
//
//        //实时价格查询
//        BigDecimal price = get1010Price(skuId);
//        detailTo.setPrice(price);
//
//        //TODO 改写这个了 4、商品（sku）所属的SPU当时定义的所有销售属性名值组合（按固定的排序展示）。
//        //          spu_sale_attr、spu_sale_attr_value
//        //          并标识出当前sku到底spu的那种组合，页面要有高亮框 sku_sale_attr_value
//        //查询当前sku对应的spu定义的所有销售属性名和值（固定好顺序）并且标记好当前sku属于哪一种组合
//        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueMarkSku(skuInfo.getSpuId(), skuId);
//        detailTo.setSpuSaleAttrList(saleAttrList);
//
//        //--------------------暂时没有这些业务----------------------------
//        //5、商品（sku）类似推荐    （x）
//        Long id = skuInfo.getId();
//        Long spuId = skuInfo.getSpuId();
//        String valueJson = spuSaleAttrService.getAllSkuSaleAttrValueJson(spuId);
//
//        detailTo.setValuesSkuJson(valueJson);
//        //6、商品（sku）介绍[所属的spu的海报]        spu_poster（x）
//        //7、商品（sku）的规格参数                  sku_attr_value
//        //8、商品（sku）售后、评论...              相关的表 (x)
//
//
//        return detailTo;
        return null;
    }


    @Override
    public BigDecimal get1010Price(Long skuId) {
        //查询实时价格
        BigDecimal price = skuInfoMapper.getRealPrice(skuId);
        return price;
    }

    /**
     * 获取SkuInfo(基本数据)的信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getDetailSkuInfo(Long skuId) {
        //0、查询到商品的基本信息 skuInfo
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    /**
     * 查询sku的图片信息
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getDetailSkuImages(Long skuId) {

        List<SkuImage> imageList = skuImageService.getSkuImage(skuId);
        return imageList;
    }

    /**
     * 查询所有的商品id
     * @return
     */
    @Override
    public List<Long> findAllSkuId() {

        return skuInfoMapper.getAllSkuId();

    }

    /**
     * 上架商品
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
        //通过skuId获取SkuInfo 将SkuInfo的IsSale属性设置为1
        SkuInfo skuInfo = skuInfoService.getOne(new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getId, skuId));
        skuInfo.setIsSale(1);
        skuInfoService.updateById(skuInfo);

        //将商品保存到es中
        Goods goods = getGoodsBySkuId(skuId);
        searchFeignClient.saveGoods(goods);
    }

    /**
     * 下架商品
     * @param skuId
     */
    @Override
    public void cancelSale(Long skuId) {
        //通过skuId获取SkuInfo 将SkuInfo的IsSale属性设置为0
        SkuInfo skuInfo = skuInfoService.getOne(new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getId, skuId));
        skuInfo.setIsSale(0);
        //将指定商品从es中删除
        searchFeignClient.deleteGoods(skuId);
        skuInfoService.updateById(skuInfo);
    }

    /**
     * 获取到需要存储到es中的商品的所有数据  条件是商品是上架状态
     * @param skuId
     * @return
     */
    @Override
    public Goods getGoodsBySkuId(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        Goods goods=new Goods();
        goods.setId(skuId);
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());
        goods.setTmId(skuInfo.getTmId());
        //-------------

        //获取品牌的信息  skuInfo里只保存了品牌id
        BaseTrademark trademark = baseTrademarkService.getById(skuInfo.getTmId());

        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());
        //只有三级分类id  根据三级分类id获取二级分类id和一级分类id和Name
        Long category3Id = skuInfo.getCategory3Id();
        CategoryViewTo categoryView = baseCategory3Mapper.getCategoryView(category3Id);

        goods.setCategory1Id(categoryView.getCategory1Id());
        goods.setCategory1Name(categoryView.getCategory1Name());
        goods.setCategory2Id(categoryView.getCategory2Id());
        goods.setCategory2Name(categoryView.getCategory2Name());
        goods.setCategory3Id(categoryView.getCategory3Id());
        goods.setCategory3Name(categoryView.getCategory3Name());

        //设置热度分
        //未来要做热度分更新 点击一次 热度分+1
        goods.setHotScore(0L);

        //查当前sku所有平台属性名和值
        List<SearchAttr> attrs=skuAttrValueService.getSkuAttrNameAndValueName(skuId);
        goods.setAttrs(attrs);



        return goods;
    }


}




