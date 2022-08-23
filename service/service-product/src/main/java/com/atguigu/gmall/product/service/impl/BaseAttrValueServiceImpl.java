package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Xiaoxin
 * @description 针对表【base_attr_value(属性值表)】的数据库操作Service实现
 * @createDate 2022-08-23 20:48:38
 */
@Service
public class BaseAttrValueServiceImpl extends ServiceImpl<BaseAttrValueMapper, BaseAttrValue>
        implements BaseAttrValueService {
    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> queryWrapper=new QueryWrapper();

        queryWrapper.eq("attr_id",attrId);

        List<BaseAttrValue> values = baseAttrValueMapper.selectList(queryWrapper);

        return values;
    }
}




