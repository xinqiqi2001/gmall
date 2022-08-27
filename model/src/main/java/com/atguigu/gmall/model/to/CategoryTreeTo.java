package com.atguigu.gmall.model.to;

import lombok.Data;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/8/26 19:05
 * @Version 1.0
 *
 * 三层分类树结构
 * 支持无线层级
 * 但是本项目只有三级
 */
@Data
public class CategoryTreeTo {
    private Long categoryId;
    private String categoryName;
    private List<CategoryTreeTo> categoryChild;//子分类
}
