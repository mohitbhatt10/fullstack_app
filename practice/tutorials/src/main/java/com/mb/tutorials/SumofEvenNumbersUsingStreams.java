package com.mb.tutorials;

import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

public class SumofEvenNumbersUsingStreams {
	
	
	public static void main(String[] args) {
		
		
		IntBinaryOperator intBinOp = (a,b) -> a+b;
		
		int asInt = IntStream.rangeClosed(1, 10).filter(num -> num %2 ==0).reduce(intBinOp).getAsInt();
		
		System.out.println(asInt);
		
	}

}
