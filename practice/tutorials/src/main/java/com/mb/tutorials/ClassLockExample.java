package com.mb.tutorials;
public class ClassLockExample {
    public static synchronized void staticMethod() {
        // Synchronized on the ClassLockExample class
        System.out.println(Thread.currentThread().getName() + " is in staticMethod");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void staticMethodWithBlock() {
        synchronized (ClassLockExample.class) {
            // Synchronized on the ClassLockExample class
            System.out.println(Thread.currentThread().getName() + " is in staticMethodWithBlock");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> ClassLockExample.staticMethod(), "Thread-1");
        Thread t2 = new Thread(() -> ClassLockExample.staticMethodWithBlock(), "Thread-2");

        t1.start();
        t2.start();
    }
}
