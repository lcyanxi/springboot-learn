package com.lcyanxi.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.lcyanxi.model.UserBase;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.util.Streams;

/**
 * 1. 简单的直接Bean.class
 * 2. 复杂的用 TypeReference
 * @author lichang
 * @date 2021/1/13
 */
public class TypeConvertUtil {

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json1 = "{\"userName\":\"小李飞刀\",\"age\":18,\"addTime\":1591851786568}";
        String json2 = "[{\"userName\":\"小李飞刀\",\"age\":18,\"addTime\":123}, {\"userName\":\"小李飞刀2\",\"age\":182,\"addTime\":1234}]";

        UserBase userBase = objectMapper.readValue(json1, UserBase.class);
        System.out.println("简单: " + userBase);

        //2.把Json转换成map，必须使用 TypeReference , map的类型定义 可以根据实际情况来定，比如若值都是String那么就可以 Map<String, String>
        Map<String, Object> userBaseMap =  objectMapper.readValue(json1, new TypeReference<Map<String, Object>>() {});
        System.out.println("map: " + userBaseMap);


        //3.list<Bean>模式，必须用 TypeReference
        List<UserBase> userBaseList = objectMapper.readValue(json2, new TypeReference<List<UserBase>>() {});
        System.out.println("list: " + userBaseList);

        //4.Bean[] 数组，必须用 TypeReference
        UserBase[] userBaseAry = objectMapper.readValue(json2, new TypeReference<UserBase[]>() {});
        for (UserBase userBase1 : userBaseAry){
            System.out.print("ary: " + userBase1);
        }
    }

}
