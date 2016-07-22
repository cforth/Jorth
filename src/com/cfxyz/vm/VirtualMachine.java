package com.cfxyz.vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.cfxyz.vm.util.VmUtil;
import com.cfxyz.vm.word.Dict;
import com.cfxyz.vm.word.Word;

public class VirtualMachine {
	private Stack<Integer> paramStack ;   //����ջ
	private Stack<Integer> returnStack ;  //����ջ
	private Dict dict ; //�ʵ�
	private List<Word> colonBuffer ; //ð�Ŵʶ���ʱ�Ļ�����
	private int ip;  //ָ������ִ�еĴ�
	private String state ; //���������״̬
	
	public VirtualMachine() {
		this.paramStack = new Stack<Integer>() ;
		this.returnStack = new Stack<Integer>() ;
		this.colonBuffer = new ArrayList<Word>() ; 
		this.dict = new Dict();
		loadCoreWords(this.dict);
		this.state = "explain" ;
	}
	
	public void parse(String str) {
		System.out.println("\n*****��ʼִ����*****");
		System.out.println("��ִ����䡿" + str);
		String [] source = str.trim().split("\\s+") ;
		List<Word> ipList = new ArrayList<Word>() ; //��������Ĵ������ڴ������еģ���IPָ�����
		for(String s : source) {
			Word w = this.dict.findName(s) ;
			if(w != null) {
				ipList.add(w) ;
			} else {  //����ʵ���û�иôʣ����½�һ����ʱ�������
				ipList.add(new Word(s));
			}
		}
		ipList.add(this.dict.findName("END"));
		if(this.run(ipList)) {
			printStack();
			System.out.println("*****ִ������*****\n");
		} else {
			this.paramStack.clear();
			this.returnStack.clear();
			this.colonBuffer.clear();
			printStack();
			System.out.println("*****ִ�г������壡*****\n");
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
			if (VmUtil.validateInteger(symbol)) {	//��������־�ѹ�����ջ
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
				this.dict.add(new Word(nextSymbol)) ;  //�ڴʵ������һ���µ�ð�Ŵ�
				this.ip++ ;
				this.state = "compile" ;
			} else if ("?BRANCH".equals(symbol)) {
				if(this.paramStack.pop() == 0) {
					this.ip += Integer.parseInt(nextSymbol) ;
				} else {
					this.ip ++ ;  //��������һ��λ��
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
				this.paramStack.push(0); //�ڲ���ջ���±��
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
					this.returnStack.push(this.ip) ; //���÷��ص�ַ
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
				if (VmUtil.validateInteger(symbol)) {	//��������־ͱ�������ֳ���
					this.colonBuffer.add(new Word(symbol)) ;
				} else if(";".equals(symbol)) {
					this.colonBuffer.add(this.dict.findName("END"));
					this.dict.get(this.dict.size() - 1).setWplist(this.colonBuffer); //Ϊ�µ�ð�Ŵ�����wplist
					this.colonBuffer = new ArrayList<Word>() ; //�����µ�ð�Ŵʺ�Ҫ��������wplist�ڴ�
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
		System.out.println("������ջ��" + this.getParamStack().toString());
		System.out.println("������ջ��" + this.getReturnStack().toString());
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
