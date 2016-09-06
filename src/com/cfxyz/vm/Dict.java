package com.cfxyz.vm;

import java.util.ArrayList;
import java.util.List;

public class Dict {
	private List<Word> dict;
	private int size;
	
	public Dict() {
		this.dict = new ArrayList<Word>();
	}
	
	public boolean add(Word word) {
		this.size ++;
		return this.dict.add(word);
	}
	
	public int size() {
		return this.size;
	}
	
	public Word get(int index) {
		return this.dict.get(index);
	}
	
	public Word set(int index, Word word) {
		return this.dict.set(index, word);
	}
	
	public int lastIndexOf(Word word) {
		return this.dict.lastIndexOf(word);
	}

	public boolean containsName(String name) {
		for (int x = this.dict.size() - 1; x >= 0; x--) { // ´Ó´ÊµäÄ©¶Ë¿ªÊ¼ËÑË÷
			if (name.equals(this.dict.get(x).getName())) {
				return true;
			}
		}
		return false;
	}

	public Word findByName(String name) {
		for (int x = this.dict.size() - 1; x >= 0; x--) { // ´Ó´ÊµäÄ©¶Ë¿ªÊ¼ËÑË÷
			if (name.equals(this.dict.get(x).getName())) {
				return this.dict.get(x);
			}
		}
		return null;
	}

	public boolean setByName(String name, Word word) {
		for (int x = this.dict.size() - 1; x >= 0; x--) { // ´Ó´ÊµäÄ©¶Ë¿ªÊ¼ËÑË÷
			if (name.equals(this.dict.get(x).getName())) {
				this.dict.set(x, word);
				return true;
			}
		}
		return false;
	}

	public Word getLastWord() {
		return this.dict.get(this.dict.size() - 1);
	}
}
