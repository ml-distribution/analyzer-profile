package org.ictclas4j.bean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 预处理后的词条.
 * @author sinboy
 *
 */
public class Preword {
	//词
	private String word;
	
	//去掉第一个字后剩余的部分
	private String res;
	
	//词在词典表中出现的位置，即词的首字在区位码表中对应的偏移位置.比如：啊－－0
	private int index;
	
	 
	public int getIndex() {
		return index;
	}
	public void setIndex(int pos) {
		this.index = pos;
	}
	public String getRes() {
		return res;
	}
	public void setRes(String res) {
		this.res = res;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	 
	public String toString() {

		return ReflectionToStringBuilder.toString(this);

	}
	
}
