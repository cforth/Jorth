package com.cfxyz.newvm;

import java.util.Arrays;
import java.util.Stack;

public class Space {
	public int here; //�ʵ��пհ�λ�Ŀ�ʼ��ַ
	public int last; //�ʵ�������һ���ʵĴ�ͷ��ַ
	public int ip; //IPָ�룬ָ������ִ�еĴʵĵ�ַ
	public int[] memory; //��̬������+�ʵ���
	public int sourceSize = 1000; //��̬�����򳤶�
	public int dictSize = 1000 ; //�ʵ䳤��
	
	public Stack<Integer> returnStack; //����ջ
	public Stack<Integer> paramStack; //����ջ

	public Space() {
		this.here = sourceSize ; //�ʵ�ָ���ʼ��
		this.last = this.here ; //�ʵ�հ�λ��ʼ��ַָ��հ״ʵ�ͷ��
		this.ip = 0 ; //IPָ��ָ��̬������
		this.memory = new int[sourceSize + dictSize];
		this.returnStack = new Stack<Integer>();
		this.paramStack = new Stack<Integer>();
	}

	public void setValue(int value) {
		this.memory[this.here] = value ;
	}

	@Override
	public String toString() {
		int[] source = new int[this.sourceSize];
		for(int x = 0 ; x < this.sourceSize; x ++) {
			source[x] = this.memory[x] ;
		}
		int[] dict = new int[this.dictSize];
		for(int x = 0; x <this.dictSize; x++) {
			dict[x] = this.memory[this.sourceSize + x];
		}
		
		return "��̬���� = " + Arrays.toString(source)
			+ "\n�ʵ�ָ�� = " + this.here + "\n��ͷ��ַ = " + this.last + "\n�ʵ��ڴ� = " + Arrays.toString(dict);
	}
}
