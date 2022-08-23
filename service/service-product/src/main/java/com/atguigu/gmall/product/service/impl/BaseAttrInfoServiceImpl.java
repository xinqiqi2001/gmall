package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2022-08-23 20:48:38
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    /**
     *
     * @param category1Id  一级分裂
     * @param category2Id  二级分类
     * @param category3Id  三级分类
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(Long category1Id, Long category2Id, Long category3Id) {

        List<BaseAttrInfo> baseAttrInfos=baseAttrInfoMapper.getAttrInfoAndValueByCategoryId(category1Id,category2Id,category3Id);

        return baseAttrInfos;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //保存属性名
        baseAttrInfoMapper.insert(baseAttrInfo);
        //获取保存属性名后的自增id  因为添加属性值的时候有关联 需要维护关联关系
        Long id = baseAttrInfo.getId();
        //保存属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        attrValueList.forEach(a->{
            //回填属性名记录的自增id
            a.setAttrId(id);
            baseAttrValueMapper.insert(a);
        });

    }
}




