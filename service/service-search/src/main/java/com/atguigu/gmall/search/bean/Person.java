package com.atguigu.gmall.search.bean;

import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Author Xiaoxin
 * @Date 2022/9/3 22:54
 * @Version 1.0
 */
@Data
@Document(indexName = "person",shards = 1,replicas = 1)//indexName索引名 将需要填进该索引里  shards分片存贮 replicas每条数据有几条备份
public class Person {

    //声明主键
    @Id
    private  Long id;

    /**
     *对应指定索引里的数据
     */
    @Field(value = "first",type = FieldType.Keyword)//text存的时候会分词 Keyword不分词
    private String fistName;
    @Field(value = "last",type = FieldType.Keyword)
    private String lastName;
    @Field(value = "age")
    private Integer age;
    //自动决定 java的string默认是ES的TEXT类型
    @Field(value = "address",type = FieldType.Text,analyzer = "ik_smart")//指定分词器
    private String address;
}
