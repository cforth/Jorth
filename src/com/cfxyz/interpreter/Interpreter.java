package com.cfxyz.interpreter;

import java.util.ArrayList;
import java.util.List;

import com.cfxyz.vm.VirtualMachine;
import com.cfxyz.vm.Word;

public class Interpreter {
	private VirtualMachine vm ;
	
	public Interpreter(VirtualMachine vm) {
		this.vm = vm ;
	}

	public void parse(String str) {
		System.out.println("��ִ����䡿" + str);
		
		String [] source = str.trim().split("\\s+") ;
		List<Word> ipList = new ArrayList<Word>() ; //��������Ĵ������ڴ������еģ���IPָ�����
		//�ı������������һ��Դ�����е�ÿ���ʣ�����һ��Word�б�
		for(String s : source) {
			Word w = this.vm.getDict().findByName(s) ;
			if(w != null) {
				ipList.add(w) ;
			} else {  //����ʵ���û�иôʣ����½�һ����ʱ�������
				ipList.add(new Word(s));
			}
		}
		ipList.add(this.vm.getDict().findByName("END")); 
		//��Word�б��������ȥִ��
		if("ok".equals(this.vm.run(ipList))) {
			System.out.println("OK\n");
		} else {
			this.vm.getParamStack().clear();
			System.out.println("ERROR!\n");
		}
	}
	
	public VirtualMachine getVm() {
		return vm;
	}

}
