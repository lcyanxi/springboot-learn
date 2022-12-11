package com.lcyanxi.manage;

import com.alibaba.fastjson.JSON;
import com.lcyanxi.model.SortInfo;
import org.assertj.core.util.Lists;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/05/30/3:52 下午
 */
public class SortInfoDemo {
    public static void main(String[] args) {
        List<SortInfo> sortInfos = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            SortInfo info = SortInfo.builder().isNewFlag(i / 2 == 0).difficulty(random(1,5))
                    .createdAt(System.currentTimeMillis() - random(1, 100)).build();

            sortInfos.add(info);
        }

        print(sortInfos);
        System.out.println("============");

        List<SortInfo> infos =
                sortInfos
                        .stream().sorted(Comparator.comparing(SortInfo::isNewFlag, Comparator.reverseOrder())
                                .thenComparing(SortInfo::getDifficulty)
                                .thenComparing(SortInfo::getCreatedAt, Comparator.reverseOrder()))
                        .collect(Collectors.toList());
        print(infos);
    }

    private static int random(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    private static void print(List<SortInfo> sortInfos) {
        sortInfos.forEach(item -> System.out.println(JSON.toJSONString(item)));
    }
}
