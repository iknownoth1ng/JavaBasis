package com.owl.ob;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by 15515
 * @Classname WrapperTest
 * @Description TODO 包装类
 * @Date 2021/4/24 22:28
 **/
public class WrapperTest {
    /*
    1.使得基本数据类型的变量具有类的特征，体现了java的面向对象
    int Integer
    short Short
    long Long
    byte Byte
    float Float
    double Double
    char Character
    boolean Boolean
    2.掌握：基本数据类型，包装类，Stirng三者之间的相互转换
     */
    @Test
    public void test1() {
        //基本数据类型-》包装类，调用包装类的构造器
        int num1 = 10;
        Integer integer = new Integer(num1);
        System.out.println(integer);
        Integer integer1 = Integer.valueOf(num1);
        System.out.println(integer1);
        Integer integer2 = new Integer("123ab");
        System.out.println(integer2);//报错


        Boolean aTrue = new Boolean("true");
        System.out.println(aTrue);//true
        Boolean true123 = new Boolean("true123");
        System.out.println(true123);//false

    }

    @Test
    public void test2() {
        //包装类-》基本数据类型，调用包装类的xxxValue()
        Integer integer = new Integer(10);
        int i = integer.intValue();
    }

    @Test
    public void test3() {
        //JDK5.0以后，自动装箱，自动拆箱
        //自动装箱:
        //基本数据类型-》包装类的对象
        int i = 10;
        auto(i);
        Integer integer = i;
        //自动拆箱:
        //包装类的对象-》基本数据类型
        Integer integer1 = new Integer(10);
        int i1 = integer;
    }

    public void auto(Object object) {

    }

    @Test
    public void test4() {
        //基本数据类型,包装类-》String类
        //方式1：连接运算
        String s = 1 + "";
        //方式2：调用String的valueOf(Xxx xxx)
        float f1 = 12.3f;
        String s1 = String.valueOf(f1);
    }

    @Test
    public void test5() {
        //String类-》基本数据类型,包装类
        //使用paresXxx方法
        String s = "123";
        Integer.parseInt(s);
    }

    //包装类的面试题
    @Test
    public void test6() {
//        确定条件表达式结果类型的规则的核心是以下3点：
//
//　　1 如果表达式1和表达式2操作数具有相同的类型，那么它就是条件表达式的类型。
//
//　　2 如果一个表达式的类型是byte、short、char类型的，而另外一个是int类型的常量表达式，且它的值可以用类型byte、short、char三者之一表示的，那么条件表达式的类型就是三者之一
//
//　　3 否则，将对操作数类型进行二进制数字提升，而条件表达式的类型就是第二个和第三个操作数被提升之后的类型
        Object o1 = true ? new Integer(1) : new Double(2.0);//三元运算符在编译的时候要求类型一致,如果表达式1和表达式2的类型不相同，那么他们需要对交集类型的自动参考转换。
        System.out.println(o1);//1.0
    }

    @Test
    public void test7() {
        Object o1;
        if (true) {
            o1 = new Integer(1);
        } else {
            o1 = new Double(2.0);
        }
        System.out.println(o1);//1
    }

    @Test
    public void test8() {
        Integer a = new Integer(1);
        Integer b = new Integer(1);
        System.out.println(a == b);//false

//Integer内部里缓存了-128~127的范围的数组，自动装箱的时候不需要new了
        Integer c = 1;
        Integer d = 1;
        System.out.println(c == d);//true

        Integer x = 128;
        Integer y = 128;
        System.out.println(x == y);//false
    }

    @Test
    public void test9() {
        Map<String, Boolean> map = new HashMap<>();
        Boolean b = (map != null) ? map.get("test") : false;
        System.out.println(b);
        //因为以上代码，在小于JDK 1.8的版本中执行的结果是NPE，在JDK 1.8 及以后的版本中执行结果是null。
        //之所以会出现这样的不同，这个就说来话长了，我挑其中的重点内容简单介绍下吧，以下内容主要内容还是围绕Java 8 的JLS 。
        //
        //JLS 15中对条件表达式（三目运算符）做了细分之后分为三种，区分方式：
        //
        //如果表达式的第二个和第三个操作数都是布尔表达式，那么该条件表达式就是布尔表达式
        //
        //如果表达式的第二个和第三个操作数都是数字型表达式，那么该条件表达式就是数字型表达式
        //
        //除了以上两种以外的表达式就是引用表达式
        //
        //因为Boolean b = (map!=null ? map.get("Hollis") : false);表达式中，第二位操作数为map.get("test")，虽然Map在定义的时候规定了其值类型为Boolean，但是在编译过程中泛型是会被擦除的（泛型的类型擦除），所以，其结果就是Object。那么根据以上规则判断，这个表达式就是引用表达式。
        //
        //又跟据JLS15.25.3中规定：
        //
        //如果引用条件表达式出现在赋值上下文或调用上下文中，那么条件表达式就是合成表达式
        //
        //因为，Boolean b = (map!=null ? map.get("Hollis") : false);其实就是一个赋值上下文（关于赋值上下文相见JLS 5.2），所以map!=null ? map.get("Hollis") : false;就是合成表达式。
        //
        //那么JLS15.25.3中对合成表达式的操作数类型做了约束：
        //
        //合成的引用条件表达式的类型与其目标类型相同
        //
        //所以，因为有了这个约束，编译器就可以推断（Java 8 中类型推断，详见JLS 18）出该表达式的第二个操作数和第三个操作数的结果应该都是Boolean类型。
        //
        //所以，在编译过程中，就可以分别把他们都转成Boolean即可，那么以上代码在Java 8中反编译后内容如下：
        //
        //Boolean b = maps == null ? Boolean.valueOf(false) : (Boolean)maps.get("Hollis");
        //但是在Java 7中可没有这些规定（Java 8之前的类型推断功能还很弱），编译器只知道表达式的第二位和第三位分别是基本类型和包装类型，而无法推断最终表达式类型。
        //
        //那么他就会先根据JLS 15.25的规定，把返回值结果转换成基本类型。然后在进行变量赋值的时候，再转换成包装类型：
        //
        //Boolean b = Boolean.valueOf(maps == null ? false : ((Boolean)maps.get("Hollis")).booleanValue());
        //所以，相比Java 8中多了一步自动拆箱，所以会导致NPE。
        //参考资料：
        //
        //《Java开发手册——泰山版》 http://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.25 http://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.25 https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.2 https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.12.2.7 https://docs.oracle.com/javase/specs/jls/se8/html/jls-18.html
    }


}
