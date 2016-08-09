package com.cfxyz.newvm.test;

import com.cfxyz.newvm.Space;
import com.cfxyz.newvm.util.DictUtil;
import com.cfxyz.newvm.util.WordUtil;

public class TestNewDict {

	public static void main(String[] args) {
		//给虚拟机分配好内存空间
		Space space = new Space() ;

		//在词典中写入
		DictUtil.addWord(-1, "NORMAL", "REVEAL", "END", "CORE", new int[]{DictUtil.END}, space);
		DictUtil.addWord(space.last, "NORMAL", "REVEAL", "ADD", "CORE", new int[]{DictUtil.ADD}, space);
		DictUtil.addWord(space.last, "NORMAL", "REVEAL", "ADD2", "COLON", new int[]{DictUtil.ADD,DictUtil.ADD,DictUtil.END}, space);
		
		//在算术栈中写入
		space.paramStack.push(1);
		space.paramStack.push(2);
		space.paramStack.push(3);
		space.paramStack.push(4);
		space.paramStack.push(5);
		space.paramStack.push(6);
		
		//在静态代码区中写入词的LFA地址
		space.memory[space.soureP++] = DictUtil.ADD;
		space.memory[space.soureP++] = 1018; //ADD2的LFA地址
		space.memory[space.soureP++] = 1018; //ADD2的LFA地址
		
		//打印出虚拟机的快照
		System.out.println(space);
		DictUtil.listWord(space);
		
		//执行静态代码
		while(space.ip != space.soureP){
			int lfaAddr = space.memory[space.ip];
			int nameLenAddr = lfaAddr + 3;
			int nameLength = space.memory[nameLenAddr];
			int cfaAddr = nameLenAddr + nameLength + 1;
			WordUtil.wordHandle(cfaAddr, space);
			space.ip ++ ;
		}
		System.out.println();
		System.out.println(space);
	}

}
