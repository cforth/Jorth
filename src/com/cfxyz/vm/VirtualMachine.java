package com.cfxyz.vm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.cfxyz.vm.util.VmUtil;

public class VirtualMachine {
	private Stack<Integer> paramStack ;   //����ջ
	private Stack<Integer> returnStack ;  //����ջ
	private Dict dict ; //�ʵ�
	private int ip;  //ָ������ִ�еĴ�
	private State state ; //���������״̬
	private Word next ; //���ұ���ǰ��һ����
	private String source = null; //ָ��ǰ�����Դ�����ַ���
	BufferedReader localReader;
	
	public VirtualMachine() {
		this.paramStack = new Stack<Integer>() ;
		this.returnStack = new Stack<Integer>() ;
		this.dict = new Dict();
		loadCoreWords(this.dict);
		this.localReader = new BufferedReader(
                new InputStreamReader(System.in));
	}
	
	public String run(List<Word> ipList) {
		this.state = State.explain ;
		this.ip = 0 ;
		while(this.ip < ipList.size() - 1) {
			this.next = ipList.get(this.ip + 1) ;
			if(State.explain.equals(this.state)) {
				this.explain((ipList.get(this.ip))) ;
			} else if (State.compile.equals(this.state)) {
				this.compile((ipList.get(this.ip))) ;
			} 
			
			if(State.error.equals(this.state)){
				return "error" ;
			}
			this.ip ++ ;
		}
		return "ok" ;
	}
	
	public void explain(Word now) {
		String symbol = now.getName();
		String nextSymbol = this.next.getName();
		if (VmUtil.validateInteger(symbol)) {	//��������־�ѹ�����ջ
			this.paramStack.push(Integer.parseInt(symbol)) ; 
		} else if("DUP".equals(symbol)) {
			this.paramStack.push(this.paramStack.peek()) ;
		} else if("BYE".equals(symbol)) {
			System.exit(0);
		}  else if("READ".equals(symbol)) {
			// ���Դӱ�׼�����ȡ����
			try {
				this.source = this.localReader.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if("INTERPRET".equals(symbol)) {
			this.returnStack.push(this.ip) ; //���÷��ص�ַ
			System.out.println("\n*****��ʼִ����*****");
			System.out.println("��ִ����䡿" + this.source);
			
			String [] source = this.source.trim().split("\\s+") ;
			List<Word> ipList = new ArrayList<Word>() ; //��������Ĵ������ڴ������еģ���IPָ�����
			//�ı������������һ��Դ�����е�ÿ���ʣ�����һ��Word�б�
			for(String s : source) {
				Word w = this.getDict().findByName(s) ;
				if(w != null) {
					ipList.add(w) ;
				} else {  //����ʵ���û�иôʣ����½�һ����ʱ�������
					ipList.add(new Word(s));
				}
			}
			ipList.add(this.getDict().findByName("END")); 
			//��Word�б��������ȥִ��
			if("ok".equals(this.run(ipList))) {
				this.printStack();
				System.out.println("*****ִ������*****\n");
			} else {
				this.getParamStack().clear();
				this.getReturnStack().clear();
				this.printStack();
				System.out.println("*****ִ�г���*****\n");
			}
			this.ip = this.returnStack.pop();
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
			this.state = State.compile ;
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
			this.dict.add(new Word(nextSymbol, new ArrayList<Word>())) ;  //�ڴʵ������һ���µ�ð�Ŵ�
			this.ip++ ;
			this.state = State.compile ;
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
			this.dict.getLastWord().getWplist().add(new Word(nextSymbol));
			this.ip ++ ;
		} else if("?>MARK".equals(symbol)) {
			this.paramStack.push(this.dict.getLastWord().getWplist().size()) ;
			this.paramStack.push(0); //�ڲ���ջ���±��
			this.dict.getLastWord().getWplist().add(new Word("0")) ;
		} else if("?<MARK".equals(symbol)) {
			this.paramStack.push(this.dict.getLastWord().getWplist().size()) ;
		} else if("?>RESOLVE".equals(symbol)) {
			int flag = this.paramStack.pop() ;
			int addr = this.paramStack.pop() ;
			this.dict.getLastWord().getWplist().set(addr, new Word(String.valueOf(this.dict.getLastWord().getWplist().size() - addr + flag)));
		} else if("?<RESOLVE".equals(symbol)) {
			int addr = this.paramStack.pop() ;
			this.dict.getLastWord().getWplist().add(new Word(String.valueOf(addr - this.dict.getLastWord().getWplist().size())));
		} else if(this.dict.containsName(symbol)) {
			Word word = this.dict.findByName(symbol) ;
			if(word.getType().equals(Word.Type.VAR)) {
				this.paramStack.push(this.dict.lastIndexOf(this.dict.findByName(word.getName()))) ;
			} else if(word.getWplist() != null) {
				this.returnStack.push(this.ip) ; //���÷��ص�ַ
				this.state = State.explain ;
				if(!"ok".equals(this.run(word.getWplist()))){ //�ݹ����run����
					this.state = State.error ;
				}
				this.ip = this.returnStack.pop();
			}
		} else {
			this.state = State.error ;
		}
	}
	
	public void compile(Word now) {
		String symbol = now.getName();
		Word word = this.dict.findByName(symbol) ;
		if(word != null) {
			if(word.getType().toString().equals("IMMEDIATE")) {
				this.explain(now) ;
				this.state = State.compile ;
			} else {
				this.dict.getLastWord().getWplist().add(word);
			}
		} else {
			if (VmUtil.validateInteger(symbol)) {	//��������־ͱ�������ֳ���
				this.dict.getLastWord().getWplist().add(new Word(symbol)) ;
			} else if(";".equals(symbol)) {
				this.dict.getLastWord().getWplist().add(this.dict.findByName("END"));
				this.dict.get(this.dict.size() - 1).setWplist(this.dict.getLastWord().getWplist()); //Ϊ�µ�ð�Ŵ�����wplist
				this.state = State.explain ;
			} else if("[".equals(symbol)) {
				this.state = State.explain ;
			} else {
				this.state = State.error ;
			}
		}
	}
	
	private void loadCoreWords(Dict dict){
		String[] coreWordNames = {
				"END", "BYE", "DUP","READ","INTERPRET","VARIABLE","!","@","]", "+", "-", "DROP", "R>", ">R", ".",
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
