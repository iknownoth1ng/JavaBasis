package com.owl.jvm;

/**
 * JMM之关键字volatile（可见性和有序性）
 *
 * @author by 15515
 * @Date 2021/5/3 21:12
 **/
public class VolatileVisibilityTest {
    private static  Boolean initFlag = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("waiting data...");
                while (!initFlag) {

                }
                System.out.println("==============success");
            }
        }).start();
        Thread.sleep(2000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareData();
            }
        }).start();
    }

    public static void prepareData() {
        System.out.println("prepare data...");
        initFlag = true;
        System.out.println("prepare data end...");
    }

}
