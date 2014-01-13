package org.ictclas4j.bean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 词性标记
 * 
 * @author sinboy
 * @since 2007.5.22
 * 
 */
public class POS {
	private int tag;// 词性标记，如：nr，高8位代表词性的第一个字符，低8位代表词的第二个字符

	private double freq;// 该词性出现的频率
	
	private int prev;//前一个词的N个词性中和该词性最匹配的那一个（下标位置）
	
	private boolean isBest;
	
	public  POS(){
		
	}
	
	public POS(int pos,double value){
		this.tag=pos;
		this.freq=value;
	}
	
	public int getTag() {
		return tag;
	}

	public void setTag(int pos) {
		this.tag = pos;
	}

	public double getFreq() {
		return freq;
	}

	public void setFreq(double value) {
		this.freq = value;
	}
  

	public int getPrev() {
		return prev;
	}

	public void setPrev(int prevPos) {
		this.prev = prevPos;
	}

	public boolean isBest() {
		return isBest;
	}

	public void setBest(boolean isBest) {
		this.isBest = isBest;
	}

	public String toString() {

		return ReflectionToStringBuilder.toString(this);

	}
}
