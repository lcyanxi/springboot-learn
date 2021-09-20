package com.lcyanxi.springbootmanage;

import com.google.common.collect.Lists;
import com.lcyanxi.model.ClassInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author lichang
 * @date 2020/9/13
 */
public class PCRegisterTest {
    private static Pattern pattern = Pattern.compile("[0-9]*");
    public static void main(String[] args) {
        List<ClassInfo> classInfos = Lists.newArrayList();
        for (int j=0 ; j< 2; j++){
            for (int i = 1; i<5; i++){
                ClassInfo info = new ClassInfo();
                info.setClassId(i);
                info.setParentClassId(i);
                info.setStock(ThreadLocalRandom.current().nextInt(0, 10));
                classInfos.add(info);
            }
        }
        System.out.println(classInfos);
        Map<Integer, Integer> listMap = classInfos.stream().collect(Collectors.groupingBy(ClassInfo::getParentClassId,
                Collectors.summingInt(ClassInfo::getStock)));
        System.out.println(listMap);

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
