package com.cfxyz.newvm;

import java.util.Arrays;

public class DictSpace {
	public int here; //词典中空白位的开始地址
	public int last; //词典中最新一个词的词头地址
	public int[] space;

	public DictSpace(int size) {
		this.space = new int[size];
		this.here = 0 ;
		this.last = 0 ;
	}

	public void setValue(int value) {
		this.space[this.here] = value ;
	}

	@Override
	public String toString() {
		return "词典指针 = " + this.here + "\n词头地址 = " + this.last + "\n词典内存 = " + Arrays.toString(this.space);
	}
}
