package com.lcyanxi.algorithm.string;

/**
 * 字符交换
 * 描述： 把一个0-1（只包含0和1的串）进行排序，你可以交换任意两个位置，问最少交换多少次？
 * eg ： 输入 ： str = "00101110101100101" 输出：5
 *
 * 那如果每次只能相邻两个数交换  最少多少次呢
 * @author lichang
 * @date 2020/11/21
 */
public class CharExchange {
    public static void main(String[] args) {
        String string = "00101110101100101";
        System.out.println(charExchange(string));
        System.out.println(charMinExchange(string));
        int [] arr = {3,2,5,7,4,1,8};

        quickSort(arr,0,arr.length - 1);
        for (int e : arr){
            System.out.print(e);
        }
    }
    // 快排思想
    private static int charExchange(String string){
        char[] chars = string.toCharArray();
        int left = 0;
        int right = chars.length - 1;
        int index = 0;
        char temp ;
        while (left <= right){
            for (; left <= right && chars[left] == '0'; ){
                left ++;
            }
            for (; left <= right && chars[right] == '1';){
                right --;
            }
            temp = chars[left];
            chars[left] =chars[right];
            chars[right] = temp;
            index ++;
        }
        return index;
    }


    private static int charMinExchange(String string){
        int num = charExchange2(string, '0');
        int num2 = charExchange2(string,'1');
        System.out.println("0 交换次数" + num + ", 1 交换次数" + num2);
        return Math.min(num, num2);
    }

    private static int charExchange2(String str,char charStr){
        char[] chars = str.toCharArray();
        int left = 0;
        int index = 0;
        int num = 0;
        while (left < chars.length){
            if (chars[left] == charStr){
                num = num + (left - index);
                index ++;
            }
            left ++;
        }
        return num;
    }

    private static void quickSort(int arr[],int left, int right){
        if (left < right){
            int target = quickSortUtil(arr, left, right);
            quickSort(arr,left,target-1);
            quickSort(arr,target + 1,right);
        }
    }

    /**
     * 快速排序算法
     * 先从数列中取出一个数作为基准数。
     * 分区过程，将比这个数大的数全放到它的右边，小于或等于它的数全放到它的左边。
     * 再对左右区间重复第二步，直到各区间只有一个数。
     * @param arr 原数据
     * @return
     */
    private static int quickSortUtil(int[] arr,int left,int right){
        int target = arr[left];
        while (left < right){  //从表的两端开始向表的中间交替扫描
            while (left < right && arr[right] >= target){
                //如果最左端的值大于标记值，则下标往前移动一位
                right --;
            }
            arr[left] = arr[right];
            while (left < right && arr[left] <= target){
                left ++;
            }
            arr[right] = arr[left];
        }
        //把标记值放在low下标的位置
        arr[left] = target;
        return left;
    }

}
