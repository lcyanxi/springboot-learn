package com.lcyanxi.springbootmanage;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author lichang
 * @date 2020/9/13
 */
public class PCRegisterTest {
    private static Pattern pattern = Pattern.compile("[0-9]*");
    public static void main(String[] args) {
        String aa = "3264882,3264880,3264888,3264886";

        System.out.println(aa);
        System.out.println(JSON.toJSONString(aa));
        List<Integer> list = str2ArrayBySplit(JSON.toJSONString(aa));
        System.out.println(list);

    }

    /**
     * 将以逗号隔开的字符串转换成数字集合
     */
    private static List<Integer> str2ArrayBySplit(String str) {
        List<String> subClassIdList = Arrays.asList(str.split(","));
        List<Integer> result = new ArrayList<>();

        //如果不是数字格式的剔除掉，如果为空则剔除，如果为负数则剔除
        subClassIdList.stream().filter(subClassId -> pattern.matcher(subClassId).matches() && StringUtils.isNotBlank(subClassId)).collect(Collectors.toList()).forEach(subClassId -> result.add(Integer.parseInt(subClassId)));
        return result;
    }
}
