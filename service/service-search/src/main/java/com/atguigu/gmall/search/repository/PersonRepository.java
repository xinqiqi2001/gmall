package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/3 23:54
 * @Version 1.0
 */
@Repository //标注在数据库层
//泛型 第一个是返回值类型 第二个是主键类型
public interface PersonRepository extends CrudRepository<Person,Long> {

    //1.查询address在西安市的人
    List<Person> findAllByAddressLike(String address);

    //2.查询年龄小于等于19的人
    List<Person> findAllByAgeLessThanEqual(Integer age);

    //3.查询 年龄大于18且在西安市的人
    List<Person> findAllByAgeGreaterThanAndAddressLike(Integer age, String address);

    //4.查询年龄大于18 且 在郑州金水的人 或id等于1003的人  有OR之后会有歧义 会将两个当做条件 可能只会查询出满足一个条件的
    List<Person> findAllByAgeGreaterThanAndAddressLikeOrId(Integer age, String address, Long id);
}
