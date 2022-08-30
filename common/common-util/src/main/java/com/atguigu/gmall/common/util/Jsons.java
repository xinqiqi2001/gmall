package com.atguigu.gmall.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Author Xiaoxin
 * @Date 2022/8/28 15:33
 * @Version 1.0
 */
public class Jsons {
    private static ObjectMapper mapper = new ObjectMapper();
    /**
     * 把对象转为json字符串
     * @param object
     * @return
     */
    public static String toStr(Object object) {
        //jackson
        try {
            String s = mapper.writeValueAsString(object);
            return s;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 把json字符串转为对象
     * @param jsonStr
     * @param clz
     * @param <T>
     * @return
     */
    public static<T> T  toObj(String jsonStr, Class<T> clz) {

        T t = null;
        try {
            t = mapper.readValue(jsonStr, clz);
            return t;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}





