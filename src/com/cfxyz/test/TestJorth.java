package com.cfxyz.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.cfxyz.vm.Jorth;

public class TestJorth {

	public static void main(String[] args) throws Exception {
		//初始化Forth虚拟机
		Jorth vm = new Jorth() ;
		//从磁盘加载标准库文件中的冒号词，这是完成Forth文本解释器的必要步骤
		loadLib(vm, "lib.fs") ;
 
		
		//将源代码解析后交给虚拟机执行
		vm.interpret("1 1 +") ;
		vm.interpret("			: add1      1 	+ ;") ; //测试空白字符
		vm.interpret("add1") ;
		vm.interpret(": add2 add1 add1 ;") ;
		vm.interpret("add2") ;
		vm.interpret(".") ;
		vm.interpret("1 2 3 *(@&#*$( ") ; //测试出错
		vm.interpret(": XXX IF + + ELSE + + + + THEN [ .s ] 1 - ;") ;
		vm.interpret(": tt DO .s LOOP ;");
		vm.interpret("1 2 3 TRUE XXX") ;
		vm.interpret("7 8 9 10 FALSE XXX") ;
		vm.interpret(".") ;
		vm.interpret(": YYY BEGIN .s TRUE UNTIL ;") ;
		vm.interpret("3 YYY");  //真假标志为FALSE时无限循环
		vm.interpret("VARIABLE ZZ 555 ZZ ! ZZ @");  // 测试变量，应在栈上留下555
		vm.interpret(": average DUP >R 1 DO + LOOP R> / ;");
		vm.interpret("WORDS");
		vm.interpret("INTERPRET");
	}
	
	public static void loadLib(Jorth vm, String filePath) {
		
		try {
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	vm.interpret(lineTxt);
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
