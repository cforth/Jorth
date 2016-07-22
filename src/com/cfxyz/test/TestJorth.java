package com.cfxyz.test;

import com.cfxyz.vm.VirtualMachine;

public class TestJorth {

	public static void main(String[] args) {
		//��ʼ����������﷨������
		VirtualMachine vm = new VirtualMachine() ;
		
		//��Դ��������󽻸������ִ��
		vm.parse("1 1 +") ;
		vm.parse("			: add1      1 	+ ;") ; //���Կհ��ַ�
		vm.parse("add1") ;
		vm.parse(": add2 add1 add1 ;") ;
		vm.parse("add2") ;
		vm.parse(".") ;
		vm.parse("1 2 3 *(@&#*$( ") ; //���Գ���
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
		vm.parse("3 FALSE YYY");  //��ٱ�־ΪFALSEʱ����ѭ��
		vm.printDict();
	}
}
