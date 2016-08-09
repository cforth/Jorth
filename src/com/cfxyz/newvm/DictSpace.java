package com.cfxyz.newvm;

import java.util.Arrays;

public class DictSpace {
	public int here; //�ʵ��пհ�λ�Ŀ�ʼ��ַ
	public int last; //�ʵ�������һ���ʵĴ�ͷ��ַ
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
		return "�ʵ�ָ�� = " + this.here + "\n��ͷ��ַ = " + this.last + "\n�ʵ��ڴ� = " + Arrays.toString(this.space);
	}
}
