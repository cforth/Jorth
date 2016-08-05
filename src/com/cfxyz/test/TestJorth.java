package com.cfxyz.test;

import com.cfxyz.vm.Jorth;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		// ��ʼ��Forth�����
		Jorth vm = new Jorth();
		// �Ӵ��̼��ر�׼���ļ��е�ð�Ŵʣ��������Forth�ı��������ı�Ҫ����
		vm.loadLib("lib.fs");

		// ��Դ��������󽻸������ִ��
		vm.interpret("1 1 +");
		vm.interpret("			: add1      1 	+ ;"); // ���Կհ��ַ�
		vm.interpret("add1");
		vm.interpret(": add2 add1 add1 ;");
		vm.interpret("add2");
		vm.interpret(".");
		vm.interpret("1 2 3 *(@&#*$( "); // ���Գ���
		vm.interpret(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;");
		vm.interpret(": tt DO .s LOOP ;");
		vm.interpret("1 2 3 TRUE XXX");
		vm.interpret("7 8 9 10 FALSE XXX");
		vm.interpret(".");
		vm.interpret(": YYY BEGIN .s TRUE UNTIL ;");
		vm.interpret("3 YYY"); // ��ٱ�־ΪFALSEʱ����ѭ��
		vm.interpret("VARIABLE ZZ 555 ZZ ! ZZ @"); // ���Ա�����Ӧ��ջ������555
		vm.interpret(": average DUP >R 1 DO + LOOP R> / ;");
		vm.interpret("WORDS");
		vm.interpret("INTERPRET");
	}
}
