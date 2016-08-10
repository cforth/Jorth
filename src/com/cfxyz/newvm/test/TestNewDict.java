package com.cfxyz.newvm.test;

import com.cfxyz.newvm.Vm;
import com.cfxyz.newvm.util.DictUtil;
import com.cfxyz.newvm.util.WordUtil;

public class TestNewDict {

	public static void main(String[] args) {
		//�������������ڴ�ռ�
		Vm vm = new Vm() ;

		//�ڴʵ���д��
		DictUtil.addWord(-1, "NORMAL", "REVEAL", "END", "CORE", new int[]{DictUtil.END}, vm);
		DictUtil.addWord(vm.last, "NORMAL", "REVEAL", "ADD", "CORE", new int[]{DictUtil.ADD}, vm);
		DictUtil.addWord(vm.last, "NORMAL", "REVEAL", "ADD2", "COLON", new int[]{DictUtil.ADD,DictUtil.ADD,DictUtil.END}, vm);
		
		//������ջ��д��
		vm.paramStack.push(1);
		vm.paramStack.push(2);
		vm.paramStack.push(3);
		vm.paramStack.push(4);
		vm.paramStack.push(5);
		vm.paramStack.push(6);
		
		//�ھ�̬��������д��ʵ�LFA��ַ
		vm.memory[vm.soureP++] = DictUtil.ADD;
		vm.memory[vm.soureP++] = 1018; //ADD2��LFA��ַ
		vm.memory[vm.soureP++] = 1018; //ADD2��LFA��ַ
		
		//��ӡ��������Ŀ���
		System.out.println(vm);
		DictUtil.listWord(vm);
		
		//ִ�о�̬����
		while(vm.ip != vm.soureP){
			int lfaAddr = vm.memory[vm.ip];
			int nameLenAddr = lfaAddr + 3;
			int nameLength = vm.memory[nameLenAddr];
			int cfaAddr = nameLenAddr + nameLength + 1;
			WordUtil.wordHandle(cfaAddr, vm);
			vm.ip ++ ;
		}
		System.out.println();
		System.out.println(vm);
	}

}
