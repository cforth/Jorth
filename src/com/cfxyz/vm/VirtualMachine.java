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
	private List<Word> colonValue ; //指向冒号词定义的引用
	private int ip;  //指向正在执行的词
	private String state ; //虚拟机运行状态
	private Word next ; //向右边提前看一个词
	
	public VirtualMachine() {
		this.paramStack = new Stack<Integer>() ;
		this.returnStack = new Stack<Integer>() ;
		this.dict = new Dict();
		loadCoreWords(this.dict);
	}
	
	public String run(List<Word> ipList) {
		this.state = "explain" ;
		this.ip = 0 ;
		while(this.ip < ipList.size() - 1) {
			this.next = ipList.get(this.ip + 1) ;
			if("explain".equals(this.state)) {
				this.state = this.explain((ipList.get(this.ip))) ;
			} else if ("compile".equals(this.state)) {
				this.state = this.compile((ipList.get(this.ip))) ;
			} 
			
			if("error".equals(this.state)){
				return "error" ;
			}
			this.ip ++ ;
		}
		return "ok" ;
	}
	
	public String explain(Word now) {
		String symbol = now.getName();
		String nextSymbol = this.next.getName();
		if (VmUtil.validateInteger(symbol)) {	//如果是数字就压入参数栈
			this.paramStack.push(Integer.parseInt(symbol)) ; 
		} else if("DUP".equals(symbol)) {
			this.paramStack.push(this.paramStack.peek()) ;
		} else if("BYE".equals(symbol)) {
			System.exit(0);
		} else if("VARIABLE".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, Word.Type.VAR)) ;  //在词典中添加一个新的变量
			List<Word> varBuffer = new ArrayList<Word>();
			varBuffer.add(new Word("0")) ;
			this.dict.get(this.dict.size() - 1).setWplist(varBuffer);
			this.ip++ ;
		} else if("@".equals(symbol)) {
			int varIndex = this.paramStack.pop();
			this.paramStack.push(Integer.parseInt(this.dict.get(varIndex).getWplist().get(0).getName())) ;
		} else if("!".equals(symbol)) {
			int varIndex = this.paramStack.pop();
			int varValue = this.paramStack.pop();
			this.dict.get(varIndex).getWplist().get(0).setName(String.valueOf(varValue));
		} else if("]".equals(symbol)) {
			return "compile" ;
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
			this.colonValue = new ArrayList<Word>(); //分配一块区域用来保存冒号词定义
			this.dict.add(new Word(nextSymbol, this.colonValue)) ;  //在词典中添加一个新的冒号词
			this.ip++ ;
			return "compile" ;
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
			this.colonValue.add(new Word(nextSymbol));
			this.ip ++ ;
		} else if("?>MARK".equals(symbol)) {
			this.paramStack.push(this.colonValue.size()) ;
			this.paramStack.push(0); //在参数栈留下标记
			this.colonValue.add(new Word("0")) ;
		} else if("?<MARK".equals(symbol)) {
			this.paramStack.push(this.colonValue.size()) ;
		} else if("?>RESOLVE".equals(symbol)) {
			int flag = this.paramStack.pop() ;
			int addr = this.paramStack.pop() ;
			this.colonValue.set(addr, new Word(String.valueOf(this.colonValue.size() - addr + flag)));
		} else if("?<RESOLVE".equals(symbol)) {
			int addr = this.paramStack.pop() ;
			this.colonValue.add(new Word(String.valueOf(addr - this.colonValue.size())));
		} else if(this.dict.containsName(symbol)) {
			Word word = this.dict.findName(symbol) ;
			if(word.getType().equals(Word.Type.VAR)) {
				this.paramStack.push(this.dict.lastIndexOf(this.dict.findName(word.getName()))) ;
			} else if(word.getWplist() != null) {
				this.returnStack.push(this.ip) ; //设置返回地址
				this.state = "explain" ;
				if(!"ok".equals(this.run(word.getWplist()))){ //递归调用run方法
					return "error" ;
				}
				this.ip = this.returnStack.pop();
			}
		} else {
			return "error" ;
		}
		return "explain" ;
	}
	
	public String compile(Word now) {
		String symbol = now.getName();
		Word word = this.dict.findName(symbol) ;
		if(word != null) {
			if(word.getType().toString().equals("IMMEDIATE")) {
				this.explain(now) ;
			} else {
				this.colonValue.add(word);
			}
		} else {
			if (VmUtil.validateInteger(symbol)) {	//如果是数字就编译成数字常数
				this.colonValue.add(new Word(symbol)) ;
			} else if(";".equals(symbol)) {
				this.colonValue.add(this.dict.findName("END"));
				this.dict.get(this.dict.size() - 1).setWplist(this.colonValue); //为新的冒号词设置wplist
				return "explain" ;
			} else if("[".equals(symbol)) {
				return "explain" ;
			} else {
				return "error" ;
			}
		}
		return "compile" ;
	}
	
	private void loadCoreWords(Dict dict){
		String[] coreWordNames = {
				"END", "BYE", "DUP","VARIABLE","!","@","]", "+", "-", "DROP", "R>", ">R", ".",
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
