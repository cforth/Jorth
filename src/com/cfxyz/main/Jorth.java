package com.cfxyz.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.cfxyz.vm.VirtualMachine;

public class Jorth {

	public static void main(String[] args) throws Exception {
		// ��ʼ��Forth�����
//		InputStream input = new FileInputStream("F:/in.fs");
//		OutputStream output = new FileOutputStream("F:/out.fs");
//		Jorth vm = new Jorth(input, output);
		VirtualMachine vm = new VirtualMachine();
		// ���ļ��м��ر�׼�⣬�������Forth�ı��������ı�Ҫ����
		vm.load("LOADFILE lib.fs");
		vm.load("LOADFILE test.fs");
		vm.load("QUIT"); //��lib.fs������Forth�ʶ�������ѭ�������Կ���ֱ�ӽ�����ѭ����
	}
}
