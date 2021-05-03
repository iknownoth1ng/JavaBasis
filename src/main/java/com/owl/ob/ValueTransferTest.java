package com.owl.ob;

import com.sun.org.apache.xpath.internal.operations.Or;

/**
 * @author by 15515
 * @Classname ValueTransferTest
 * @Description TODO java中的变量赋值
 * @Date 2021/4/24 20:44
 **/
public class ValueTransferTest {
    //变量赋值
    /*
    如果变量类型是基本数据类型，那么赋值的是变量所保存的值；
    如果变量类型是引用数据类型，那么赋值的是变量保存的数据的地址值；
     */
    public static void main(String[] args) {
        System.out.println("基本数据类型赋值*********");
        int n = 10;
        int m = n;
        System.out.println(String.format("n:{%d},m:{%d}", n, m));
        n = 20;
        System.out.println(String.format("n:{%d},m:{%d}", n, m));

        System.out.println("引用数据类型赋值*********");
        Order o1 = new Order();
        o1.orderId = 1001;
        Order o2 = o1;
        System.out.println(String.format("o1:{%d},m:{%d}", o1.orderId, o2.orderId));
        o1.orderId = 1002;
        System.out.println(String.format("o1:{%d},m:{%d}", o1.orderId, o2.orderId));
    }
}

class Order {
    int orderId;
}
