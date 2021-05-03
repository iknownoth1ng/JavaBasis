package com.owl.ob;

/**
 * static关键字
 *1.static 可以修饰：属性，方法，代码块，内部类
 *2。使用static修饰属性:静态变量
 *3.静态属性vs非静态属性（实例变量）
 *  3.1实例变量：每个对象都独立拥有一套类中的非静态变量。当修改一个对象中的实例变量不会影响其他对象中的实例变量；
 *     静态变量：所有的对象共享一个静态变量，当修改静态变量会导致其他对象中引用的静态变量也改变。
 *  3.2静态变量随着类的加载而加载
 *     静态变量的加载要早于对象的创建
 *     类只会加载一次，则静态变量在内存中只存在一份，存在于方法区的静态域中
 *  3.3静态属性举例，Math.PI
 *
 *  4.用static修饰方法称为静态方法
 *   4.1随着类的加载而加载，可以用类来调用
 *   静态方法中只能使用静态的属性和方法
 *   非静态方法中，既可以使用静态的也可以使用非静态的（因为生命周期）
 *  5.注意：不能使用this和super关键字
 *        从生命周期角度考虑
 *  6.开发中如何确定一个属性是否要声明static
 *    >属性是可以被多个对象共享的，不会随着对象的不同而不同
 *    >类中的常量也常常声明为static
 *
 *   开发中如何确定一个方法是否要声明static
 *    >操作静态属性的
 *    >工具类，如Math Collections
 * @author by 15515
 * @Date 2021/5/4 0:30
 **/
public class StaticTest {
    public static void main(String[] args) {
        People p1 = new People();
        p1.name = "姚明";
        p1.age = 40;
        p1.nation = "CHINA";
        People p2 = new People();
        p2.name = "马龙";
        p2.age = 30;
        p2.nation = "CHN";
        System.out.println(People.nation);
        System.out.println(p1.nation);
    }
}

class People {
    public String name;
    public int age;
    public static String nation;
}
