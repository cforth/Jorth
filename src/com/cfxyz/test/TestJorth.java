package com.cfxyz.test;

import com.cfxyz.interpreter.Interpreter;
import com.cfxyz.vm.VirtualMachine;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		//��ʼ����������ı�������
		VirtualMachine vm = new VirtualMachine() ;
		Interpreter jorth = new Interpreter(vm) ;
		
		//��Դ��������󽻸������ִ��
		jorth.parse("1 1 +") ;
		jorth.parse("			: add1      1 	+ ;") ; //���Կհ��ַ�
		jorth.parse("add1") ;
		jorth.parse(": add2 add1 add1 ;") ;
		jorth.parse("add2") ;
		jorth.parse(".") ;
		jorth.parse("1 2 3 *(@&#*$( ") ; //���Գ���
		jorth.parse(": TRUE 1 ;") ;
		jorth.parse(": FALSE 0 ;") ;
		jorth.parse(": 2DUP OVER OVER ;") ;
		jorth.parse(": IF COMPILE ?BRANCH ?>MARK ; IMMEDIATE") ;
		jorth.parse(": ELSE COMPILE BRANCH  1 + ?>RESOLVE ?>MARK ; IMMEDIATE") ;
		jorth.parse(": THEN ?>RESOLVE ; IMMEDIATE") ;
		jorth.parse(": BEGIN ?<MARK ; IMMEDIATE") ;
		jorth.parse(": UNTIL COMPILE ?BRANCH ?<RESOLVE ; IMMEDIATE") ;
		jorth.parse(": DO COMPILE 2DUP COMPILE > COMPILE ?BRANCH ?<MARK ?>MARK ; IMMEDIATE") ;
		jorth.parse(": LOOP COMPILE 1 COMPILE + COMPILE BRANCH 1 + ?>RESOLVE 3 - ?<RESOLVE COMPILE DROP COMPILE DROP ; IMMEDIATE") ;
		jorth.parse(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;") ;
		jorth.parse(": tt DO .s LOOP ;");
		jorth.parse("1 2 3 TRUE XXX") ;
		jorth.parse("7 8 9 10 FALSE XXX") ;
		jorth.parse(".") ;
		jorth.parse(": YYY BEGIN .s TRUE UNTIL ;") ;
		jorth.parse("3 YYY");  //��ٱ�־ΪFALSEʱ����ѭ��
		jorth.parse("VARIABLE ZZ 555 ZZ ! ZZ @");  // ���Ա�����Ӧ��ջ������555
		jorth.parse(": WORDS SIZE 0 DO DUP PRINTWORD LOOP DROP");
		jorth.parse("WORDS");
		jorth.parse(": MAIN_LOOP BEGIN READ INTERPRET FALSE  UNTIL ;");
		jorth.parse("WORDS");
		jorth.parse("MAIN_LOOP");

	}
}
