package org.ictclas4j.bean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class WordResult {
	private String word;

	private int handle;

	private double value;// The -log(frequency/MAX)

	public int getHandle() {
		return handle;
	}

	public void setHandle(int handle) {
		this.handle = handle;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
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
