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
		System.out.println("【执行语句】" + str);
		
		String [] source = str.trim().split("\\s+") ;
		List<Word> ipList = new ArrayList<Word>() ; //将解析后的代码存放在代码区中的，供IP指针操作
		//文本解释器分离出一行源代码中的每个词，建立一个Word列表
		for(String s : source) {
			Word w = this.vm.getDict().findByName(s) ;
			if(w != null) {
				ipList.add(w) ;
			} else {  //如果词典中没有该词，则新建一个临时词来存放
				ipList.add(new Word(s));
			}
		}
		ipList.add(this.vm.getDict().findByName("END")); 
		//将Word列表交给虚拟机去执行
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
