package com.cfxyz.newvm.util;

import com.cfxyz.newvm.Vm;

public final class WordUtil {
	public static void createLFA(int lfa, Vm space) {
		space.setValue(lfa);
		space.here ++ ;
	}
	public static void createNFA(int precedenceBit, int smudgeBit, int nameLength, String name, Vm space) {
		space.setValue(precedenceBit);
		space.here ++ ;
		space.setValue(smudgeBit);
		space.here ++ ;
		space.setValue(nameLength);
		space.here ++ ;
		char[] nameChar = name.toCharArray();
		for(char c : nameChar) {
			space.setValue((int)c);
			space.here ++ ;
		}
	}
	public static void createCFA(int cfa, Vm space) {
		space.setValue(cfa);
		space.here ++;
	}
	public static void createPFA(int[] pfa, Vm space) {
		for(int p : pfa) {
			space.setValue(p);
			space.here ++ ;
		}
	}
	public static void coreWordHandle(int pfaAddr, Vm space) {
		//���Ĵʴ������ģ��ֱ�����л�����
		int symbol = space.memory[pfaAddr] ;
		if(symbol == DictUtil.END) { // END�Ĵ�ͷ��ַ
			space.ip = space.returnStack.pop(); 
		} else if(symbol == DictUtil.ADD) { // ADD�Ĵ�ͷ��ַ
			space.paramStack.push(space.paramStack.pop() + space.paramStack.pop()) ;
		} else {
			System.out.println("�޴˺��Ĵʣ�");
		}
	}
	public static void colonWordHandle(int pfaAddr, Vm space) {
		//��չ�ʴ������
		space.returnStack.push(space.ip);
		space.ip = pfaAddr-1 ;
	}
	public static void wordHandle(int cfaAddr, Vm space) {
		System.out.println();
		System.out.println(space);
		int cfa = space.memory[cfaAddr];
		if(cfa == 1) {
			coreWordHandle(cfaAddr+1, space);
		} else if(cfa == 2) {
			colonWordHandle(cfaAddr+1, space);
		}
	}
}
