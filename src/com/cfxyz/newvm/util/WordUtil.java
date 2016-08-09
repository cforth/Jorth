package com.cfxyz.newvm.util;

import com.cfxyz.newvm.Space;

public final class WordUtil {
	public static void createLFA(int lfa, Space space) {
		space.setValue(lfa);
		space.here ++ ;
	}
	public static void createNFA(int precedenceBit, int smudgeBit, int nameLength, String name, Space space) {
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
	public static void createCFA(int cfa, Space space) {
		space.setValue(cfa);
		space.here ++;
	}
	public static void createPFA(int[] pfa, Space space) {
		for(int p : pfa) {
			space.setValue(p);
			space.here ++ ;
		}
	}
	public static void coreWordHandle(int pfaAddr, Space space) {
		//���Ĵʴ������ģ��ֱ�����л�����
		int symbol = space.memory[pfaAddr] ;
		if(symbol == DictUtil.END) { // END�Ĵ�ͷ��ַ
			space.ip = space.returnStack.pop(); 
		} else if(symbol == DictUtil.ADD) { // ADD�Ĵ�ͷ��ַ
			space.paramStack.push(space.paramStack.pop() + space.paramStack.pop()) ;
		} else {
			System.out.println("�޴˺��Ĵʣ�");
		}
		space.ip++ ;
	}
	public static void colonWordHandle(int pfaAddr, Space space) {
		//��չ�ʴ������
		space.returnStack.push(space.ip);
		space.ip = pfaAddr ;
	}
	public static void executeWord(int lfa, Space space) {
		//ִ�дʵĲ���
	}
}
