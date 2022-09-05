package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

/**
 * @Author Xiaoxin
 * @Date 2022/9/4 0:10
 * @Version 1.0
 */
@SpringBootTest
public class EsTest {

    @Autowired
    PersonRepository personRepository;

    /**
     * GET /person/_search
     * {
     *   "query": {
     *     "match": {
     *       "address":"西安市"
     *     }
     *   }
     * }
     */
    @Test
    public void queryTest(){
//        Optional<Person> byId = personRepository.findById(1001l);
//        System.out.println(byId.get());

        //1.查询address在西安市的人
        List<Person> Xa = personRepository.findAllByAddressLike("西安市");
        Xa.forEach(xa->{
            System.out.println("address在西安市的人"+xa);
        });
        //2.查询年龄小于等于19的人
        List<Person> ageLessThan = personRepository.findAllByAgeLessThanEqual(19);
        ageLessThan.forEach(al->{
            System.out.println("年龄小于等于19的人"+al);
        });
        //3.查询 年龄大于18且在濮阳市华龙区的人
        List<Person> Py = personRepository.findAllByAgeGreaterThanAndAddressLike(18, "濮阳市华龙区");
        Py.forEach(hl->{
            System.out.println("年龄大于18且在濮阳市华龙区的人"+hl);
        });
        //4.查询年龄大于18 且 在濮阳市的人 或id等于1003的人
        List<Person> andAddressLikeOrId = personRepository.findAllByAgeGreaterThanAndAddressLikeOrId(18, "濮阳市", 1003l);
        andAddressLikeOrId.forEach(aai->{
            System.out.println("年龄大于18 且 在濮阳市的人 或id等于1003的人"+aai);
        });

    }

    @Test
    public void test1(){
        Person person = new Person();
        person.setId(1001L);
        person.setFistName("辛");
        person.setLastName("奇奇");
        person.setAge(18);
        person.setAddress("西安市雁塔区");

        personRepository.save(person);
        System.out.println("保存完成1");


        Person person1 = new Person();
        person1.setId(1002L);
        person1.setFistName("王");
        person1.setLastName("文帅");
        person1.setAge(20);
        person1.setAddress("濮阳市华龙区");

        personRepository.save(person1);
        System.out.println("保存完成2");

        Person person2 = new Person();
        person2.setId(1003L);
        person2.setFistName("张");
        person2.setLastName("庆凯");
        person2.setAge(19);
        person2.setAddress("郑州市金水区");

        personRepository.save(person2);
        System.out.println("保存完成3");
    }
}
