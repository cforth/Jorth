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
	private Stack<Integer> paramStack; // ����ջ
	private Stack<Integer> returnStack; // ����ջ
	private Dict dict; // �ʵ�
	private int ip; // ָ������ִ�еĴ�
	private State state; // ���������״̬
	private Word next; // ���ұ���ǰ��һ����
	private String source = null; // ָ��ǰ�����Դ�����ַ���
	private List<Word> wordListBuffer = null; // �������ַ���תΪWord�б�󱣴�����
	private BufferedReader localReader = null;

	/**
	 * Forth���������ʼ��
	 */
	public Jorth() {
		this.paramStack = new Stack<Integer>();
		this.returnStack = new Stack<Integer>();
		this.dict = new Dict();
		loadCoreWords(this.dict);
		this.localReader = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * Forth�Ľ��ͳ���
	 * 
	 * @param str
	 */
	public void interpret(String str) {
		this.source = str;
		parse(this.source);
		run(this.wordListBuffer);

	}

	/**
	 * ��һ��Forth����ת��ΪWord�б�
	 * 
	 * @param line
	 */
	public void parse(String line) {
		System.out.println("��ִ����䡿" + line);

		List<String> source = VmUtil.separateWord(line);
		this.wordListBuffer = new ArrayList<Word>(); // ��������Ĵ������ڴ������еģ���IPָ�����
		// �ı������������һ��Դ�����е�ÿ���ʣ�����һ��Word�б�
		for (String s : source) {
			Word w = this.dict.findByName(s);
			if (w != null) {
				this.wordListBuffer.add(w);
			} else { // ����ʵ���û�иôʣ����½�һ����ʱ�������
				this.wordListBuffer.add(new Word(s));
			}
		}
		this.wordListBuffer.add(this.dict.findByName("END"));
		// System.out.println(this.wordListBuffer);
	}

	/**
	 * ��Word�б�ִ��
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
				this.state = State.explain; // ��״̬�л��ؽ���̬�����ڴ���ָ�
			}
			this.ip++;
		}
	}

	/**
	 * ִ�е�ǰWord��
	 * 
	 * @param now
	 */
	public void explain(Word now) {
		String symbol = now.getName();
		String nextSymbol = this.next.getName();
		if (VmUtil.validateInteger(symbol)) { // ��������־�ѹ�����ջ
			this.paramStack.push(Integer.parseInt(symbol));
		} else if ("BYE".equals(symbol)) {
			System.exit(0);
		} else if ("PARSE".equals(symbol)) {
			try { // �ӱ�׼�����ȡ����
				this.source = this.localReader.readLine();
				parse(this.source);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("RUN".equals(symbol)) {
			this.returnStack.push(this.ip); // ���÷��ص�ַ
			run(this.wordListBuffer);
			this.ip = this.returnStack.pop();
		} else if ("CONSTANT".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, Word.Type.CONST)); // �ڴʵ������һ���µĳ���
			List<Word> varBuffer = new ArrayList<Word>();
			varBuffer.add(new Word(String.valueOf(this.paramStack.pop())));
			this.dict.get(this.dict.size() - 1).setWplist(varBuffer);
			this.ip++;
		} else if ("VARIABLE".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, Word.Type.VAR)); // �ڴʵ������һ���µı���
			List<Word> varBuffer = new ArrayList<Word>();
			varBuffer.add(new Word("0"));
			this.dict.get(this.dict.size() - 1).setWplist(varBuffer);
			this.ip++;
		} else if ("CREATE".equals(symbol)) { // Ŀǰֻ�����ڶ�������
			this.dict.add(new Word(nextSymbol, Word.Type.ARRAY)); // �ڴʵ������һ���µ�����
			this.ip++;
		} else if ("ALLOT".equals(symbol)) { // �����������з���ռ�
			int arraySize = this.paramStack.pop();
			List<Word> arrayBuffer = new ArrayList<Word>();
			for (int x = 0; x < arraySize; x++) {
				arrayBuffer.add(new Word("0"));
			}
			this.dict.get(this.dict.size() - 1).setWplist(arrayBuffer);
		} else if ("@".equals(symbol)) {
			int varIndex = this.paramStack.pop(); // ȡ�����ڴʵ��е�λ��
			int varDev = this.paramStack.pop(); // ȡ�����������ĳ�ʼƫ����������Ϊ0
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
		} else if (".\"".equals(symbol)) { // ��ӡ�������ַ���
			System.out.print(nextSymbol.replace("\"", ""));
			this.ip++;
		} else if ("R>".equals(symbol)) {
			this.paramStack.push(this.returnStack.pop());
		} else if (">R".equals(symbol)) {
			this.returnStack.push(this.paramStack.pop());
		} else if ("SEE".equals(symbol)) {
			System.out.println(this.dict.findByName(nextSymbol));
			this.ip++;
		} else if ("SIZE".equals(symbol)) { // �ʵ䳤��
			this.paramStack.push(this.dict.size());
		} else if ("PRINTWORD".equals(symbol)) { // ��ջ��������Ϊ�ʵ��дʵ��±꣬��ӡ���ʵĶ���
			System.out.println(this.dict.get(this.paramStack.pop()));
		} else if (".".equals(symbol)) {
			System.out.println(this.paramStack.pop());
		} else if (".s".equals(symbol)) {
			System.out.println("DS> " + this.paramStack.toString());
			System.out.println("RS> " + this.returnStack.toString());
		} else if (":".equals(symbol)) {
			this.dict.add(new Word(nextSymbol, new ArrayList<Word>())); // �ڴʵ������һ���µ�ð�Ŵ�
			this.ip++;
			this.state = State.compile;
		} else if ("?BRANCH".equals(symbol)) {
			if (this.paramStack.pop() == 0) {
				this.ip += Integer.parseInt(nextSymbol);
			} else {
				this.ip++; // ��������һ��λ��
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
			this.paramStack.push(0); // �ڲ���ջ���±��
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
				this.returnStack.push(this.ip); // ���÷��ص�ַ
				run(word.getWplist());// �ݹ����run����
				this.ip = this.returnStack.pop();
			}
		} else {
			this.state = State.error;
		}
	}

	/**
	 * ���뵱ǰWord��
	 * 
	 * @param now
	 */
	public void compile(Word now) {
		String symbol = now.getName();
		Word word = this.dict.findByName(symbol);
		if (".\"".equals(symbol)) {
			this.dict.getLastWord().getWplist().add(word);
			this.dict.getLastWord().getWplist().add(this.next); // ������ַ����������ͱ�����ʵ���
			this.ip++;
		} else if (";".equals(symbol)) {
			this.dict.getLastWord().getWplist().add(this.dict.findByName("END"));
			this.dict.get(this.dict.size() - 1).setWplist(this.dict.getLastWord().getWplist()); // Ϊ�µ�ð�Ŵ�����wplist
			this.state = State.explain;
		} else if ("[".equals(symbol)) {
			this.state = State.explain;
		} else if (word != null) { // ����ڴʵ����ж���
			if (word.getType().toString().equals("IMMEDIATE")) {
				this.explain(now);
				this.state = State.compile;
			} else {
				this.dict.getLastWord().getWplist().add(word);
			}
		} else {
			if (VmUtil.validateInteger(symbol)) { // ��������־ͱ�������ֳ���
				this.dict.getLastWord().getWplist().add(new Word(symbol));
			} else {
				System.out.println(symbol);
				this.state = State.error;
			}
		}
	}

	/**
	 * ���غ��Ĵʵ��ʵ��� �����ڴʵ�����Ӻ��ĴʵĿմʣ����Ĵ���ͨ��explain����ģ��ִ��
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
	 * ���ļ��ж�ȡForthԴ���룬֧��ð�ŴʵĻ��ж���
	 * @param filePath Դ�����ļ���·��
	 */
	public void loadLib(String filePath) {
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// ���ǵ������ʽ
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				boolean colonFlag = false; // ����ð�Ŵʻ��ж���
				String colonTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					//���ļ��ж�ȡԴ����ʱ������ð�Ŵ������ж���
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
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
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
		System.out.println("������ջ��" + this.getParamStack().toString());
		System.out.println("������ջ��" + this.getReturnStack().toString());
	}
}
