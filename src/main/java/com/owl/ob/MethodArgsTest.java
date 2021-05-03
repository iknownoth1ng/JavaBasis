package com.owl.ob;

import java.util.Arrays;

/**
 * @author by 15515
 * @Classname MethodArgsTest
 * @Description TODO 面向对象（上） 可变个数形参的方法
 * @Date 2021/4/24 20:14
 **/
public class MethodArgsTest {
    public static void main(String[] args) {
        test(1);
        test("hello");
        test("hello", "world");
    }

    public static void test(int i) {
        System.out.println(i);
    }

//    public static void test(String s) {
//        System.out.println(s);
//    }

    /*
    jdk5.0后新加的功能；
       可变个数形参的方法
       通常可以用在查询sql的方法里
    */
    public static void test(String... strs) {
        System.out.println(Arrays.toString(strs));
    }
/*
不可以与下面的方法共存
 */
   /* public static void test(String[] strs) {
        System.out.println(Arrays.toString(strs));
    }*/

    /*
    必须放在最后,而且只能声明一个。
     */
    public static void test(int i, String... strs) {
        System.out.println(Arrays.toString(strs));
    }
}
