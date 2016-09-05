package com.cfxyz.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.cfxyz.vm.Jorth;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		// ��ʼ��Forth�����
//		InputStream input = new FileInputStream("F:/in.fs");
//		OutputStream output = new FileOutputStream("F:/out.fs");
//		Jorth vm = new Jorth(input, output);
		Jorth vm = new Jorth();
		// ���ļ��м��ر�׼�⣬�������Forth�ı��������ı�Ҫ����
		vm.loadLib("src/lib.fs");


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
		vm.interpret("VARIABLE  ZZ 555 0 ZZ ! 0 ZZ @"); // ���Ա�����Ӧ��ջ������555�������ĳ�ʼƫ������0�������봫ͳforth��ͬ
		vm.interpret(": average DUP >R 1 DO + LOOP R> / ;");
		vm.interpret("CREATE weekrain 7 ALLOT"); // ��������
		vm.interpret("777 0 weekrain ! 888 1 weekrain !"); // ����д������
		vm.interpret("0 CONSTANT Monday"); // ������ƫ��������Ϊ����
		vm.interpret("1 CONSTANT Tuesday"); // ������ƫ��������Ϊ����
		vm.interpret("Monday weekrain @ Tuesday weekrain @ Tuesday 1+ weekrain @ .s"); // ���Զ�ȡ���飬��ջ������777 888 0
		vm.interpret("WORDS");
		vm.interpret("INTERPRET");
	}
}
