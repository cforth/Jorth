package com.cfxyz.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.cfxyz.vm.VirtualMachine;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		//��ʼ����������ı�������
		VirtualMachine vm = new VirtualMachine() ;
		loadLib(vm, "lib.fs") ;
 
		
		//��Դ��������󽻸������ִ��
		vm.parse("1 1 +") ;
		vm.parse("			: add1      1 	+ ;") ; //���Կհ��ַ�
		vm.parse("add1") ;
		vm.parse(": add2 add1 add1 ;") ;
		vm.parse("add2") ;
		vm.parse(".") ;
		vm.parse("1 2 3 *(@&#*$( ") ; //���Գ���
		vm.parse(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;") ;
		vm.parse(": tt DO .s LOOP ;");
		vm.parse("1 2 3 TRUE XXX") ;
		vm.parse("7 8 9 10 FALSE XXX") ;
		vm.parse(".") ;
		vm.parse(": YYY BEGIN .s TRUE UNTIL ;") ;
		vm.parse("3 YYY");  //��ٱ�־ΪFALSEʱ����ѭ��
		vm.parse("VARIABLE ZZ 555 ZZ ! ZZ @");  // ���Ա�����Ӧ��ջ������555
		vm.parse(": average DUP >R 1 DO + LOOP R> / ;");
		vm.parse("WORDS");
		vm.parse("MAIN_LOOP");
	}
	
	public static void loadLib(VirtualMachine vm, String filePath) {
		
		try {
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),encoding);//���ǵ������ʽ
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	vm.parse(lineTxt);
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
