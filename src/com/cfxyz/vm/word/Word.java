package com.cfxyz.vm.word;

import java.util.List;

public class Word {
	private String name ;
	private Type type = Word.Type.REVEAL ;
	private List<Word> wplist ;
	public Word(String name) {
		this.name = name ;
	}
	public Word(String name, Type type) {
		this.name = name ;
		this.type = type ;
	}
	public String getName() {
		return this.name ;
	}
	public void setWplist(List<Word> wplist) {
		this.wplist = wplist ;
	}
	public List<Word> getWplist() {
		return this.wplist;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {
		return type;
	}
	@Override
	public String toString() {
		String wp = this.wplist == null ? "" : " , Value = " + this.wplist ; 
		return "[Name = " + this.name + wp +"]";
	}
	
	public enum Type { REVEAL, IMMEDIATE, HIDE, CORE }
}
