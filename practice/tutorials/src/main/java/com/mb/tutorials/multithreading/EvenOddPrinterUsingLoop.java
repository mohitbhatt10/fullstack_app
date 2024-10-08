package com.mb.tutorials.multithreading;

public class EvenOddPrinterUsingLoop {
	
	static volatile int counter = 1;
	
	
	public static void main(String[] args) throws InterruptedException {
		
		
		Thread evenThread = new Thread(() -> printSequence(),"evenThread");
		Thread oddThread =  new Thread(() -> printSequence(),"oddThread");
			
		evenThread.start();
		oddThread.start();
			
		evenThread.join();
		oddThread.join();
		
	}

	private static void printEven() {
		for(int i=1; i<=5;i++) {
			if(counter % 2 ==0) {
				System.out.println("Printed by even thread: "+counter);
			}
			counter++;
		}
	}
	
	private static void printOdd() {
		for(int i=1; i<=5;i++) {
			if(counter % 2 !=0) {
				System.out.println("Printed by odd thread: "+counter);
			}
			counter++;
		}
	}
	
	
	private synchronized static void printSequence() {
		for(int i = 0 ;i<5; i++) {
			if(counter % 2 ==0 ) {
				System.out.println("Printed by :"+ Thread.currentThread().getName()+" thread: "+counter);
			}else {
				System.out.println("Printed by :"+ Thread.currentThread().getName()+" thread: "+counter);
			}
			counter++;
		}
	}

}
