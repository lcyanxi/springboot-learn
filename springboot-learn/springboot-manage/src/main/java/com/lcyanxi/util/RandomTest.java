package com.lcyanxi.util;

import org.apache.commons.lang3.RandomUtils;

public class RandomTest {
    public static double replaceFifthDecimal(double number) {
        // 生成一个 0 到 9 的随机数
        int randomDigit = RandomUtils.nextInt(0,9);
        long integerPart = (long) number;
        double decimalPart = number - integerPart;

        // 计算小数点后第五位的值
        double factor = Math.pow(10, 5);
        int currentFifthDecimal = (int) ((decimalPart * factor) % 10);

        // 替换小数点后第五位
        decimalPart = decimalPart - (currentFifthDecimal / factor) + (randomDigit / factor);

        // 返回修改后的数字
        return integerPart + decimalPart;
    }

    public static void main(String[] args) {
        double a = 39;
        System.out.println(replaceFifthDecimal(a));
    }
}
