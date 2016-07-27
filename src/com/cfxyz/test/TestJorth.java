package com.cfxyz.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.cfxyz.interpreter.Interpreter;
import com.cfxyz.vm.VirtualMachine;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		//初始化虚拟机和文本解释器
		VirtualMachine vm = new VirtualMachine() ;
		Interpreter jorth = new Interpreter(vm) ;
		loadLib(jorth, "lib.fs") ;
 
		
		//将源代码解析后交给虚拟机执行
		jorth.parse("1 1 +") ;
		jorth.parse("			: add1      1 	+ ;") ; //测试空白字符
		jorth.parse("add1") ;
		jorth.parse(": add2 add1 add1 ;") ;
		jorth.parse("add2") ;
		jorth.parse(".") ;
		jorth.parse("1 2 3 *(@&#*$( ") ; //测试出错
		jorth.parse(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;") ;
		jorth.parse(": tt DO .s LOOP ;");
		jorth.parse("1 2 3 TRUE XXX") ;
		jorth.parse("7 8 9 10 FALSE XXX") ;
		jorth.parse(".") ;
		jorth.parse(": YYY BEGIN .s TRUE UNTIL ;") ;
		jorth.parse("3 YYY");  //真假标志为FALSE时无限循环
		jorth.parse("VARIABLE ZZ 555 ZZ ! ZZ @");  // 测试变量，应在栈上留下555
		jorth.parse(": average DUP >R 1 DO + LOOP R> / ;");
		jorth.parse("WORDS");
		jorth.parse("MAIN_LOOP");
	}
	
	public static void loadLib(Interpreter jorth, String filePath) {
		
		try {
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	jorth.parse(lineTxt);
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
