package com.cfxyz.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.cfxyz.interpreter.Interpreter;
import com.cfxyz.vm.VirtualMachine;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		//��ʼ����������ı�������
		VirtualMachine vm = new VirtualMachine() ;
		Interpreter jorth = new Interpreter(vm) ;
		loadLib(jorth, "lib.fs") ;
 
		
		//��Դ��������󽻸������ִ��
		jorth.parse("1 1 +") ;
		jorth.parse("			: add1      1 	+ ;") ; //���Կհ��ַ�
		jorth.parse("add1") ;
		jorth.parse(": add2 add1 add1 ;") ;
		jorth.parse("add2") ;
		jorth.parse(".") ;
		jorth.parse("1 2 3 *(@&#*$( ") ; //���Գ���
		jorth.parse(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;") ;
		jorth.parse(": tt DO .s LOOP ;");
		jorth.parse("1 2 3 TRUE XXX") ;
		jorth.parse("7 8 9 10 FALSE XXX") ;
		jorth.parse(".") ;
		jorth.parse(": YYY BEGIN .s TRUE UNTIL ;") ;
		jorth.parse("3 YYY");  //��ٱ�־ΪFALSEʱ����ѭ��
		jorth.parse("VARIABLE ZZ 555 ZZ ! ZZ @");  // ���Ա�����Ӧ��ջ������555
		jorth.parse(": average DUP >R 1 DO + LOOP R> / ;");
		jorth.parse("WORDS");
		jorth.parse("MAIN_LOOP");
	}
	
	public static void loadLib(Interpreter jorth, String filePath) {
		
		try {
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),encoding);//���ǵ������ʽ
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	jorth.parse(lineTxt);
                }
                read.close();
		    }else{
		        System.out.println("�Ҳ���ָ�����ļ�");
		    }
		} catch (Exception e) {
		    System.out.println("��ȡ�ļ����ݳ���");
		    e.printStackTrace();
		}
	}
}
