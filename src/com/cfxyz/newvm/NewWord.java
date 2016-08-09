package com.cfxyz.newvm;

public class NewWord {
	private int lfa ;
	private String precedence ;
	private String smudge ;
	private String wordName;
	private String cfa;
	private int[] pfa ;
	public NewWord(int lfa, String precedence, String smudge, String wordName, String cfa, int[] pfa) {
		this.lfa = lfa;
		this.precedence = precedence;
		this.smudge = smudge;
		this.wordName = wordName;
		this.cfa = cfa;
		this.pfa = pfa;
	}
	public int getLfa() {
		return lfa;
	}
	public void setLfa(int lfa) {
		this.lfa = lfa;
	}
	public String getPrecedence() {
		return precedence;
	}
	public void setPrecedence(String precedence) {
		this.precedence = precedence;
	}
	public String getSmudge() {
		return smudge;
	}
	public void setSmudge(String smudge) {
		this.smudge = smudge;
	}
	public String getWordName() {
		return wordName;
	}
	public void setWordName(String wordName) {
		this.wordName = wordName;
	}
	public String getCfa() {
		return cfa;
	}
	public void setCfa(String cfa) {
		this.cfa = cfa;
	}
	public int[] getPfa() {
		return pfa;
	}
	public void setPfa(int[] pfa) {
		this.pfa = pfa;
	}
	
}
