package com.mb.tutorials;

public class ProgrammerQuestion {

	public static void main(String[] args) {
		String s = "024";
		int a = Integer.parseInt(s,8);
		int b = 0b11001;
		int c = 5;
		char ch = '\u0224';
		short sh = (short)65540;
		byte bt = (byte)260;
		
		if(a!=b) {
			a += a++ +c--;
		}
		else if(a>=b || b<c) {
			b -=a*b%c;
		}
		else {
			c /= a+b/c;
		}
		
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		System.out.println(ch);
		System.out.println(sh);
		System.out.println(bt);

	}

}
