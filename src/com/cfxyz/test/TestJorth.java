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
		vm.loadLib("src/test.fs");
		vm.interpret("INTERPRET"); //��lib.fs������Forth�ʶ�������ѭ�������Կ���ֱ�ӽ�����ѭ����
	}
}
