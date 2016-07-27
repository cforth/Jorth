package com.cfxyz.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.cfxyz.vm.Jorth;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		//��ʼ��Forth�����
		Jorth vm = new Jorth() ;
		//�Ӵ��̼��ر�׼���ļ��е�ð�Ŵʣ��������Forth�ı��������ı�Ҫ����
		loadLib(vm, "lib.fs") ;
 
		
		//��Դ��������󽻸������ִ��
		vm.interpret("1 1 +") ;
		vm.interpret("			: add1      1 	+ ;") ; //���Կհ��ַ�
		vm.interpret("add1") ;
		vm.interpret(": add2 add1 add1 ;") ;
		vm.interpret("add2") ;
		vm.interpret(".") ;
		vm.interpret("1 2 3 *(@&#*$( ") ; //���Գ���
		vm.interpret(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;") ;
		vm.interpret(": tt DO .s LOOP ;");
		vm.interpret("1 2 3 TRUE XXX") ;
		vm.interpret("7 8 9 10 FALSE XXX") ;
		vm.interpret(".") ;
		vm.interpret(": YYY BEGIN .s TRUE UNTIL ;") ;
		vm.interpret("3 YYY");  //��ٱ�־ΪFALSEʱ����ѭ��
		vm.interpret("VARIABLE ZZ 555 ZZ ! ZZ @");  // ���Ա�����Ӧ��ջ������555
		vm.interpret(": average DUP >R 1 DO + LOOP R> / ;");
		vm.interpret("WORDS");
		vm.interpret("INTERPRET");
	}
	
	public static void loadLib(Jorth vm, String filePath) {
		
		try {
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),encoding);//���ǵ������ʽ
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	vm.interpret(lineTxt);
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
