package com.mb.tutorials.multithreading;

import java.util.concurrent.atomic.AtomicInteger;

public class EvenOddPrinterWithMultiThreading {

	public static void main(String[] args) throws InterruptedException {
		
		SharedCounter sc = new SharedCounter();
		ShareCounterWithAtomic scAtomic = new ShareCounterWithAtomic();
		
		Thread oddThread = new OddThread(sc, "OddThread", 10);
		Thread evenThrad = new EvenThread(sc, "EvenThread", 10);
		
		oddThread.start();
		evenThrad.start();
		
		oddThread.join();
		evenThrad.join();	
	}
	
}


class SharedCounter {
	
	public static int counter = 1;
	
	public synchronized  void printOdd(){
		while(counter %2 ==0) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		if(counter %2 != 0) {
			System.out.println("Printed by :"+Thread.currentThread().getName()+", Counter Value: "+counter);
		}
		counter++;
		notifyAll();
	}
	
	public synchronized void printEven() {
		while(counter %2 != 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		if(counter %2 == 0) {
			System.out.println("Printed by :"+Thread.currentThread().getName()+", Counter Value: "+counter);
		}
		counter++;
		notifyAll();
	}
}

class OddThread extends Thread{
	
	private SharedCounter sc;
	
	private ShareCounterWithAtomic scAtomic;
	
	private int limit;
	
	public OddThread(SharedCounter sc, String name, int limit) {
		super(name);
		this.limit = limit;
		this.sc = sc;
	}
	
	public OddThread(ShareCounterWithAtomic scAtomic, String name, int limit) {
		super(name);
		this.limit = limit;
		this.scAtomic = scAtomic;
	}
	
	@Override
	public void run() {
		for(int i=1;i<=limit;i++) {
			sc.printOdd();
		}
	}
}

class EvenThread extends Thread{
	
	private SharedCounter sc;
	
	private ShareCounterWithAtomic scAtomic;
	
	private int limit;
	
	public EvenThread(SharedCounter sc, String name, int limit) {
		super(name);
		this.limit = limit;
		this.sc = sc;
	}
	
	public EvenThread(ShareCounterWithAtomic scAtomic, String name, int limit) {
		super(name);
		this.limit = limit;
		this.scAtomic = scAtomic;
	}
	
	@Override
	public void run() {
		for(int i=1;i<=limit;i++) {
			sc.printEven();
		}
	}
}

class ShareCounterWithAtomic {
	
	private AtomicInteger atomicInt = new AtomicInteger(1);
	
	public void printOdd(){
		if(atomicInt.get() %2 != 0) {
			System.out.println("Printed by :"+Thread.currentThread().getName()+", Counter Value: "+atomicInt.get());
			
		}
		atomicInt.incrementAndGet();
		
	}
	
	public void printEven() {
		if(atomicInt.get() %2 == 0) {
			System.out.println("Printed by :"+Thread.currentThread().getName()+", Counter Value: "+atomicInt.get());
		}
		atomicInt.incrementAndGet();
		
	}
	
	
	
	
}