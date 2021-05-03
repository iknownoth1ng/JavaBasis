package com.owl.ob;

/**
 * @author by 15515
 * @Classname ValueTransferTest1
 * @Description TODO
 * @Date 2021/4/24 21:26
 **/
public class ValueTransferTest1 {
    public static void main(String[] args) {

        /*
    如果变量类型是基本数据类型，那么赋值的是变量所保存的值；
    如果变量类型是引用数据类型，那么赋值的是变量保存的数据的地址值；
     */
        int n = 10;
        int m = 20;
        System.out.println(String.format("n:{%d},m:{%d}", n, m));
//        int temp = n;
//        n = m;
//        m = temp;
        swap(m, n);
        System.out.println(String.format("n:{%d},m:{%d}", n, m));


        Data data = new Data();
        data.m = 10;
        data.n = 20;
        System.out.println(String.format("n:{%d},m:{%d}", data.n, data.m));
        swap(data);
        System.out.println(String.format("n:{%d},m:{%d}", data.n, data.m));
    }

    /*
    交换值
     */
    public static void swap(int m, int n) {
        int temp = n;
        n = m;
        m = temp;
    }

    public static void swap(Data data) {
        int temp = data.n;
        data.n = data.m;
        data.m = temp;
    }
}

class Data {
    int m;
    int n;
}
