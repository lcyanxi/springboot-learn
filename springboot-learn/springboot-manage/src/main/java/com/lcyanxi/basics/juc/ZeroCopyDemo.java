package com.lcyanxi.basics.juc;

import java.io.*;

/**
 * @author : lichang
 * @desc : 零拷贝 Demo
 * @since : 2022/04/19/6:34 下午
 */
public class ZeroCopyDemo {
    public static void readFile() {
        long millis = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/lichang/Desktop/zero_copy_new.txt"));
                BufferedWriter bufferedWriter =
                        new BufferedWriter(new FileWriter("/Users/lichang/Desktop/zero_copy_out.txt"))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                bufferedWriter.write(line + "\r\n");
            }
            bufferedWriter.flush();
        } catch (IOException e) {}
        System.out.println("times:" + (System.currentTimeMillis() - millis));
    }


    public static void main(String[] args) {
        readFile();
    }
}
