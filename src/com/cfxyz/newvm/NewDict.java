package com.cfxyz.newvm;

public class NewDict {
	public DictSpace dictSpace ;
	public NewDict(DictSpace space) {
		this.dictSpace = space ;
	}
	public void addWord2(NewWord word) {
		this.dictSpace.last = this.dictSpace.here ;  //更新词头地址
		DictSpaceUtil.createLFA(word.getLfa(), this.dictSpace);
		
		int precedenceBit = 0;
		int smudgeBit = 0;
		if(word.getPrecedence().equals("IMMEDIATE")) {
			precedenceBit = 1 ;
		} else if(word.getPrecedence().equals("NORMAL")) {
			precedenceBit = 0 ;
		}
		if(word.getSmudge().equals("HIDE")) {
			smudgeBit = 1 ;
		} else if(word.getSmudge().equals("REVEAL")) {
			smudgeBit = 0 ;
		}
		DictSpaceUtil.createNFA(precedenceBit, smudgeBit, word.getWordName().length(), word.getWordName(), this.dictSpace);
		
		int cfa = 0 ;
		if(word.getCfa().equals("CORE")) {
			cfa = 0 ;
		} else if (word.getCfa().equals("COLON")) {
			cfa = 1 ;
		} else if (word.getCfa().equals("VAR")) {
			cfa = 2 ;
		} else if (word.getCfa().equals("CONSTANT")) {
			cfa = 3 ;
		}
		DictSpaceUtil.createCFA(cfa, this.dictSpace);
		
		DictSpaceUtil.createPFA(word.getPfa(), this.dictSpace);
	}
	
	public void addWord(int lfa, String precedence, String smudge, String name, String code, int[] pfa) {
		this.dictSpace.last = this.dictSpace.here ;  //更新词头地址
		
		DictSpaceUtil.createLFA(lfa, this.dictSpace);
		
		int precedenceBit = 0;
		int smudgeBit = 0;
		if(precedence.equals("IMMEDIATE")) {
			precedenceBit = 1 ;
		} else if(precedence.equals("NORMAL")) {
			precedenceBit = 0 ;
		}
		if(smudge.equals("HIDE")) {
			smudgeBit = 1 ;
		} else if(smudge.equals("REVEAL")) {
			smudgeBit = 0 ;
		}
		DictSpaceUtil.createNFA(precedenceBit, smudgeBit, name.length(), name, this.dictSpace);
		
		int cfa = 0 ;
		if(code.equals("CORE")) {
			cfa = 0 ;
		} else if (code.equals("COLON")) {
			cfa = 1 ;
		} else if (code.equals("VAR")) {
			cfa = 2 ;
		} else if (code.equals("CONSTANT")) {
			cfa = 3 ;
		}
		DictSpaceUtil.createCFA(cfa, this.dictSpace);
		
		DictSpaceUtil.createPFA(pfa, this.dictSpace);
	}
	
	public void listWord() {
		for(int x = this.dictSpace.last; x >= 0;) {
			if(this.dictSpace.space[x] == -1) {
				break ;
			} else {
				int next = this.dictSpace.space[x] ;
				x++;
				
				if(this.dictSpace.space[x] == 1) {
					System.out.println("Precedence = IMMEDIATE") ;
				} else if(this.dictSpace.space[x] == 0) {
					System.out.println("Precedence = NORMAL") ;
				}
				x++;
				if(this.dictSpace.space[x] == 1) {
					System.out.println("Smudge = HIDE") ;
				} else if(this.dictSpace.space[x] == 0) {
					System.out.println("Smudge = REVEAL") ;
				}
				x++;
				int wordLength = this.dictSpace.space[x] ;
				x++;
				char[] wordNameCharArray = new char[wordLength] ;
				for(int y = 0; y < wordLength; y++) {
					wordNameCharArray[y] = (char)this.dictSpace.space[x] ;
					x++;
				}
				System.out.println("WordName = " + String.valueOf(wordNameCharArray));
				
				if(this.dictSpace.space[x] == 0) {
					System.out.println("CFA = CORE") ;
				} else if (this.dictSpace.space[x] == 1) {
					System.out.println("CFA = COLON") ;
				} else if (this.dictSpace.space[x] == 2) {
					System.out.println("CFA = VAR") ;
				} else if (this.dictSpace.space[x] == 3) {
					System.out.println("CFA = CONSTANT") ;
				}
				x++;
				
				System.out.print("PFA = ");
				while(this.dictSpace.space[x] != 999) {
					System.out.print(this.dictSpace.space[x] + ", ");
					x++;
				}
				System.out.println(this.dictSpace.space[x] + "\n");
				
				x = next ;
			}
		}
	}
	
	@Override
	public String toString() {
		return this.dictSpace.toString();
	}
}
