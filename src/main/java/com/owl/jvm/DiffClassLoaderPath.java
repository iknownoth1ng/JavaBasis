package com.owl.jvm;

/**
 * 不同级别的类加载器加载的路径
 *
 * @author by 15515
 * @Date 2021/5/4 19:31
 **/
public class DiffClassLoaderPath {
    public static void main(String[] args) {
        bootClassLoaderLoadingPath();
        extraClassLoaderLoadingPath();
        appClassLoaderLoadingPath();
    }

    public static void bootClassLoaderLoadingPath() {
        //获取启动器加载器的加载路径
        String bootStrapLoadingPath = System.getProperty("sun.boot.class.path");
        String[] split = bootStrapLoadingPath.split(";");
        for (String s : split) {
            System.out.println("启动器加载器-----的加载路径：" + s);
        }
    }

    public static void extraClassLoaderLoadingPath() {
        //获取扩展类加载器的加载路径
        String extraLoadingPath = System.getProperty("java.ext.dirs");
        String[] split = extraLoadingPath.split(";");
        for (String s : split) {
            System.out.println("扩展类加载器-----的加载路径：" + s);
        }
    }

    public static void appClassLoaderLoadingPath() {
        //获取启动器加载器的加载路径
        String appLoadingPath = System.getProperty("java.class.path");
        String[] split = appLoadingPath.split(";");
        for (String s : split) {
            System.out.println("应用类加载器-----的加载路径：" + s);
        }
    }

}
