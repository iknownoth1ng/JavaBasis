导读
Java源代码被编译为Class文件之后，里面究竟保存了什么东西，有什么奥秘呢？本文将为你揭开Class文件神秘的面纱。Class文件结构是JVM加载Class，实例化对象，和进行方法调用的重要依据，了解了它，我们将能够更透彻的洞悉JVM执行字节码背后的机制：

运行时常量池和静态常量池有什么区别？
Class文件里面都有什么内容？
Class文件反汇编之后的格式里面分别有什么，尝试解读里面方法中的汇编指令
本地变量表和操作数栈是如何工作的
1、查看Class文件
如果想要探究Class文件十六进制数字背后的秘密，那就必须得翻出字节码来仔细研究一下了，看一下里面究竟是什么东西。这里提供一个查看字节码文件的命令：

1、以十六进制查看Class文件

技巧：vim + xxd = 十六进制编辑器

vim -b xxx.class 可以以二进制将class文件打开；

vim内调用：:%!xxd 以十六进制显示当前文件；

修改完成之后，如果想保存，则执行以下命令把十六进制转换回二进制：

:%!xxd -r

2、输出包括行号，本地变量反汇编等信息

javap

-v -verbose：输出附加信息(包括行号、本地变量表、反汇编等信息)
-c：对代码进行反汇编
如：

javap -c xxx.class

javap -verbose Test.class

更多关于javap的介绍：javap - The Java Class File Disassembler

关于反汇编：
反汇编(Disassembly)：把目标代码转为汇编代码的过程，也可以说是把机器语言转换为汇编语言代码、低级转高级的意思。软件一切神秘的运行机制全在反汇编代码里面。
– 来源：反汇编

2、Class文件解读
JVM规范中的Class文件解读
这一小节比较枯燥，大部分是文字描述，但是很重要，决定了我们能不能正确把class二进制文件给解析出来，所以还是需要先大致了解下。

JVM规范中 4.1. The ClassFile Structure 给我们提供了一下的Class文件结构：

ClassFile {
    u4             magic; // 魔数
    u2             minor_version; // 副版本号
    u2             major_version; // 主版本号
    u2             constant_pool_count; // 常量池计数器
    cp_info        constant_pool[constant_pool_count-1]; // 常量池数据区
    u2             access_flags; // 访问标志
    u2             this_class; // 类索引
    u2             super_class; // 父类索引
    u2             interfaces_count; // 接口计数器
    u2             interfaces[interfaces_count]; // 接口表
    u2             fields_count; // 字段计数器
    field_info     fields[fields_count]; // 字段表
    u2             methods_count; // 方法计数器
    method_info    methods[methods_count]; // 方法表
    u2             attributes_count; // 属性计数器
    attribute_info attributes[attributes_count]; // 属性表
}
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
Class文件是一组以8位字节为基础单位的二进制流。以上类结构只有两种数据类型：

无符号数：无符号数属于基本属性类型，用u1, u2, u4, u8分别代表1个字节，2个字节，4个字节和8个字节的无符号数，可以用它描述数字、索引引用、数量值或者utf8编码的字符串值；
表：由多个无符号数或者其他表作为数据项构成的复合数据类型，以命名_info结尾。
根据以上的Class文件结构，我们可以梳理出以下的Class文件结构图：



2.1、魔数 magic
用于标识这个文件的格式，Class文件格式的魔数为 0xCAFEBABE。

2.2、副版本号 minor_version，主版本号 major_version
minor_version和major_version项目的值是此类文件的次要版本号和主要版本号。 主版本号和次版本号共同决定了类文件格式的版本。 如果类文件的主版本号为M，次版本号为m，则将其类文件格式的版本表示为M.m。 因此，可以按字典顺序对类文件格式版本进行排序，例如1.5 <2.0 <2.1。

2.3、常量池计数器 constant_pool_count
常量池描述着整个Class文件中所有的字面量信息。常量池计数器(constant_pool_count)的值等于常量池(constant_pool)表中的条目数加一。

如果constant_pool索引大于零且小于constant_pool_count，则该索引被视为有效。

2.4、常量池表 constant_pool[]
constant_pool[]是一个结构表，表示各种字符串常量，类和接口名称，字段名称以及在ClassFile结构及其子结构中引用的其他常量。 每个constant_pool表条目的格式由其第一个“标签”字节指示。

所有类型的常量池表项目有以下通用的格式：

cp_info {
    u1 tag;
    u1 info[];
}
1
2
3
4
constant_pool表的索引从1到constant_pool_count-1。

常量池中的14种常量结构
参考JVM规范：4.4. The Constant Pool，得出常量池各种常量类型的结构：

常量名称	类型	项目	Description
CONSTANT_Class_info	u1	tag	7：类或接口的符号引用
u2	name_index	指向全限定名常量项的索引
CONSTANT_Fieldref_info	u1	tag	9：字段的符号引用
u2	class_index	指向声明字段的类或者接口描述符CONSTANT_Class_info的索引项
u2	name_and_type_index	指向字段描述符CONSTANT_NameAndType的索引项
CONSTANT_Methodref_info	u1	tag	10：类中方法的符号引用
u2	class_index	指向声明方法的类描述符CONSTANT_Class_info的索引项
u2	name_and_type_index	指向名称及类型描述符CONSTANT_NameAndType的索引项
CONSTANT_InterfaceMethodref_info	u1	tag	11：接口中方法的符号引用
u2	class_index	指向声明方法的接口描述符CONSTANT_Class_info的索引项
u2	name_and_type_index	指向名称及类型描述符CONSTANT_NameAndType的索引项
CONSTANT_String_info	u1	tag	8：字符串类型字面量
u2	string_index	指向字符串字面量的索引
CONSTANT_Integer_info	u1	tag	3：整型字面量
u4	bytes	按照高位在前存储的int值
CONSTANT_Float_info	u1	tag	4：浮点型字面量
u4	bytes	按照高位在前存储的float值
CONSTANT_Long_info	u1	tag	5：长整型字面量
u4	high_bytes	
u4	low_bytes	((long) high_bytes << 32) + low_bytes
CONSTANT_Double_info	u1	tag	6：双精度浮点型字面量
u4	high_bytes	
u4	low_bytes	
CONSTANT_NameAndType_info	u1	tag	12：字段或方法的部分符号引用
u2	name_index	指向该字段或方法名称常量项的索引
u2	descriptor_index	指向该字段或方法描述符常量项的索引
CONSTANT_Utf8_info	u1	tag	1：UTF-8编码的字符串
u2	length	UTF-8编码的字符串占用的字节数
u1	byte[length]	长度为length的UTF-8编码的字符串
CONSTANT_MethodHandle_info	u1	tag	15：表示方法句柄
u1	reference_kind	值必须在1到9的范围内。该值表示此方法句柄的类型，该句柄表征其字节码行为(§5.4.3.5)
u2	reference_index	值必须是对常量池的有效引用
CONSTANT_MethodType_info	u1	tag	16：表示方法类型
u2	descriptor_index	值必须是指向constant_pool表的有效索引。该索引处的constant_pool条目必须是代表方法描述符的CONSTANT_Utf8_info结构
CONSTANT_InvokeDynamic_info	u1	tag	18：表示一个动态方法调用点
u2	bootstrap_method_attr_index	值必须是此类文件的bootstrap方法表(§4.7.23)的bootstrap_methods数组的有效索引。
u2	name_and_type_index	值必须是指向constant_pool表的有效索引。该索引处的constant_pool条目必须是代表方法名称和方法描述符(§4.3.3)的CONSTANT_NameAndType_info结构(§4.4.6)。
2.5、访问标记 access_flags
access_flags是一种掩码标志，用于表示对该类或接口的访问权限。每个标志的解释如下：

标志名称	值	含义
ACC_PUBLIC	0x0001	标记为 public，可以被类外访问。
ACC_FINAL	0x0010	标记定义为 final，不允许有子类。
ACC_SUPER	0x0020	当调用到 invokespecial 指令时，需要特殊处理的父类方法。
ACC_INTERFACE	0x0200	是一个接口。
ACC_ABSTRACT	0x0400	是一个抽象类，不能够被实例化。
ACC_SYNTHETIC	0x1000	标记是由编译器产生的，不存在于源码中。
ACC_ANNOTATION	0x2000	标记为注解类型。
ACC_ENUM	0x4000	标记为枚举类型。
注意：

接口通过标记位ACC_INTERFACE来区分，如果不是这个标记位，则表示一个类，而非接口；
设置了ACC_INTERFACE标记位，那么ACC_ABSTRACT标记位也得设置，并且不得设置ACC_FINAL，ACC_SUPER以及ACC_ENUM；
如果设置的不是ACC_INTERFACE，那么除了ACC_ANNOTATION，其他都可以设置。不能同时使用ACC_FINAL和ACC_ABSTRACT标记；
目前的Java虚拟机指令集的编译器应设置ACC_SUPER标记。 在Java SE 8和更高版本中，Java虚拟机将在每个类文件中设置ACC_SUPER标记，而不管该标志在该类文件中的实际值和类文件的版本如何。ACC_SUPER标志是为了兼容旧版本编译器编译的代码。在JDK1.0.2之前编译生成Class没有ACC_SUPER标志，JDK1.0.2前的JVM遇到该标记将忽略它；
ACC_SYNTHETIC标记表明该类或者接口是由编译器编译产生，不存在与源码中；
注解类型必须设置ACC_ANNOTATION标记，具有ACC_ANNOTATION标记的同时，必须要有ACC_INTERFACE标记；
ACC_ENUM标记表明该类或其超类被声明为枚举类型。
2.6、类索引 this_class
类索引的值必须是constant_pool表中的有效索引。该索引处的constant_pool条目必须是CONSTANT_Class_info结构，该结构表示此类文件定义的类或接口。

2.7、父类索引 super_class
对于一个类，父类索引的值必须为零或必须是constant_pool表中的有效索引。 如果super_class项的值非零，则该索引处的constant_pool条目必须是CONSTANT_Class_info结构，该结构表示此类文件定义的类的直接超类。 直接超类或其任何超类都不能在其ClassFile结构的access_flags项中设置ACC_FINAL标志。

如果super_class项的值为零，则该类只可能是java.lang.Object，这是没有直接超类的唯一类或接口。

对于接口，父类索引的值必须始终是constant_pool表中的有效索引。该索引处的constant_pool条目必须是java.lang.Object的CONSTANT_Class_info结构。

2.8、接口计数器 interfaces_count
接口计数器表示当前类或接口类型的直接超接口的数量。

2.9、接口表 interfaces[]
接口表的每个值都必须是constant_pool表中的有效索引。interfaces [i]的每个值（其中0≤i <interfaces_count）上的constant_pool条目必须是CONSTANT_Class_info结构，该结构描述当前类或接口类型的直接超接口。

2.10、字段计数器 fields_count
字段计数器的值给出了fields表中field_info(§4.5)结构的数量。 field_info结构代表此类或接口类型声明的所有字段，包括类变量和实例变量。

2.11、字段表 fields[]
字段表中的每个值都必须是field_info结构(§4.5)，以提供对该类或接口中字段的完整描述。 字段表仅包含此类或接口声明的字段，不包含从超类或超接口继承的字段。

字段有如下结构：

field_info {
    u2             access_flags;
    u2             name_index;
    u2             descriptor_index;
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
1
2
3
4
5
6
7
2.12、方法计数器 methods_count
方法计数器的值表示方法表中method_info§4.6结构的数量。

2.13、方法表 methods[]
方法表中的每个值都必须是method_info§4.6结构，以提供对该类或接口中方法的完整描述。 如果在method_info结构的access_flags项中均未设置ACC_NATIVE和ACC_ABSTRACT标志，则还将提供实现该方法的Java虚拟机指令；

method_info结构表示此类或接口类型声明的所有方法，包括实例方法，类方法，实例初始化方法以及任何类或接口初始化的方法。 方法表不包含表示从超类或超接口继承的方法。

方法具有如下结构：

method_info {
    u2             access_flags;
    u2             name_index;
    u2             descriptor_index;
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
1
2
3
4
5
6
7
其中

2.14、属性计数器 attributes_count
属性计数器的值表示当前类的属性表中的属性数量。

2.15、属性表 attributes[]
注意，这里的属性并不是Java代码里面的类属性(类字段)，而是Java源文件便已有特有的一些属性，参考以下表格。

属性表的每个值都必须是attribute_info (§4.7)结构，属性的通用结构如下：

attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
1
2
3
4
5
不同的属性有不同的info[]。

在Java 8 规范中，ClassFile结构中的的属性表中的属性包括：

Attribute	Location	class file
SourceFile	ClassFile	45.3
InnerClasses	ClassFile	45.3
EnclosingMethod	ClassFile	49.0
SourceDebugExtension	ClassFile	49.0
BootstrapMethods	ClassFile	51.0
ConstantValue	field_info	45.3
Code	method_info	45.3
Exceptions	method_info	45.3
RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations	method_info	49.0
AnnotationDefault	method_info	49.0
MethodParameters	method_info	52.0
Synthetic	ClassFile, field_info, method_info	45.3
Deprecated	ClassFile, field_info, method_info	45.3
Signature	ClassFile, field_info, method_info	49.0
RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations	ClassFile, field_info, method_info	49.0
LineNumberTable	Code	45.3
LocalVariableTable	Code	45.3
LocalVariableTypeTable	Code	49.0
StackMapTable	Code	50.0
RuntimeVisibleTypeAnnotations, RuntimeInvisibleTypeAnnotations	ClassFile, field_info, method_info, Code	52.0
以下给出了有关定义为出现在ClassFile结构的属性表中的属性的规则：§4.7；

以下给出了有关ClassFile结构的属性表中非预定义属性的规则：§4.7.1。

3、解析Class文件实例
接下来给大家展示一下Class文件。有如下Java文件：

package com.itzhai.classes;

public class TestA implements TestIntf{

    private int a = 0;
    
    @Override
    public void init(String title) {
        String tmp = "test method";
        a = 1;
    }
    
    public int getA() {
        return a;
    }

}

interface TestIntf {

    void init(String title);

}
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
3.1、分析class十六进制文件
编译成Class文件之后，使用vim以十六进制打开：

00000000: cafe babe 0000 0034 001f 0a00 0500 1909  .......4........
00000010: 0004 001a 0800 1b07 001c 0700 1d07 001e  ................
00000020: 0100 0161 0100 0149 0100 063c 696e 6974  ...a...I...<init
00000030: 3e01 0003 2829 5601 0004 436f 6465 0100  >...()V...Code..
00000040: 0f4c 696e 654e 756d 6265 7254 6162 6c65  .LineNumberTable
00000050: 0100 124c 6f63 616c 5661 7269 6162 6c65  ...LocalVariable
00000060: 5461 626c 6501 0004 7468 6973 0100 1a4c  Table...this...L
00000070: 636f 6d2f 6974 7a68 6169 2f63 6c61 7373  com/itzhai/class
00000080: 6573 2f54 6573 7441 3b01 0004 696e 6974  es/TestA;...init
00000090: 0100 1528 4c6a 6176 612f 6c61 6e67 2f53  ...(Ljava/lang/S
000000a0: 7472 696e 673b 2956 0100 0574 6974 6c65  tring;)V...title
000000b0: 0100 124c 6a61 7661 2f6c 616e 672f 5374  ...Ljava/lang/St
000000c0: 7269 6e67 3b01 0003 746d 7001 0004 6765  ring;...tmp...ge
000000d0: 7441 0100 0328 2949 0100 0a53 6f75 7263  tA...()I...Sourc
000000e0: 6546 696c 6501 000a 5465 7374 412e 6a61  eFile...TestA.ja
000000f0: 7661 0c00 0900 0a0c 0007 0008 0100 0b74  va.............t
00000100: 6573 7420 6d65 7468 6f64 0100 1863 6f6d  est method...com
00000110: 2f69 747a 6861 692f 636c 6173 7365 732f  /itzhai/classes/
00000120: 5465 7374 4101 0010 6a61 7661 2f6c 616e  TestA...java/lan
00000130: 672f 4f62 6a65 6374 0100 1b63 6f6d 2f69  g/Object...com/i
00000140: 747a 6861 692f 636c 6173 7365 732f 5465  tzhai/classes/Te
00000150: 7374 496e 7466 0021 0004 0005 0001 0006  stIntf.!........
00000160: 0001 0002 0007 0008 0000 0003 0001 0009  ................
00000170: 000a 0001 000b 0000 0038 0002 0001 0000  .........8......
00000180: 000a 2ab7 0001 2a03 b500 02b1 0000 0002  ..*...*.........
00000190: 000c 0000 000a 0002 0000 0003 0004 0005  ................
000001a0: 000d 0000 000c 0001 0000 000a 000e 000f  ................
000001b0: 0000 0001 0010 0011 0001 000b 0000 004f  ...............O
000001c0: 0002 0003 0000 0009 1203 4d2a 04b5 0002  ..........M*....
000001d0: b100 0000 0200 0c00 0000 0e00 0300 0000  ................
000001e0: 0900 0300 0a00 0800 0b00 0d00 0000 2000  .............. .
000001f0: 0300 0000 0900 0e00 0f00 0000 0000 0900  ................
00000200: 1200 1300 0100 0300 0600 1400 1300 0200  ................
00000210: 0100 1500 1600 0100 0b00 0000 2f00 0100  ............/...
00000220: 0100 0000 052a b400 02ac 0000 0002 000c  .....*..........
00000230: 0000 0006 0001 0000 000e 000d 0000 000c  ................
00000240: 0001 0000 0005 000e 000f 0000 0001 0017  ................
00000250: 0000 0002 0018 0a                        .......
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
根据JVM规范的介绍，我们来解析下这个十六进制文件：





可以发现，十六进制文件分析后，得到的结果跟文章开头的Class文件结构图完全对的上。

3.2、反汇编Class字节码文件
使用javap -v输出附加信息，可以看到本地变量表，常量池，异常表，代码偏移量映射表等信息。

反汇编之后的内容，跟JVM中描述的Class规范中的概念基本一致，对照JVM Class文件规范，即可解读反汇编后的Class字节码文件。参考：Chapter 4. The class File Format

同时，为了解读以下字节码指令，您需要提前了解相关指令作用。

The Java Virtual Machine Instruction Set

Java bytecode instruction listings

Introduction to Java Bytecode

以下是反汇编Class字节码得到的内容：

Classfile /Users/arthinking/Dev/demos/spring-demo/target/classes/com/itzhai/classes/TestA.class
  Last modified Dec 29, 2019; size 598 bytes
  MD5 checksum b8a7dcaba9f5ddeb930aec319bc3ad16
  Compiled from "TestA.java"
public class com.itzhai.classes.TestA implements com.itzhai.classes.TestIntf
  // 副版本号
  minor version: 0
  // 主版本号
  major version: 52
  // 访问标记
  flags: ACC_PUBLIC, ACC_SUPER
// 常量池(类中所有的字面量信息都在这里)
Constant pool:
   #1 = Methodref          #5.#25         // java/lang/Object."<init>":()V
   #2 = Fieldref           #4.#26         // com/itzhai/classes/TestA.a:I
   #3 = String             #27            // test method
   #4 = Class              #28            // com/itzhai/classes/TestA
   #5 = Class              #29            // java/lang/Object
   #6 = Class              #30            // com/itzhai/classes/TestIntf
   #7 = Utf8               a
   #8 = Utf8               I
   #9 = Utf8               <init>
  #10 = Utf8               ()V
  #11 = Utf8               Code
  #12 = Utf8               LineNumberTable
  #13 = Utf8               LocalVariableTable
  #14 = Utf8               this
  #15 = Utf8               Lcom/itzhai/classes/TestA;
  #16 = Utf8               init
  #17 = Utf8               (Ljava/lang/String;)V
  #18 = Utf8               title
  #19 = Utf8               Ljava/lang/String;
  #20 = Utf8               tmp
  #21 = Utf8               getA
  #22 = Utf8               ()I
  #23 = Utf8               SourceFile
  #24 = Utf8               TestA.java
  #25 = NameAndType        #9:#10         // "<init>":()V
  #26 = NameAndType        #7:#8          // a:I
  #27 = Utf8               test method
  #28 = Utf8               com/itzhai/classes/TestA
  #29 = Utf8               java/lang/Object
  #30 = Utf8               com/itzhai/classes/TestIntf
{
  // 默认构造方法，完成类的初始化(成员变量初始化赋值等)
  public com.itzhai.classes.TestA();
    descriptor: ()V
    // 访问标记
    flags: ACC_PUBLIC
    // 方法的Code属性，格式参考：https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.3
    Code:
      // 操作数栈最大大小=2，本地变量数量=1
      stack=2, locals=1, args_size=1
         0: aload_0 // 从本地变量表中加载第0项，即下面本地变量表中的this，入栈
         1: invokespecial #1 // 出栈，调用Method java/lang/Object."<init>":()V 初始化对象
         4: aload_0 // this引用入栈
         5: iconst_0 // 将常量0压入到操作数栈
         6: putfield      #2 // Field a:I 将0取出，赋值给a
         9: return
      // 指令与代码行数的偏移对应关系，第一个数字问代码行数，第二个数字为上面Code中指令前面的数字
      LineNumberTable:
        line 3: 0
        line 5: 4
      // 本地变量表
      // start和length分表表示这个本地变量在字节码中的生命周期开始的字节码偏移量及其作用域范围覆盖的长度。两者结合起来就是这个本地变量在字节码中的作用域范围。slot就是这个变量在本地变量表中的槽位（槽位可复用），name就是变量名称，Signature是本地变量类型描述
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      10     0  this   Lcom/itzhai/classes/TestA;

  // 类中自定义的init方法
  public void init(java.lang.String);
    descriptor: (Ljava/lang/String;)V
    // 访问标记
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=3, args_size=2
         0: ldc           #3                  // String test method 将常量”test method“的在常量池中的索引压入栈
         2: astore_2  // 从栈中取出刚刚的索引，存储到本地变量表的tmp中
         3: aload_0 // this引用入栈
         4: iconst_1 // 数值1入栈
         5: putfield      #2                  // Field a:I  数值1出栈，赋值给 常量池#2，这里是一个Fieldref，对应代码中的a变量
         8: return
      LineNumberTable:
        line 9: 0
        line 10: 3
        line 11: 8
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  this   Lcom/itzhai/classes/TestA;
            0       9     1 title   Ljava/lang/String;
            3       6     2   tmp   Ljava/lang/String;

  public int getA();
    descriptor: ()I
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0 // this引用入栈
         1: getfield      #2                  // Field a:I 从常量池#2中取值，这里是变量a
         4: ireturn // 返回整型结果，即a的值
      LineNumberTable:
        line 14: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lcom/itzhai/classes/TestA;
}
SourceFile: "TestA.java"
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
95
96
97
98
99
100
101
102
103
104
105
106
107
4、JVM堆栈工作原理
有了以上案例之后，我们现在来梳理下JVM堆栈的工作原理。

在Java运行时数据区域如何工作这边文章中，我们已经大致了解了Java运行数据区的工作原理，并提供了下图：



下面我们把这个图放大，详细了解一下虚拟机栈帧中，本地变量表和操作数栈的工作原理图。

我们知道，每个方法调用都对应一个栈帧，现在我们把其中一个栈帧放大：



如上图，这里我们重点关注本地变量表和操作数栈的工作。

本地变量表：本地变量表长度编译期确定，一个本地变量(Slot)可以存32位以内的数据，可以保存类型为 int, short, reference, byte, char, floath和returnAddress的数据，两个本地变量可以保存类型为long和double的数据；
操作数栈：每个栈帧内部都包含一个称为操作数栈的后进先出栈，提供给方法计算过程使用。
以上面例子中的自定义的init方法为例，说明下本地变量表和操作数栈的工作原理：

@Override
    public void init(String title) {
        String tmp = "test method";
        a = 1;
    }
1
2
3
4
5
对应的汇编指令：

// 类中自定义的init方法
  public void init(java.lang.String);
    descriptor: (Ljava/lang/String;)V
    // 访问标记
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=3, args_size=2
         0: ldc           #3                  // String test method 将常量”test method“的在常量池中的索引压入栈
         2: astore_2  // 从栈中取出刚刚的索引，存储到本地变量表的tmp中
         3: aload_0 // this引用入栈
         4: iconst_1 // 数值1入栈
         5: putfield      #2                  // Field a:I  数值1出栈，赋值给 常量池#2，这里是一个Fieldref，对应代码中的a变量
         8: return
      LineNumberTable:
        line 9: 0
        line 10: 3
        line 11: 8
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  this   Lcom/itzhai/classes/TestA;
            0       9     1 title   Ljava/lang/String;
            3       6     2   tmp   Ljava/lang/String;
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
如上面的汇编指令，可以知道，操作数栈的最大大小为2，本地变量表的size=3，得出如下结构：



其中b本地变量第0项为TestA实例的地址在堆中的地址引用。

接下来解读下后续的指令操作。

0: ldc #3

从字符串常量池中获取”test method“的应用，并压入操作数栈中：



2: astore_2

从操作数栈中取出刚刚的索引，存储到本地变量表的tmp中：



3: aload_0

this引用入栈：



4: iconst_1

数值1入栈：



5: putfield #2

数值1和this引用出栈，把数值1赋值给this引用对应的实例的属性a。这里的#2是从常量池中获取到一个Fieldref，对应代码中的a变量。

8: return

方法返回void。

References
Java Virtual Machine Specification

Introduction to Java Bytecode

《深入理解Java虚拟机-JVM高级特性与最佳实践》

反汇编

javap - The Java Class File Disassembler

Introduction to Java Bytecode

本文为arthinking基于相关技术资料和官方文档撰写而成，确保内容的准确性，如果你发现了有何错漏之处，烦请高抬贵手帮忙指正，万分感激。

大家可以关注我的博客：itzhai.com 获取更多文章，我将持续更新后端相关技术，涉及JVM、Java基础、架构设计、网络编程、数据结构、数据库、算法、并发编程、分布式系统等相关内容。

如果您觉得读完本文有所收获的话，可以关注我的账号，或者点赞啥的。关注我的公众号，及时获取最新的文章。

本文作者： arthinking

博客链接： https://www.itzhai.com/jvm/the-secret-of-hexadecimal-class-file.html

Class文件十六进制背后的秘密

版权声明： 版权归作者所有，未经许可不得转载，侵权必究！联系作者请加公众号。
————————————————
版权声明：本文为CSDN博主「arthinking-itzhai」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/peng_zhanxuan/article/details/104329859