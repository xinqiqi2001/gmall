package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiaoxin
 * @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
 * @createDate 2022-08-23 20:48:38
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
        implements BaseAttrInfoService {

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    /**
     * @param category1Id 一级分裂
     * @param category2Id 二级分类
     * @param category3Id 三级分类
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(Long category1Id, Long category2Id, Long category3Id) {

        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.getAttrInfoAndValueByCategoryId(category1Id, category2Id, category3Id);

        return baseAttrInfos;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        //属性id
        Long Iid = baseAttrInfo.getId();
        if (Iid == null) {
            //1没有id  说明是新增

            addBaseAttrInfo(baseAttrInfo);

        } else {
            //有id  说明之前已经有了这个属性 这次是修改
            updateBaseAttrInfo(baseAttrInfo, Iid);

        }


    }

    /**
     * 修改属性
     * @param baseAttrInfo
     * @param Iid
     */
    private void updateBaseAttrInfo(BaseAttrInfo baseAttrInfo, Long Iid) {

        //修改属性名
        baseAttrInfoMapper.updateById(baseAttrInfo);

        //修改属性值 把之前老的属性值全删除 新提交全部新增
        List<Long> ids = new ArrayList<>();

        baseAttrInfo.getAttrValueList().forEach(baseAttrValue->{
            //判断前端没有提交的 都是需要删除的
            Long id = baseAttrValue.getId();
            if (id != null) {
                ids.add(id);
            }
        });

        //ids 是本次提交的属性值的所有id
        if (ids.size() > 0) {
            QueryWrapper<BaseAttrValue> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("attr_id", Iid);
            //ids 是我本次提交的属性值的id  没有在这个ids的  就是我本次没有提交的数据 需要删除
            deleteWrapper.notIn("id", ids);
            baseAttrValueMapper.delete(deleteWrapper);
        }else {
            //ids=0  说明前端一个属性值id都没传过来 全删除
            QueryWrapper<BaseAttrValue> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("attr_id", Iid);
            baseAttrValueMapper.delete(deleteWrapper);
        }

        baseAttrInfo.getAttrValueList().forEach(baseAttrValue->{
            //获取属性值的id
            Long id = baseAttrValue.getId();

            if (id == null) {
                //没有属性值的id 是新增属性值
                //把属性的id设置给attrId  因为提交的新增属性值并没有attrId  跟平台属性绑定上
                baseAttrValue.setAttrId(Iid);
                baseAttrValueMapper.insert(baseAttrValue);
            }

            if (id != null) {
                //有属性值的id  说明之前有这个属性值 进行修改操作
                baseAttrValueMapper.updateById(baseAttrValue);
            }
        });
    }

    /**
     * 新增属性
     */
    private void addBaseAttrInfo(BaseAttrInfo baseAttrInfo) {
        //保存属性名
        baseAttrInfoMapper.insert(baseAttrInfo);
        //获取保存属性名后的自增id  因为添加属性值的时候有关联 需要维护关联关系
        Long id = baseAttrInfo.getId();
        //保存属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        attrValueList.forEach(a -> {
            //回填属性名记录的自增id
            a.setAttrId(id);
            baseAttrValueMapper.insert(a);
        });
    }
}




