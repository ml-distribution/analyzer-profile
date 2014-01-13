package org.ictclas4j.bean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * </pre>
 * 
 * 原子
 * 
 * 所谓原子,就是按照分词的最小单位进行分隔.比如需要分词的每一个汉字都是一个原子,
 * 别外开始标记和结束标记虽然一是一个字符,但也认为是原子,比如:始##始,它已无法再进行 切分了.
 * 
 * 比如:源字符串为"始##始他说的确实在理",经过原子分隔后为: 始##始 他 说 的 确 实 在 理
 * 
 * </pre>
 * 
 * @author sinboy
 * 
 */
public class Atom {
	private String word;

	private int pos;

	private int len;

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
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
