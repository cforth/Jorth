package com.cfxyz.interpreter;

import java.util.ArrayList;
import java.util.List;

import com.cfxyz.vm.VirtualMachine;
import com.cfxyz.vm.word.Word;

public class Interpreter {
	private VirtualMachine vm ;
	
	public Interpreter(VirtualMachine vm) {
		this.vm = vm ;
	}

	public void parse(String str) {
		System.out.println("\n*****开始执行啦*****");
		System.out.println("【执行语句】" + str);
		
		String [] source = str.trim().split("\\s+") ;
		List<Word> ipList = new ArrayList<Word>() ; //将解析后的代码存放在代码区中的，供IP指针操作
		//文本解释器分离出一行源代码中的每个词，建立一个Word列表
		for(String s : source) {
			Word w = this.vm.getDict().findName(s) ;
			if(w != null) {
				ipList.add(w) ;
			} else {  //如果词典中没有该词，则新建一个临时词来存放
				ipList.add(new Word(s));
			}
		}
		ipList.add(this.vm.getDict().findName("END")); 
		//将Word列表交给虚拟机去执行
		if("ok".equals(this.vm.run(ipList))) {
			this.vm.printStack();
			System.out.println("*****执行完啦*****\n");
		} else {
			this.vm.getParamStack().clear();
			this.vm.getReturnStack().clear();
			this.vm.getColonBuffer().clear();
			this.vm.printStack();
			System.out.println("*****执行出错，三清！*****\n");
		}
	}
	
	public VirtualMachine getVm() {
		return vm;
	}

}
