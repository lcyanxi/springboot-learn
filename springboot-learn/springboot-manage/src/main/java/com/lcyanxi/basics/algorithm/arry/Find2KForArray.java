package com.lcyanxi.basics.algorithm.arry;


/**
 * 查找N/2次数的数
 * 描述：给定一个长度为N的数组,如果有那些数次数大于N/2,就放回这些数
 * 要求：时间复杂度O(N),不能使用额外的存储空间
 * eg:
 *    输入：arr = [4,1,7,5,5,5,5,8,2,7]
 *    输出：5
 * 思路：根消消乐思想一致，保持前后两数是否一致，不一致抵消一次，留下的就是候选的
 * @author lichang
 * @date 2020/12/9
 */
public class Find2KForArray {
    public static void main(String[] args) {

        int[] arr = {4,1,7,5,5,5,5,5,2,7};
        int[] arr1 = {4,4,4,5,5,5,5,5,4,4};
        System.out.println(find2KForArray(arr));
        System.out.println(find2KForArray(arr1));
    }
    private static int find2KForArray(int[] arr){
        int index = 0;
        int hp = 0;
        // 一次删除两个不同的数，剩余下来的就是候选
        for (int i = 0; i < arr.length; i++){
            if (hp == 0){
                index = arr[i];
                hp = 1;
            }else if (arr[i] == index){
                hp ++;
            }else {
                hp --;
            }
        }
        if (hp == 0){
            return -1;
        }
        for (int i = 0; i < arr.length; i++){
            if (arr[i] == index){
                hp ++;
            }
        }
        return hp > arr.length/2 ? index: -1;
    }

}
