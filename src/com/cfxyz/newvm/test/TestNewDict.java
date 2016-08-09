package com.cfxyz.newvm.test;

import com.cfxyz.newvm.Space;
import com.cfxyz.newvm.util.DictUtil;
import com.cfxyz.newvm.util.WordUtil;

public class TestNewDict {

	public static void main(String[] args) {
		//�������������ڴ�ռ�
		Space space = new Space() ;

		//�ڴʵ���д��
		DictUtil.addWord(-1, "NORMAL", "REVEAL", "END", "CORE", new int[]{DictUtil.END}, space);
		DictUtil.addWord(space.last, "NORMAL", "REVEAL", "ADD", "CORE", new int[]{DictUtil.ADD}, space);
		DictUtil.addWord(space.last, "NORMAL", "REVEAL", "ADD2", "COLON", new int[]{DictUtil.ADD,DictUtil.ADD,DictUtil.END}, space);
		
		//������ջ��д��
		space.paramStack.push(1);
		space.paramStack.push(2);
		space.paramStack.push(3);
		space.paramStack.push(4);
		space.paramStack.push(5);
		space.paramStack.push(6);
		
		//�ھ�̬��������д��ʵ�LFA��ַ
		space.memory[space.soureP++] = DictUtil.ADD;
		space.memory[space.soureP++] = 1018; //ADD2��LFA��ַ
		space.memory[space.soureP++] = 1018; //ADD2��LFA��ַ
		
		//��ӡ��������Ŀ���
		System.out.println(space);
		DictUtil.listWord(space);
		
		//ִ�о�̬����
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
