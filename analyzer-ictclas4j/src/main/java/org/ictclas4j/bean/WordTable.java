package org.ictclas4j.bean;

import java.util.ArrayList;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;


/**
 * 相同开头的词组表.
 * 
 * @author sinboy
 * 
 */
public class WordTable {
	// 词组集的数目
	private int count;

	private ArrayList<WordItem> words;
	
	public WordTable(){
		
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ArrayList<WordItem> getWords() {
		return words;
	}

	public void setWords(ArrayList<WordItem> words) {
		this.words = words;
	}
	
	public void setWords(WordItem[] wis){
		if(wis!=null){
			if(words==null)
				words=new ArrayList<WordItem>();
			for(WordItem wi:wis) 
				words.add(wi);
		 
		}
	}

	public String toString() {

		return ReflectionToStringBuilder.toString(this);

	}
}
