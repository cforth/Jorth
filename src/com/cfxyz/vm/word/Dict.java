package com.cfxyz.vm.word;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Dict extends ArrayList<Word>{
	public Dict() {
	}
	
	public boolean containsName(String name) {
		for(int x = this.size() - 1; x >= 0; x--) { //´Ó´ÊµäÄ©¶Ë¿ªÊ¼ËÑË÷
			if(name.equals(this.get(x).getName())) {
				return true ;
			}
		}
		return false ;
	}
	
	public Word findName(String name) {
		for(int x = this.size() - 1; x >= 0; x--) { //´Ó´ÊµäÄ©¶Ë¿ªÊ¼ËÑË÷
			if(name.equals(this.get(x).getName())) {
				return this.get(x) ;
			}
		}
		return null ;
	}
	
	public boolean setName(String name, Word word) {
		for(int x = this.size() - 1; x >= 0; x--) { //´Ó´ÊµäÄ©¶Ë¿ªÊ¼ËÑË÷
			if(name.equals(this.get(x).getName())) {
				this.set(x, word) ;
				return true ;
			}
		}
		return false ;
	}
}
