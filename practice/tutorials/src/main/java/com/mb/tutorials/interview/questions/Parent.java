package com.mb.tutorials.interview.questions;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

public class Parent {

	private void foo() {
		System.out.println("Parnet foo");
	}
	
	public static void main(String[] args) {
		Set treeSet = new TreeSet();
		
		//treeSet.add("1");
		//treeSet.add(Integer.parseInt("2"));
		//treeSet.add(3);
		
		int a = 5;
		
		int b = a++;
		
		System.out.println("a:"+a+" b:"+b);
		
		float f1= 1F;
		//float f2= 1.0;
		//float f3="17";
		//float f4= 1.0d;
		
		int[] arr = {120,200,016};
		
		printn(arr);
		
		File f = new File("abc.txt");
		
		String str = "Hello";
		str = str+"World";
		str= str.substring(5, 10);
		System.out.println(str);
		
		System.out.println(treeSet);
	}
	
	public static void printn(int[] a) {
		for(int n: a) {
			System.out.print(n+" ");
		}
	}
}

class Clild extends Parent{
	
	public void foo() {
		System.out.println("Clild foo");
	}
}

class ParentClildTest{
	
	public static void main(String[] args) {
		
		Clild obj1 = new Clild();
		obj1.foo();
		
		Parent obj2 = new Clild();
		//obj2.foo();
		
	}
	
}