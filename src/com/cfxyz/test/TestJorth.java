package com.cfxyz.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.cfxyz.vm.VirtualMachine;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		//初始化虚拟机和文本解释器
		VirtualMachine vm = new VirtualMachine() ;
		loadLib(vm, "lib.fs") ;
 
		
		//将源代码解析后交给虚拟机执行
		vm.parse("1 1 +") ;
		vm.parse("			: add1      1 	+ ;") ; //测试空白字符
		vm.parse("add1") ;
		vm.parse(": add2 add1 add1 ;") ;
		vm.parse("add2") ;
		vm.parse(".") ;
		vm.parse("1 2 3 *(@&#*$( ") ; //测试出错
		vm.parse(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;") ;
		vm.parse(": tt DO .s LOOP ;");
		vm.parse("1 2 3 TRUE XXX") ;
		vm.parse("7 8 9 10 FALSE XXX") ;
		vm.parse(".") ;
		vm.parse(": YYY BEGIN .s TRUE UNTIL ;") ;
		vm.parse("3 YYY");  //真假标志为FALSE时无限循环
		vm.parse("VARIABLE ZZ 555 ZZ ! ZZ @");  // 测试变量，应在栈上留下555
		vm.parse(": average DUP >R 1 DO + LOOP R> / ;");
		vm.parse("WORDS");
		vm.parse("MAIN_LOOP");
	}
	
	public static void loadLib(VirtualMachine vm, String filePath) {
		
		try {
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	vm.parse(lineTxt);
                }
                read.close();
		    }else{
		        System.out.println("找不到指定的文件");
		    }
		} catch (Exception e) {
		    System.out.println("读取文件内容出错");
		    e.printStackTrace();
		}
	}
}
