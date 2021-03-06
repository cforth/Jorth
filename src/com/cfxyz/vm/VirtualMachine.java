package com.cfxyz.vm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VirtualMachine {
	private Stack<Integer> paramStack; // 参数栈
	private Stack<Integer> returnStack; // 返回栈
	private Dict dict; // 词典
	private int ip; // 指向正在执行的词
	private State state; // 虚拟机运行状态
	private Word next; // 向右边提前看一个词
	private String source = null; // 指向当前读入的源代码字符串
	private List<Word> wordListBuffer = null; // 将输入字符串转为Word列表后保存起来
	private BufferedReader localReader = null; //指定输入流
	private PrintStream out = null; //指定输出流

	/**
	 * Forth的虚拟机初始化
	 */
	public VirtualMachine() {
		this.paramStack = new Stack<Integer>();
		this.returnStack = new Stack<Integer>();
		this.dict = new Dict();
		loadCoreWords();
		this.localReader = new BufferedReader(new InputStreamReader(System.in));
		this.out = new PrintStream(System.out);
	}
	
	/**
	 * Forth的虚拟机初始化，指定输出流
	 */
	public VirtualMachine(InputStream input, OutputStream output) {
		this.paramStack = new Stack<Integer>();
		this.returnStack = new Stack<Integer>();
		this.dict = new Dict();
		loadCoreWords();
		this.localReader = new BufferedReader(new InputStreamReader(input));
		this.out = new PrintStream(output);
	}
	
	/**
	 * 加载核心词到词典中 仅仅在词典中添加核心词的空词，核心词是通过explain方法模拟执行
	 */
	private void loadCoreWords() {
		String[] coreWordNames = { "END", "BYE", "PICK", "ROLL", "PARSE", "RUN", "CONSTANT", "VARIABLE", "CREATE",
				"ALLOT", "!", "@", "[", "]", "+", "-", "DROP", ">", "<", "=", "R>", ">R", ".", ".\"", "SEE", "SIZE",
				"PRINTWORD", "*", "/", ".s", ":", ";", "?BRANCH", "BRANCH", "IMMEDIATE", "COMPILE", "?>MARK", "EMIT",
				"?<MARK", "?>RESOLVE", "?<RESOLVE", "QUERY", "LOADFILE" };
		for (int x = 0; x < coreWordNames.length; x++) {
			this.dict.add(new Word(coreWordNames[x], Word.Type.CORE));
		}

	}
	
	/**
	 * 从输入流中读取Forth代码，冒号词可以跨行定义
	 * @return 一行合法的Forth代码
	 */
	private String read() {
		String lineTxt = null;
		boolean colonFlag = false; // 处理冒号词换行定义
		String colonTxt = "";
		try {
			while ((lineTxt = this.localReader.readLine()) != null) {
				//冒号词允许换行定义
				if (!lineTxt.contains(":") && !lineTxt.contains(";") && !colonFlag
						|| lineTxt.contains(":") && lineTxt.contains(";")) {
					return lineTxt;
				} else if (lineTxt.contains(":")) {
					colonFlag = true;
					colonTxt = lineTxt;
				} else if (lineTxt.contains(";")) {
					colonTxt = colonTxt + " " + lineTxt;
					return colonTxt;
				} else if (colonFlag) {
					colonTxt = colonTxt + " " + lineTxt;

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lineTxt;
	}
	
	/**
	 * 将一行Forth代码转换为Word列表，存入this.wordListBuffer中
	 * 
	 * @param line 一行合法的Forth代码
	 */
	private List<Word> getTokens(String line) {
		line = line.replaceAll("\\(\\s[^)]*\\)", ""); //将源代码中的括号注释删除
		List<String> tokens = new ArrayList<String>();

		List<Integer> note = new ArrayList<Integer>();
		note.add(0);
		int start = 0;
		int end = 0;
		if (line.trim().equals("")) {
			tokens.add("END");
		} else if (line.indexOf(".\" ") != -1) {
			while (true) {
				if (line.substring(end).indexOf(".\" ") != -1) {
					start = line.substring(end).indexOf(".\" ") + end + 3;
					end = line.substring(start).indexOf("\"") + start + 1;
					if (end != -1) {
						note.add(start);
						note.add(end);

					} else {
						break;
					}
				} else {
					break;
				}
			}
			note.add(line.length());
			for (String s : line.substring(note.get(0), note.get(1)).trim().split("\\s+")) {
				tokens.add(s);
			}
			for (int x = 1; x < note.size() - 2; x += 2) {
				int y = x + 1;
				int z = x + 2;

				tokens.add(line.substring(note.get(x), note.get(y)).trim());
				for (String s : line.substring(note.get(y), note.get(z)).trim().split("\\s+")) {
					if (!s.equals("")) {
						tokens.add(s);
					}
				}
			}

		} else {
			for (String x : line.trim().split("\\s+")) {
				tokens.add(x);
			}
		}

		List<Word> wordList = new ArrayList<Word>(); // 将解析后的代码存放在代码区中的，供IP指针操作
		// 文本解释器分离出一行源代码中的每个词，建立一个Word列表
		for (String s : tokens) {
			Word w = this.dict.findByName(s);
			if (w != null) {
				wordList.add(w);
			} else { // 如果词典中没有该词，则新建一个临时词来存放
				wordList.add(new Word(s));
			}
		}
		wordList.add(this.dict.findByName("END"));
		
		return wordList;
	}

	/**
	 * 执行当前Word词
	 * 
	 * @param now
	 */
	private void explain(Word now) {
		String symbol = now.getName();
		String nextSymbol = this.next.getName();
		List<Word> lastWordWplist = this.dict.getLastWord().getWplist();
		if (symbol.matches("-?\\d+")) { // 如果是数字就压入参数栈
			this.paramStack.push(Integer.parseInt(symbol));
		} else if ("BYE".equals(symbol)) {
			System.exit(0);
		} else if ("LOADFILE".equals(symbol)) {
			loadLib(nextSymbol); //从文件中加载Forth代码
			skipNextWord();
		} else if ("QUERY".equals(symbol)) {
			this.source = read(); //从输入流中读取一段Forth代码		
			if(this.source == null) {
				this.out.println("【程序退出】");
				System.exit(-1);  //如果到达输入流尾端，则退出主循环
			} else {
				this.wordListBuffer = getTokens(this.source);
			}
		} else if ("RUN".equals(symbol)) {
			this.returnStack.push(this.ip); // 设置返回地址
			run(this.wordListBuffer);
			this.ip = this.returnStack.pop();
		} else if ("CONSTANT".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, Word.Type.CONST)); // 在词典中添加一个新的常量
			List<Word> varBuffer = new ArrayList<Word>();
			varBuffer.add(new Word(String.valueOf(this.paramStack.pop())));
			this.dict.getLastWord().setWplist(varBuffer);
			skipNextWord();
		} else if ("VARIABLE".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, Word.Type.VAR)); // 在词典中添加一个新的变量
			List<Word> varBuffer = new ArrayList<Word>();
			varBuffer.add(new Word("0"));
			this.dict.getLastWord().setWplist(varBuffer);
			skipNextWord();
		} else if ("CREATE".equals(symbol)) { // 目前只能用于定义数组
			this.dict.add(new Word(nextSymbol, Word.Type.ARRAY)); // 在词典中添加一个新的数组
			skipNextWord();
		} else if ("ALLOT".equals(symbol)) { // 用来在数组中分配空间
			int arraySize = this.paramStack.pop();
			List<Word> arrayBuffer = new ArrayList<Word>();
			for (int x = 0; x < arraySize; x++) {
				arrayBuffer.add(new Word("0"));
			}
			this.dict.getLastWord().setWplist(arrayBuffer);
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
			this.out.print((char) (int) this.paramStack.pop());
		} else if (".\"".equals(symbol)) { // 打印出后面字符串
			this.out.print(nextSymbol.replace("\"", ""));
			skipNextWord();
		} else if ("R>".equals(symbol)) {
			this.paramStack.push(this.returnStack.pop());
		} else if (">R".equals(symbol)) {
			this.returnStack.push(this.paramStack.pop());
		} else if ("SEE".equals(symbol)) {
			this.out.println(this.dict.findByName(nextSymbol));
			skipNextWord();
		} else if ("SIZE".equals(symbol)) { // 词典长度
			this.paramStack.push(this.dict.size());
		} else if ("PRINTWORD".equals(symbol)) { // 将栈顶数字作为词典中词的下标，打印出词的名称
			this.out.print(this.dict.get(this.paramStack.pop()).getName());
		} else if (".".equals(symbol)) {
			this.out.println(this.paramStack.pop());
		} else if (".s".equals(symbol)) {
			this.out.println("DS> " + this.paramStack.toString());
			this.out.println("RS> " + this.returnStack.toString());
		} else if (":".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, Word.Type.REVEAL, new ArrayList<Word>())); // 在词典中添加一个新的冒号词
			skipNextWord();
			this.state = State.compile;
		} else if ("?BRANCH".equals(symbol)) {
			if (this.paramStack.pop() == 0) {
				this.ip += Integer.parseInt(nextSymbol);
			} else {
				skipNextWord(); // 跳过下面一个位置
			}
		} else if ("BRANCH".equals(symbol)) {
			this.ip += Integer.parseInt(nextSymbol);
		} else if ("IMMEDIATE".equals(symbol)) {
			this.dict.getLastWord().setType(Word.Type.IMMEDIATE);
		} else if ("COMPILE".equals(symbol)) {
			compile(this.next);
			skipNextWord();
		} else if ("?>MARK".equals(symbol)) {
			this.paramStack.push(lastWordWplist.size());
			this.paramStack.push(0); // 在参数栈留下标记
			lastWordWplist.add(new Word("0"));
		} else if ("?<MARK".equals(symbol)) {
			this.paramStack.push(lastWordWplist.size());
		} else if ("?>RESOLVE".equals(symbol)) {
			int flag = this.paramStack.pop();
			int addr = this.paramStack.pop();
			lastWordWplist.set(addr,
					new Word(String.valueOf(lastWordWplist.size() - addr + flag)));
		} else if ("?<RESOLVE".equals(symbol)) {
			int addr = this.paramStack.pop();
			lastWordWplist.add(new Word(String.valueOf(addr - lastWordWplist.size())));
		} else if (this.dict.containsName(symbol)) {
			Word word = this.dict.findByName(symbol);
			if (word.getType().equals(Word.Type.VAR) || word.getType().equals(Word.Type.ARRAY)) {
				this.paramStack.push(this.dict.lastIndexOf(word));
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
	 * @param now
	 */
	private void compile(Word now) {
		String symbol = now.getName();
		List<Word> lastWordWplist = this.dict.getLastWord().getWplist();
		if (symbol.matches("-?\\d+")) { // 如果是数字就编译成数字常数
			lastWordWplist.add(new Word(symbol));
		} else if (".\"".equals(symbol)) {
			lastWordWplist.add(this.dict.findByName(".\""));
			lastWordWplist.add(this.next); // 如果是字符串常量，就编译进词典中
			skipNextWord();
		} else if (";".equals(symbol)) {
			lastWordWplist.add(this.dict.findByName("END"));
			this.state = State.explain;
		} else if ("[".equals(symbol)) {
			this.state = State.explain;
		} else if (this.dict.containsName(symbol)) { // 如果在词典中有定义
			Word word = this.dict.findByName(symbol);
			if (word.getType().toString().equals("IMMEDIATE")) {
				explain(word);
				this.state = State.compile;
			} else {
				lastWordWplist.add(word);
			}
		} else {
			this.state = State.error;
		}
	}
	
	/**
	 * 将Word列表执行
	 * 
	 * @param ipList
	 * @return
	 */
	private void run(List<Word> ipList) {
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
				this.out.println("ERROR! -> " + this.source);
				this.state = State.explain; // 将状态切换回解释态，用于错误恢复
				break; //退出当前执行的Word列表
			}
			skipNextWord();
		}
	}

	/**
	 * Forth的代码加载程序
	 * 用于读取Forth库文件后启动Forth解释器的主循环
	 * @param str 单行合法的Forth代码
	 */
	public void load(String str) {
		this.source = str;
		this.wordListBuffer = getTokens(this.source);
		run(this.wordListBuffer);

	}
	
	/**
	 * 从文件中读取Forth源代码，支持冒号词的换行定义
	 * @param filePath 源代码文件的路径
	 */
	private void loadLib(String filePath) {
		String line;
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader vmReader = this.localReader; //先保存虚拟机的localReader
				this.localReader = new BufferedReader(read);
				while((line = read()) != null) {
					this.load(line);
				}
				
				read.close();
				this.localReader = vmReader; //恢复虚拟机的localReader
			} else {
				this.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			this.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}
	
	/**
	 * 跳过Word列表中的下一个Word词
	 */
	private void skipNextWord() {
		this.ip++;
	}
	
	/**
	 * 虚拟机的状态
	 */
	private enum State {
		explain, compile, error
	}
}
