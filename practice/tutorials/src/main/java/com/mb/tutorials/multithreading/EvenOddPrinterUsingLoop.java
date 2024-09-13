package com.mb.tutorials.multithreading;

public class EvenOddPrinterUsingLoop {
	
	static int counter = 1;
	
	
	public static void main(String[] args) throws InterruptedException {
		
		
		Thread evenThread = new Thread(() -> printEven());
		Thread oddThread =  new Thread(() -> printOdd());
		
		for(int i = 1 ;i<=10;i++) {
			
			evenThread.start();
			oddThread.start();
			counter++;
			
		}
		evenThread.join();
		oddThread.join();
		
	}

	private static void printEven() {
		if(counter % 2 ==0) {
			System.out.println("Printed by even thread: "+counter);
		}
	}
	
	private static void printOdd() {
		if(counter %2 != 0) {
			System.out.println("Printed by odd thread:" +counter);
		}
	}
	

}
