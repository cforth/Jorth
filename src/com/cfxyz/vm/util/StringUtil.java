package com.cfxyz.vm.util;

/**
 * ��֤�ַ����ĺϷ���
 *
 */
public final class StringUtil {
	private StringUtil () {
	}
	
	public static boolean validateInteger(String str) {
		if(str.matches("-?\\d+")) {
			return true ;
		} else {
			return false ;
		}
	}
}
