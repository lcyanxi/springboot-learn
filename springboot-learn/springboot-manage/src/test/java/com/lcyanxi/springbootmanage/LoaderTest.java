package com.lcyanxi.springbootmanage;

/**
 * @author lichang
 * @date 2020/9/12
 */
public class LoaderTest {


    public static void main(String[] args) {
        System.out.println(Son.B);
        System.out.println(Son.A);
        Son son = new Son();
        System.out.println();
    }
}


 class Father{
    public static int A = 1;
    static {
        A = 2;
        System.out.println(A);
    }
    
}

 class Son extends Father{
    public static int B = A;

    static {
        System.out.println(B);
    }
}