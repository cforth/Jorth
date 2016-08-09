package com.cfxyz.newvm.test;

import com.cfxyz.newvm.Space;
import com.cfxyz.newvm.util.DictUtil;

public class TestNewDict {

	public static void main(String[] args) {
		Space space = new Space() ;

		DictUtil.addWord(-1, "NORMAL", "REVEAL", "END", "CORE", new int[]{DictUtil.END}, space);
		DictUtil.addWord(space.last, "NORMAL", "REVEAL", "ADD", "CORE", new int[]{DictUtil.ADD}, space);
		DictUtil.addWord(space.last, "NORMAL", "REVEAL", "ADD2", "COLON", new int[]{DictUtil.ADD,DictUtil.ADD,DictUtil.END}, space);
		
		System.out.println(space);
		DictUtil.listWord(space);
		
		space.paramStack.push(1);
		space.paramStack.push(2);
		space.memory[0] =  DictUtil.ADD;
		//Ö´ÐÐ¾²Ì¬´úÂë
	}

}
