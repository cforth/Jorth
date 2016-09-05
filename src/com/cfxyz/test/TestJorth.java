package com.cfxyz.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.cfxyz.vm.Jorth;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		// 初始化Forth虚拟机
//		InputStream input = new FileInputStream("F:/in.fs");
//		OutputStream output = new FileOutputStream("F:/out.fs");
//		Jorth vm = new Jorth(input, output);
		Jorth vm = new Jorth();
		// 从文件中加载标准库，这是完成Forth文本解释器的必要步骤
		vm.loadLib("src/lib.fs");
		vm.loadLib("src/test.fs");
		vm.interpret("INTERPRET"); //在lib.fs里面用Forth词定义了主循环，所以可以直接进入主循环了
	}
}
