package com.lcyanxi.basics.algorithm.arry;

/**
 * 直方图装雨量
 * 描述：给定一个正整数arr,把arr想象成一个直方图，放回这个直方图如果装水，能够装几格水
 * eg:
 * 输入：arr = {4,8,5,1,9,7}  输出：10
 * @author lichang
 * @date 2020/12/2
 */
public class HistogramRain {
    public static void main(String[] args) {

        int[] arr = {4,8,5,1,9,7};
        System.out.println(histogramRain(arr));

        System.out.println(histogramRain2(arr));



    }

    /**
     * 思路 max(left) + max(right)  时间复杂度o(N^2)
     * |    |
     * |____|
     * @param arr 原数据
     * @return
     */
    private static int histogramRain(int[] arr){
        if (arr == null || arr.length < 2){
            return 0;
        }
        int index = 1;
        int temp = 0;
        while (index < arr.length - 1){
            int left = 0;
            int right = arr.length - 1;
            int leftMax = 0;
            int rightMax = 0;
            while (left < index){
                if (arr[left] > leftMax){
                    leftMax = arr[left];
                }
                left ++;
            }
            while (right > index){
                if (arr[right] > rightMax){
                    rightMax = arr[right];
                }
                right --;
            }
            int size = Math.min(leftMax,rightMax);
            if (size > arr[index]){
                temp += size - arr[index];
            }
            index ++;
        }
        return temp;
    }

    /**
     * 动态规划
     * @param arr 原数据
     * @return
     */
    private static int histogramRain2(int[] arr){
        if (arr == null || arr.length < 2){
            return 0;
        }
        int water = 0;
        int leftMax = arr[0];
        int rightMax = arr[arr.length - 1];
        int left = 1;
        int right = arr.length - 2;
        while (left <= right){
            if (leftMax <= rightMax){
                water += Math.max(0,leftMax - arr[left]);
                leftMax = Math.max(leftMax,arr[left ++ ]);
            }else {
                water += Math.max(0,rightMax-arr[right]);
                rightMax = Math.max(rightMax,arr[right--]);
            }
        }
        return water;
    }

}
