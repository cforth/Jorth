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
	private Word next ; //���ұ���ǰ��һ����
	
	public VirtualMachine() {
		this.paramStack = new Stack<Integer>() ;
		this.returnStack = new Stack<Integer>() ;
		this.colonBuffer = new ArrayList<Word>() ; 
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
		if (VmUtil.validateInteger(symbol)) {	//��������־�ѹ�����ջ
			this.paramStack.push(Integer.parseInt(symbol)) ; 
		} else if("DUP".equals(symbol)) {
			this.paramStack.push(this.paramStack.peek()) ;
		} else if("BYE".equals(symbol)) {
			System.exit(0);
		} else if("VARIABLE".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, Word.Type.VAR)) ;  //�ڴʵ������һ���µı���
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
			this.dict.add(new Word(nextSymbol)) ;  //�ڴʵ������һ���µ�ð�Ŵ�
			this.ip++ ;
			return "compile" ;
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
			if(word.getType().equals(Word.Type.VAR)) {
				this.paramStack.push(this.dict.lastIndexOf(this.dict.findName(word.getName()))) ;
			} else if(word.getWplist() != null) {
				this.returnStack.push(this.ip) ; //���÷��ص�ַ
				this.state = "explain" ;
				if(!"ok".equals(this.run(word.getWplist()))){ //�ݹ����run����
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
				this.colonBuffer.add(word);
			}
		} else {
			if (VmUtil.validateInteger(symbol)) {	//��������־ͱ�������ֳ���
				this.colonBuffer.add(new Word(symbol)) ;
			} else if(";".equals(symbol)) {
				this.colonBuffer.add(this.dict.findName("END"));
				this.dict.get(this.dict.size() - 1).setWplist(this.colonBuffer); //Ϊ�µ�ð�Ŵ�����wplist
				this.colonBuffer = new ArrayList<Word>() ; //�����µ�ð�Ŵʺ�Ҫ��������wplist�ڴ�
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
	
	public List<Word> getColonBuffer() {
		return colonBuffer;
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
