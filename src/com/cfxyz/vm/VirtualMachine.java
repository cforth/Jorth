package com.cfxyz.vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.cfxyz.vm.util.VmUtil;
import com.cfxyz.vm.word.Dict;
import com.cfxyz.vm.word.Word;

public class VirtualMachine {
	private Stack<Integer> paramStack ;   //参数栈
	private Stack<Integer> returnStack ;  //返回栈
	private Dict dict ; //词典
	private List<Word> colonBuffer ; //冒号词定义时的缓存区
	private int ip;  //指向正在执行的词
	private String state ; //虚拟机运行状态
	
	public VirtualMachine() {
		this.paramStack = new Stack<Integer>() ;
		this.returnStack = new Stack<Integer>() ;
		this.colonBuffer = new ArrayList<Word>() ; 
		this.dict = new Dict();
		loadCoreWords(this.dict);
		this.state = "explain" ;
	}
	
	public void parse(String str) {
		System.out.println("\n*****开始执行啦*****");
		System.out.println("【执行语句】" + str);
		String [] source = str.trim().split("\\s+") ;
		List<Word> ipList = new ArrayList<Word>() ; //将解析后的代码存放在代码区中的，供IP指针操作
		for(String s : source) {
			Word w = this.dict.findName(s) ;
			if(w != null) {
				ipList.add(w) ;
			} else {  //如果词典中没有该词，则新建一个临时词来存放
				ipList.add(new Word(s));
			}
		}
		ipList.add(this.dict.findName("END"));
		if(this.run(ipList)) {
			printStack();
			System.out.println("*****执行完啦*****\n");
		} else {
			this.paramStack.clear();
			this.returnStack.clear();
			this.colonBuffer.clear();
			printStack();
			System.out.println("*****执行出错，三清！*****\n");
		}
	}
	
	public boolean run(List<Word> ipList) {
		this.ip = 0 ;
		while(this.ip < ipList.size() - 1) {
			if(this.execute(ipList.get(this.ip), ipList.get(this.ip + 1))) {
				this.ip ++ ;
			} else {
				return false ;
			}
		}
		return true ;
	}
	
	public boolean execute(Word now, Word next) {
		String symbol = now.getName();
		String nextSymbol = next.getName();
		if("explain".equals(this.state)) {
			if (VmUtil.validateInteger(symbol)) {	//如果是数字就压入参数栈
				this.paramStack.push(Integer.parseInt(symbol)) ; 
			} else if("DUP".equals(symbol)) {
				this.paramStack.push(this.paramStack.peek()) ;
			} else if("]".equals(symbol)) {
				this.state = "compile" ;
			} else if("+".equals(symbol)) {
				this.paramStack.push(this.paramStack.pop() + this.paramStack.pop()) ;
			} else if("-".equals(symbol)) {
				int temp = this.paramStack.pop() ;
				this.paramStack.push(this.paramStack.pop() - temp) ;
			} else if("DROP".equals(symbol)) {
				this.paramStack.pop() ;
			} else if("R>".equals(symbol)) {
				this.paramStack.push(this.returnStack.pop()) ;
			} else if(">R".equals(symbol)) {
				this.returnStack.push(this.paramStack.pop()) ;
			} else if(".".equals(symbol)) {
				System.out.println(this.paramStack.pop()) ;
			} else if(".s".equals(symbol)) {
				System.out.println(this.paramStack.toString()) ;
			} else if(":".equals(symbol)) {
				this.dict.add(new Word(nextSymbol)) ;  //在词典中添加一个新的冒号词
				this.ip++ ;
				this.state = "compile" ;
			} else if ("?BRANCH".equals(symbol)) {
				if(this.paramStack.pop() == 0) {
					this.ip += Integer.parseInt(nextSymbol) ;
				} else {
					this.ip ++ ;  //跳过下面一个位置
				}
			}  else if ("BRANCH".equals(symbol)) {		
				this.ip += Integer.parseInt(nextSymbol) ;
			} else if ("IMMEDIATE".equals(symbol)) {
				this.dict.get(this.dict.size() - 1).setType(Word.Type.IMMEDIATE) ;
			} else if("COMPILE".equals(symbol)) {
				this.colonBuffer.add(new Word(nextSymbol));
				this.ip ++ ;
			} else if("?>MARK".equals(symbol)) {
				this.paramStack.push(this.colonBuffer.size()) ;
				this.paramStack.push(0); //在参数栈留下标记
				this.colonBuffer.add(new Word("0")) ;
			} else if("?<MARK".equals(symbol)) {
				this.paramStack.push(this.colonBuffer.size()) ;
			} else if("?>RESOLVE".equals(symbol)) {
				int flag = this.paramStack.pop() ;
				int addr = this.paramStack.pop() ;
				this.colonBuffer.set(addr, new Word(String.valueOf(this.colonBuffer.size() - addr + flag)));
			} else if("?<RESOLVE".equals(symbol)) {
				int addr = this.paramStack.pop() ;
				this.colonBuffer.add(new Word(String.valueOf(addr - this.colonBuffer.size())));
			} else if(this.dict.containsName(symbol)) {
				Word word = this.dict.findName(symbol) ;
				if(word.getWplist() != null) {
					this.returnStack.push(this.ip) ; //设置返回地址
					this.run(word.getWplist()) ;
					this.ip = this.returnStack.pop();
				}
			} else {
				return false ;
			}
		} else if ("compile".equals(this.state)) {
			Word word = this.dict.findName(symbol) ;
			if(word != null) {
				if(word.getType().toString().equals("IMMEDIATE")) {
					this.state = "explain" ;
					this.execute(now, next) ;
					this.state = "compile" ;
				} else {
					this.colonBuffer.add(word);
				}
			} else {
				if (VmUtil.validateInteger(symbol)) {	//如果是数字就编译成数字常数
					this.colonBuffer.add(new Word(symbol)) ;
				} else if(";".equals(symbol)) {
					this.colonBuffer.add(this.dict.findName("END"));
					this.dict.get(this.dict.size() - 1).setWplist(this.colonBuffer); //为新的冒号词设置wplist
					this.colonBuffer = new ArrayList<Word>() ; //增加新的冒号词后要重新申请wplist内存
					this.state = "explain" ;
				} else if("[".equals(symbol)) {
					this.state = "explain" ;
				} else {
					return false ;
				}
			}
		}
		return true ;
	}
	
	private void loadCoreWords(Dict dict){
		String[] coreWordNames = {
				"END", "DUP", "]", "+", "-", "DROP", "R>", ">R", ".",
				".s", ":", "?BRANCH", "BRANCH", "IMMEDIATE", "COMPILE", "?>MARK",
				"?<MARK", "?>RESOLVE", "?<RESOLVE"};
		for(int x = 0; x < coreWordNames.length; x ++) {
			dict.add(new Word(coreWordNames[x], Word.Type.CORE)) ;
		}

	}
	
	public Stack<Integer> getParamStack() {
		return paramStack;
	}
	
	public Stack<Integer> getReturnStack() {
		return returnStack;
	}
	
	public Dict getDict() {
		return dict;
	}

	public void setDict(Dict dict) {
		this.dict = dict;
	}
	
	public void printStack() {
		System.out.println("【参数栈】" + this.getParamStack().toString());
		System.out.println("【返回栈】" + this.getReturnStack().toString());
	}
	
	public void printDict() {
		for (Word w : this.getDict()) {   
            System.out.print("key= " + w.getName());   
            if(w.getWplist() != null) {
            	System.out.println(", value= " + w.getWplist());
            } else {
            	System.out.println();
            }
        }  
	}
}
