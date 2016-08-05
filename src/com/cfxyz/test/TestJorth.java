package com.cfxyz.test;

import com.cfxyz.vm.Jorth;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		// 初始化Forth虚拟机
		Jorth vm = new Jorth();
		// 从磁盘加载标准库文件中的冒号词，这是完成Forth文本解释器的必要步骤
		vm.loadLib("lib.fs");

		// 将源代码解析后交给虚拟机执行
		vm.interpret("1 1 +");
		vm.interpret("			: add1      1 	+ ;"); // 测试空白字符
		vm.interpret("add1");
		vm.interpret(": add2 add1 add1 ;");
		vm.interpret("add2");
		vm.interpret(".");
		vm.interpret("1 2 3 *(@&#*$( "); // 测试出错
		vm.interpret(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;");
		vm.interpret(": tt DO .s LOOP ;");
		vm.interpret("1 2 3 TRUE XXX");
		vm.interpret("7 8 9 10 FALSE XXX");
		vm.interpret(".");
		vm.interpret(": YYY BEGIN .s TRUE UNTIL ;");
		vm.interpret("3 YYY"); // 真假标志为FALSE时无限循环
		vm.interpret("VARIABLE ZZ 555 ZZ ! ZZ @"); // 测试变量，应在栈上留下555
		vm.interpret(": average DUP >R 1 DO + LOOP R> / ;");
		vm.interpret("WORDS");
		vm.interpret("INTERPRET");
	}
}
