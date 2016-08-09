package com.cfxyz.newvm;

public final class DictSpaceUtil {
	public static void createLFA(int lfa, DictSpace dict) {
		dict.setValue(lfa);
		dict.here ++ ;
	}
	public static void createNFA(int precedenceBit, int smudgeBit, int nameLength, String name, DictSpace dict) {
		dict.setValue(precedenceBit);
		dict.here ++ ;
		dict.setValue(smudgeBit);
		dict.here ++ ;
		dict.setValue(nameLength);
		dict.here ++ ;
		char[] nameChar = name.toCharArray();
		for(char c : nameChar) {
			dict.setValue((int)c);
			dict.here ++ ;
		}
	}
	public static void createCFA(int cfa, DictSpace dict) {
		dict.setValue(cfa);
		dict.here ++;
	}
	public static void createPFA(int[] pfa, DictSpace dict) {
		for(int p : pfa) {
			dict.setValue(p);
			dict.here ++ ;
		}
	}
}
