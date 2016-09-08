package com.cfxyz.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.cfxyz.vm.VirtualMachine;

public class Jorth {

	public static void main(String[] args) throws Exception {
		// 初始化Forth虚拟机
//		InputStream input = new FileInputStream("F:/in.fs");
//		OutputStream output = new FileOutputStream("F:/out.fs");
//		Jorth vm = new Jorth(input, output);
		VirtualMachine vm = new VirtualMachine();
		// 从文件中加载标准库，这是完成Forth文本解释器的必要步骤
		vm.load("LOADFILE lib.fs");
		vm.load("LOADFILE test.fs");
		vm.load("QUIT"); //在lib.fs里面用Forth词定义了主循环，所以可以直接进入主循环了
	}
}
