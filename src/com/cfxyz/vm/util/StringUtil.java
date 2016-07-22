package com.cfxyz.vm.util;

/**
 * 验证字符串的合法性
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
