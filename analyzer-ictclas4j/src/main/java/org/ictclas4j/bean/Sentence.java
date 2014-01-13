package org.ictclas4j.bean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class Sentence {
	private String content;

	// 是否需要进行分词.回车换行和空格是不需要的
	private boolean isSeg;

	public Sentence(){
		
	}
	public Sentence(String content){
		this.content=content;
	}
	public Sentence(String content,boolean isSeg){
		this.content=content;
		this.isSeg=isSeg;
	}
 
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isSeg() {
		return isSeg;
	}

	public void setSeg(boolean isSeg) {
		this.isSeg = isSeg;
	}
	public String toString() {

		return ReflectionToStringBuilder.toString(this);

	}
}
