package com.cfxyz.newvm.test;

import com.cfxyz.newvm.DictSpace;
import com.cfxyz.newvm.NewDict;
import com.cfxyz.newvm.NewWord;

public class TestNewDict {

	public static void main(String[] args) {
		NewDict dict = new NewDict(new DictSpace(100));
		dict.addWord(-1, "NORMAL", "REVEAL", "ADD", "CORE", new int[]{1,2,999});
		dict.addWord(dict.dictSpace.last, "NORMAL", "REVEAL", "MUL", "CORE", new int[]{3,4,999});
		dict.addWord(dict.dictSpace.last, "NORMAL", "REVEAL", "DIV", "CORE", new int[]{5,6,999});
		dict.addWord(dict.dictSpace.last, "NORMAL", "REVEAL", "DUP", "CORE", new int[]{7,8,999});
		dict.addWord2(new NewWord(dict.dictSpace.last, "NORMAL", "REVEAL", "DROP", "CORE", new int[]{9,10,11,999}));
		
		System.out.println(dict);
		dict.listWord();
	}

}
