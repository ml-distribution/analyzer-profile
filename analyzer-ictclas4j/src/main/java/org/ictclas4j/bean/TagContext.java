package org.ictclas4j.bean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class TagContext {
	private int key;// The key word

	private int[][] contextArray;// The context array

	private int[] tagFreq;// The total number a tag appears

	private int totalFreq;// The total number of all the tags

	public int[][] getContextArray() {
		return contextArray;
	}

	public void setContextArray(int[][] contextArray) {
		this.contextArray = contextArray;
	}

 
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int[] getTagFreq() {
		return tagFreq;
	}

	public void setTagFreq(int[] tagFreq) {
		this.tagFreq = tagFreq;
	}

	public int getTotalFreq() {
		return totalFreq;
	}

	public void setTotalFreq(int totalFreq) {
		this.totalFreq = totalFreq;
	}
	public String toString() {

		return ReflectionToStringBuilder.toString(this);

	}
}
