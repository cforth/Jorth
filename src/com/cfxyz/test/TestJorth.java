package com.cfxyz.test;

import com.cfxyz.vm.VirtualMachine;

public class TestJorth {

	public static void main(String[] args) {
		//初始化虚拟机和语法解析器
		VirtualMachine vm = new VirtualMachine() ;
		
		//将源代码解析后交给虚拟机执行
		vm.parse("1 1 +") ;
		vm.parse("			: add1      1 	+ ;") ; //测试空白字符
		vm.parse("add1") ;
		vm.parse(": add2 add1 add1 ;") ;
		vm.parse("add2") ;
		vm.parse(".") ;
		vm.parse("1 2 3 *(@&#*$( ") ; //测试出错
		vm.parse(": TRUE 1 ;") ;
		vm.parse(": FALSE 0 ;") ;
		vm.parse(": IF COMPILE ?BRANCH ?>MARK ; IMMEDIATE") ;
		vm.parse(": ELSE COMPILE BRANCH  1 + ?>RESOLVE ?>MARK ; IMMEDIATE") ;
		vm.parse(": THEN ?>RESOLVE ; IMMEDIATE") ;
		vm.parse(": BEGIN ?<MARK  COMPILE DUP COMPILE >R ; IMMEDIATE") ;
		vm.parse(": UNTIL COMPILE R> COMPILE ?BRANCH ?<RESOLVE COMPILE DROP ; IMMEDIATE") ;
		vm.parse(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;") ;
		vm.parse("1 2 3 TRUE XXX") ;
		vm.parse("7 8 9 10 FALSE XXX") ;
		vm.parse(".") ;
		vm.parse(": YYY BEGIN .s R> . 1 >R UNTIL ;") ;
//		vm.parse(": YYY BEGIN .s UNTIL ;") ;
		vm.parse("3 FALSE YYY");  //真假标志为FALSE时无限循环
		vm.printDict();
	}
}
