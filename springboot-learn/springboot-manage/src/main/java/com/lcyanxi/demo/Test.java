package com.lcyanxi.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lcyanxi on 2021/5/18
 *
 * 给定一个范围在  1 ≤ a[i] ≤ n ( n = 数组大小 ) 的 整型数组，数组中的元素一些出现了两次，另一些只出现一次。
 找到所有在 [1, n] 范围之间没有出现在数组中的数字。
 您能在不使用额外空间且时间复杂度为O(n)的情况下完成这个任务吗? 你可以假定返回的数组不算在额外空间内。 
 */

public class Test {

    public static void main(String[] args) {
        int [] arr = new int[]{3,1,4,5,7};
        System.out.println(process(arr,9));
        System.out.println(exist(13));
    }

    public static boolean exist(int c) {
        int slow = 1;
        int height = c/2;
        while(slow < height){
            if((slow * slow + height * height) == c){
                System.out.println(slow + "：" +height);
                return true;
            }
            if((slow * slow + height * height) > c){
                height --;
            }else{
                slow ++;
            }
        }
        return false;
    }


    public static  List<Integer> process(int [] arr, int target){
        Map<Integer,Integer> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();

        for (int i=0; i<arr.length; i++){
            int temp = target - arr[i];
            if(map.containsKey(temp)){
                list.add(map.get(temp));
                list.add(i);
                break;
            }
            map.put(arr[i],i);
        }
        return  list;
    }

}
