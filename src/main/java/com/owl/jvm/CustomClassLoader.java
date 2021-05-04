package com.owl.jvm;

import java.io.*;

/**
 * 自定义加载器
 *
 * @author by 15515
 * @Date 2021/5/5 1:04
 **/
public class CustomClassLoader extends ClassLoader {
    /**
     * 定义加载文件的后缀名
     **/
    private final static String FILESUFFIXEXT = ".class";
    /**
     * 定义加载器名称
     **/
    private String classLoaderName;
    /**
     * 定义加载路径
     **/
    private String loadPath;

    /**
     * 设置加载路径
     **/
    public void setLoadPath(String loadPath) {
        this.loadPath = loadPath;
    }

    public CustomClassLoader(ClassLoader parent, String classLoaderName) {
        /**
         * 指定当前类加载器的父类加载器
         **/
        super(parent);
        this.classLoaderName = classLoaderName;
    }

    public CustomClassLoader(String classLoaderName) {
        /**
         * 默认使用AppClassLoader加载器为父类加载器
         **/
        super();
        this.classLoaderName = classLoaderName;
    }

    public CustomClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * Description: 将class文件转为字节
     *
     * @return byte[]
     * @Author: 15515
     * @Date: 2021/5/5 1:16
     * @Param [name] 类的名称
     **/
    private byte[] loadClassData(String name) {
        byte[] data = null;
        ByteArrayOutputStream baos = null;
        InputStream is = null;

        try {
            name = name.replace(".", "\\");
            String fileName = loadPath + name + FILESUFFIXEXT;
            File file = new File(fileName);
            is = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            int ch;
            while (-1 != (ch = is.read())) {
                baos.write(ch);
            }
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != baos) {
                    baos.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] data = loadClassData(name);
        System.out.println("CustomClassLoader 加载我们的类：===》" + name);
        return defineClass(name, data, 0, data.length);
    }
}
