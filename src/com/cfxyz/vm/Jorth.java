package com.cfxyz.vm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.cfxyz.vm.util.VmUtil;

public class Jorth {
	private Stack<Integer> paramStack; // 参数栈
	private Stack<Integer> returnStack; // 返回栈
	private Dict dict; // 词典
	private int ip; // 指向正在执行的词
	private State state; // 虚拟机运行状态
	private Word next; // 向右边提前看一个词
	private String source = null; // 指向当前读入的源代码字符串
	private List<Word> wordListBuffer = null; // 将输入字符串转为Word列表后保存起来
	private BufferedReader localReader = null;

	/**
	 * Forth的虚拟机初始化
	 */
	public Jorth() {
		this.paramStack = new Stack<Integer>();
		this.returnStack = new Stack<Integer>();
		this.dict = new Dict();
		loadCoreWords(this.dict);
		this.localReader = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * Forth的解释程序
	 * 
	 * @param str
	 */
	public void interpret(String str) {
		this.source = str;
		parse(this.source);
		run(this.wordListBuffer);

	}

	/**
	 * 将一行Forth代码转换为Word列表
	 * 
	 * @param line
	 */
	public void parse(String line) {
		System.out.println("【执行语句】" + line);

		List<String> source = VmUtil.separateWord(line);
		this.wordListBuffer = new ArrayList<Word>(); // 将解析后的代码存放在代码区中的，供IP指针操作
		// 文本解释器分离出一行源代码中的每个词，建立一个Word列表
		for (String s : source) {
			Word w = this.dict.findByName(s);
			if (w != null) {
				this.wordListBuffer.add(w);
			} else { // 如果词典中没有该词，则新建一个临时词来存放
				this.wordListBuffer.add(new Word(s));
			}
		}
		this.wordListBuffer.add(this.dict.findByName("END"));
		// System.out.println(this.wordListBuffer);
	}

	/**
	 * 将Word列表执行
	 * 
	 * @param ipList
	 * @return
	 */
	public void run(List<Word> ipList) {
		this.state = State.explain;
		this.ip = 0;
		while (this.ip < ipList.size() - 1) {
			this.next = ipList.get(this.ip + 1);
			if (State.explain.equals(this.state)) {
				this.explain((ipList.get(this.ip)));
			} else if (State.compile.equals(this.state)) {
				this.compile((ipList.get(this.ip)));
			}

			if (State.error.equals(this.state)) {
				this.paramStack.clear();
				System.out.println("ERROR! -> " + this.source);
				this.state = State.explain; // 将状态切换回解释态，用于错误恢复
			}
			this.ip++;
		}
	}

	/**
	 * 执行当前Word词
	 * 
	 * @param now
	 */
	public void explain(Word now) {
		String symbol = now.getName();
		String nextSymbol = this.next.getName();
		if (VmUtil.validateInteger(symbol)) { // 如果是数字就压入参数栈
			this.paramStack.push(Integer.parseInt(symbol));
		} else if ("BYE".equals(symbol)) {
			System.exit(0);
		} else if ("PARSE".equals(symbol)) {
			try { // 从标准输入读取代码
				this.source = this.localReader.readLine();
				parse(this.source);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("RUN".equals(symbol)) {
			this.returnStack.push(this.ip); // 设置返回地址
			run(this.wordListBuffer);
			this.ip = this.returnStack.pop();
		} else if ("CONSTANT".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, Word.Type.CONST)); // 在词典中添加一个新的常量
			List<Word> varBuffer = new ArrayList<Word>();
			varBuffer.add(new Word(String.valueOf(this.paramStack.pop())));
			this.dict.get(this.dict.size() - 1).setWplist(varBuffer);
			this.ip++;
		} else if ("VARIABLE".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, Word.Type.VAR)); // 在词典中添加一个新的变量
			List<Word> varBuffer = new ArrayList<Word>();
			varBuffer.add(new Word("0"));
			this.dict.get(this.dict.size() - 1).setWplist(varBuffer);
			this.ip++;
		} else if ("CREATE".equals(symbol)) { // 目前只能用于定义数组
			this.dict.add(new Word(nextSymbol, Word.Type.ARRAY)); // 在词典中添加一个新的数组
			this.ip++;
		} else if ("ALLOT".equals(symbol)) { // 用来在数组中分配空间
			int arraySize = this.paramStack.pop();
			List<Word> arrayBuffer = new ArrayList<Word>();
			for (int x = 0; x < arraySize; x++) {
				arrayBuffer.add(new Word("0"));
			}
			this.dict.get(this.dict.size() - 1).setWplist(arrayBuffer);
		} else if ("@".equals(symbol)) {
			int varIndex = this.paramStack.pop(); // 取出词在词典中的位置
			int varDev = this.paramStack.pop(); // 取出数组或变量的初始偏移量，变量为0
			this.paramStack.push(Integer.parseInt(this.dict.get(varIndex).getWplist().get(varDev).getName()));
		} else if ("!".equals(symbol)) {
			int varIndex = this.paramStack.pop();
			int varDev = this.paramStack.pop();
			int varValue = this.paramStack.pop();
			this.dict.get(varIndex).getWplist().get(varDev).setName(String.valueOf(varValue));
		} else if ("]".equals(symbol)) {
			this.state = State.compile;
		} else if ("+".equals(symbol)) {
			this.paramStack.push(this.paramStack.pop() + this.paramStack.pop());
		} else if ("-".equals(symbol)) {
			int temp = this.paramStack.pop();
			this.paramStack.push(this.paramStack.pop() - temp);
		} else if ("*".equals(symbol)) {
			this.paramStack.push(this.paramStack.pop() * this.paramStack.pop());
		} else if ("/".equals(symbol)) {
			int temp = this.paramStack.pop();
			this.paramStack.push(this.paramStack.pop() / temp);
		} else if (">".equals(symbol)) {
			int temp = this.paramStack.pop();
			if (this.paramStack.pop() > temp) {
				this.paramStack.push(1);
			} else {
				this.paramStack.push(0);
			}
		} else if ("<".equals(symbol)) {
			int temp = this.paramStack.pop();
			if (this.paramStack.pop() < temp) {
				this.paramStack.push(1);
			} else {
				this.paramStack.push(0);
			}
		} else if ("=".equals(symbol)) {
			int temp = this.paramStack.pop();
			if (this.paramStack.pop() == temp) {
				this.paramStack.push(1);
			} else {
				this.paramStack.push(0);
			}
		} else if ("DROP".equals(symbol)) {
			this.paramStack.pop();
		} else if ("PICK".equals(symbol)) {
			int temp = this.paramStack.pop();
			this.paramStack.push(this.paramStack.get(this.paramStack.size() - temp));
		} else if ("ROLL".equals(symbol)) {
			int temp = this.paramStack.pop();
			this.paramStack.push(this.paramStack.get(this.paramStack.size() - temp));
			this.paramStack.remove(this.paramStack.size() - temp - 1);
		} else if ("EMIT".equals(symbol)) {
			System.out.print((char) (int) this.paramStack.pop());
		} else if (".\"".equals(symbol)) { // 打印出后面字符串
			System.out.print(nextSymbol.replace("\"", ""));
			this.ip++;
		} else if ("R>".equals(symbol)) {
			this.paramStack.push(this.returnStack.pop());
		} else if (">R".equals(symbol)) {
			this.returnStack.push(this.paramStack.pop());
		} else if ("SEE".equals(symbol)) {
			System.out.println(this.dict.findByName(nextSymbol));
			this.ip++;
		} else if ("SIZE".equals(symbol)) { // 词典长度
			this.paramStack.push(this.dict.size());
		} else if ("PRINTWORD".equals(symbol)) { // 将栈顶数字作为词典中词的下标，打印出词的定义
			System.out.println(this.dict.get(this.paramStack.pop()));
		} else if (".".equals(symbol)) {
			System.out.println(this.paramStack.pop());
		} else if (".s".equals(symbol)) {
			System.out.println("DS> " + this.paramStack.toString());
			System.out.println("RS> " + this.returnStack.toString());
		} else if (":".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, new ArrayList<Word>())); // 在词典中添加一个新的冒号词
			this.ip++;
			this.state = State.compile;
		} else if ("?BRANCH".equals(symbol)) {
			if (this.paramStack.pop() == 0) {
				this.ip += Integer.parseInt(nextSymbol);
			} else {
				this.ip++; // 跳过下面一个位置
			}
		} else if ("BRANCH".equals(symbol)) {
			this.ip += Integer.parseInt(nextSymbol);
		} else if ("IMMEDIATE".equals(symbol)) {
			this.dict.get(this.dict.size() - 1).setType(Word.Type.IMMEDIATE);
		} else if ("COMPILE".equals(symbol)) {
			this.dict.getLastWord().getWplist().add(new Word(nextSymbol));
			this.ip++;
		} else if ("?>MARK".equals(symbol)) {
			this.paramStack.push(this.dict.getLastWord().getWplist().size());
			this.paramStack.push(0); // 在参数栈留下标记
			this.dict.getLastWord().getWplist().add(new Word("0"));
		} else if ("?<MARK".equals(symbol)) {
			this.paramStack.push(this.dict.getLastWord().getWplist().size());
		} else if ("?>RESOLVE".equals(symbol)) {
			int flag = this.paramStack.pop();
			int addr = this.paramStack.pop();
			this.dict.getLastWord().getWplist().set(addr,
					new Word(String.valueOf(this.dict.getLastWord().getWplist().size() - addr + flag)));
		} else if ("?<RESOLVE".equals(symbol)) {
			int addr = this.paramStack.pop();
			this.dict.getLastWord().getWplist()
					.add(new Word(String.valueOf(addr - this.dict.getLastWord().getWplist().size())));
		} else if (this.dict.containsName(symbol)) {
			Word word = this.dict.findByName(symbol);
			if (word.getType().equals(Word.Type.VAR) || word.getType().equals(Word.Type.ARRAY)) {
				this.paramStack.push(this.dict.lastIndexOf(this.dict.findByName(word.getName())));
			} else if (word.getType().equals(Word.Type.CONST)) {
				this.paramStack
						.push(Integer.valueOf(this.dict.findByName(word.getName()).getWplist().get(0).getName()));
			} else if (word.getWplist() != null) {
				this.returnStack.push(this.ip); // 设置返回地址
				run(word.getWplist());// 递归调用run方法
				this.ip = this.returnStack.pop();
			}
		} else {
			this.state = State.error;
		}
	}

	/**
	 * 编译当前Word词
	 * 
	 * @param now
	 */
	public void compile(Word now) {
		String symbol = now.getName();
		Word word = this.dict.findByName(symbol);
		if (".\"".equals(symbol)) {
			this.dict.getLastWord().getWplist().add(word);
			this.dict.getLastWord().getWplist().add(this.next); // 如果是字符串常量，就编译进词典中
			this.ip++;
		} else if (";".equals(symbol)) {
			this.dict.getLastWord().getWplist().add(this.dict.findByName("END"));
			this.dict.get(this.dict.size() - 1).setWplist(this.dict.getLastWord().getWplist()); // 为新的冒号词设置wplist
			this.state = State.explain;
		} else if ("[".equals(symbol)) {
			this.state = State.explain;
		} else if (word != null) { // 如果在词典中有定义
			if (word.getType().toString().equals("IMMEDIATE")) {
				this.explain(now);
				this.state = State.compile;
			} else {
				this.dict.getLastWord().getWplist().add(word);
			}
		} else {
			if (VmUtil.validateInteger(symbol)) { // 如果是数字就编译成数字常数
				this.dict.getLastWord().getWplist().add(new Word(symbol));
			} else {
				System.out.println(symbol);
				this.state = State.error;
			}
		}
	}

	/**
	 * 加载核心词到词典中 仅仅在词典中添加核心词的空词，核心词是通过explain方法模拟执行
	 * 
	 * @param dict
	 */
	private void loadCoreWords(Dict dict) {
		String[] coreWordNames = { "END", "BYE", "PICK", "ROLL", "PARSE", "RUN", "CONSTANT", "VARIABLE", "CREATE",
				"ALLOT", "!", "@", "[", "]", "+", "-", "DROP", ">", "<", "=", "R>", ">R", ".", ".\"", "SEE", "SIZE",
				"PRINTWORD", "*", "/", ".s", ":", ";", "?BRANCH", "BRANCH", "IMMEDIATE", "COMPILE", "?>MARK", "EMIT",
				"?<MARK", "?>RESOLVE", "?<RESOLVE" };
		for (int x = 0; x < coreWordNames.length; x++) {
			dict.add(new Word(coreWordNames[x], Word.Type.CORE));
		}

	}
	
	/**
	 * 从文件中读取Forth源代码，支持冒号词的换行定义
	 * @param filePath 源代码文件的路径
	 */
	public void loadLib(String filePath) {
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				boolean colonFlag = false; // 处理冒号词换行定义
				String colonTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					//从文件中读取源代码时，定义冒号词允许换行定义
					if (!lineTxt.contains(":") && !lineTxt.contains(";") && !colonFlag
							|| lineTxt.contains(":") && lineTxt.contains(";")) {
						this.interpret(lineTxt);
					} else if (lineTxt.contains(":")) {
						colonFlag = true;
						colonTxt = lineTxt;
					} else if (lineTxt.contains(";")) {
						colonTxt = colonTxt + " " + lineTxt;
						this.interpret(colonTxt);
						colonFlag = false;
					} else if (colonFlag) {
						colonTxt = colonTxt + " " + lineTxt;

					}
				}

				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
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
}
