package com.cfxyz.vm.util;

/**
 * 验证字符串的合法性
 *
 */
public final class VmUtil {
	private VmUtil () {
	}
	
	public static boolean validateInteger(String str) {
		if(str.matches("-?\\d+")) {
			return true ;
		} else {
			return false ;
		}
	}
}
