package com.owl.jvm;

/**
 * volatile不支持原子性
 *
 * @author by 15515
 * @Date 2021/5/3 23:22
 **/
public class VolatileAtomicTest {
    private static volatile int num = 0;

    public static void increase() {
        num++;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        increase();
                    }
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println(num);
    }
}
