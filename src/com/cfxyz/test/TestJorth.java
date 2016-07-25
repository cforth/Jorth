package com.cfxyz.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
		jorth.parse(": IF COMPILE ?BRANCH ?>MARK ; IMMEDIATE") ;
		jorth.parse(": ELSE COMPILE BRANCH  1 + ?>RESOLVE ?>MARK ; IMMEDIATE") ;
		jorth.parse(": THEN ?>RESOLVE ; IMMEDIATE") ;
		jorth.parse(": BEGIN ?<MARK  COMPILE DUP COMPILE >R ; IMMEDIATE") ;
		jorth.parse(": UNTIL COMPILE R> COMPILE ?BRANCH ?<RESOLVE COMPILE DROP ; IMMEDIATE") ;
		jorth.parse(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;") ;
		jorth.parse("1 2 3 TRUE XXX") ;
		jorth.parse("7 8 9 10 FALSE XXX") ;
		jorth.parse(".") ;
		jorth.parse(": YYY BEGIN .s R> . 1 >R UNTIL ;") ;
//		jorth.parse(": YYY BEGIN .s UNTIL ;") ;
		jorth.parse("3 FALSE YYY");  //��ٱ�־ΪFALSEʱ����ѭ��
		jorth.parse("VARIABLE ZZ 555 ZZ ! ZZ @");  // ���Ա�����Ӧ��ջ������555
		jorth.getVm().printDict();
		
		
		// ���Դӱ�׼�����ȡ����
		BufferedReader localReader = new BufferedReader(
                new InputStreamReader(System.in));
		String source = null;
		while ((source = localReader.readLine()) != null) {
			jorth.parse(source) ;
		}
	}
}
