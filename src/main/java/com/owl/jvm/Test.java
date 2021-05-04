package com.owl.jvm;

/**
 * 测试
 *
 * @author by 15515
 * @Date 2021/5/5 1:39
 **/
public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        CustomClassLoader customClassLoader = new CustomClassLoader("自定义加载器");
        customClassLoader.setLoadPath("D:\\");
        Class<?> aClass = customClassLoader.loadClass("com.owl.jvm.Person");
        System.out.println(aClass.getClassLoader());
    }
}
