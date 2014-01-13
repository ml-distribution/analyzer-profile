package org.ictclas4j.bean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 词条.包括词内容、长度、句柄及频度
 * 
 * @author sinboy
 * 
 */
public class WordItem {
  
	private String word;

	private int len;

	// 句柄，用来标识词的词性
	private int handle;

	// 频度，用来说明该词出现在语料库中的次数或概率
	private int freq;

	public int getFreq() {
		return freq;
	}

	public void setFreq(int frequency) {
		this.freq = frequency;
	}

	public int getHandle() {
		return handle;
	}

	public void setHandle(int handle) {
		this.handle = handle;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
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
