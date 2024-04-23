package com.lcyanxi.basics.algorithm.doublePoint;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Data;

/**
 * @author : lichang
 * @desc : 给出一组区间，请合并所有重叠的区间。
 *       请保证合并后的区间按区间起点升序排列。
 * @since : 2024/04/23/6:29 下午
 */
public class ArrayMerge {

    public static void main(String[] args) {
        List<Interval> originList = Lists.newArrayList(
                Interval.builder().start(10).end(30).build(),
                Interval.builder().start(20).end(60).build(),
                Interval.builder().start(80).end(100).build(),
                Interval.builder().start(150).end(180).build());
        System.out.println(merge(originList));
    }


    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     */
    public static List<Interval> merge(List<Interval> intervals) {
        if (CollectionUtils.isEmpty(intervals) || intervals.size() <= 1) {
            return intervals;
        }
        // 按 start 升序排序
        List<Interval> collect =
                intervals.stream().sorted(Comparator.comparing(Interval::getStart)).collect(Collectors.toList());

        List<Interval> resultList = Lists.newArrayList();
        resultList.add(collect.get(0));
        int count = 0;
        for (int i = 1; i < collect.size(); i++) {
            Interval interval = collect.get(i);
            Interval origin = resultList.get(count);
            if (origin.getEnd() < interval.getStart()) {
                resultList.add(interval);
                count++;
            } else {
                resultList.remove(count);
                int end = Math.max(origin.getEnd(), interval.getEnd());
                Interval newInterval = Interval.builder().start(origin.getStart()).end(end).build();
                resultList.add(newInterval);
            }
        }
        return resultList;
    }



    @Builder
    @Data
    public static class Interval {
        int start;
        int end;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

}
