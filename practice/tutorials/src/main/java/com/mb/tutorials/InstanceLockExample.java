package com.mb.tutorials;
public class InstanceLockExample {
    public synchronized void instanceMethod() {
        // Synchronized on this instance
        System.out.println(Thread.currentThread().getName() + " is in instanceMethod");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void instanceMethodWithBlock() {
        synchronized (this) {
            // Synchronized on this instance
            System.out.println(Thread.currentThread().getName() + " is in instanceMethodWithBlock");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        InstanceLockExample obj = new InstanceLockExample();

        Thread t1 = new Thread(() -> obj.instanceMethod(), "Thread-1");
        Thread t2 = new Thread(() -> obj.instanceMethodWithBlock(), "Thread-2");

        t1.start();
        t2.start();
    }
}
