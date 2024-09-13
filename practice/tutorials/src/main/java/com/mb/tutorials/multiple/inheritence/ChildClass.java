package com.mb.tutorials.multiple.inheritence;

public class ChildClass implements BaseInterface1, BaseInterface2{

	@Override
	public void show() {
		System.out.println("In Child Class");
		BaseInterface1.super.show();
		BaseInterface2.super.show();
	}
	
	public static void main(String[] args) {
		
		ChildClass cc = new ChildClass();
		cc.show();
		
	}

}
