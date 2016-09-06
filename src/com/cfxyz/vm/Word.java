package com.cfxyz.vm;

import java.util.List;

public class Word {
	private String name;
	private Type type;
	private List<Word> wplist;
	
	/**
	 * 创建一个Forth词，词的类型默认为Type.REVEAL
	 * @param name 词的名字
	 */
	public Word(String name) {
		this.name = name;
		this.type = Type.REVEAL;
	}
	
	public Word(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	public Word(String name, Type type, List<Word> wplist) {
		this.name = name;
		this.type = type;
		this.wplist = wplist;
	}

	public String getName() {
		return this.name;
	}

	public void setWplist(List<Word> wplist) {
		this.wplist = wplist;
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

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		if(this.wplist == null) {
			return this.name;
		} else {
			String str = "";
			for(Word w : this.wplist) {
				str += w.getName() + " ";
			}
			return str;
		}
	}

	public enum Type {
		REVEAL, IMMEDIATE, HIDE, CORE, VAR, CONST, ARRAY
	}
}
