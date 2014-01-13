package org.ictclas4j.bean;

import java.util.ArrayList;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.ictclas4j.utility.Utility;


/**
 * 分词图表中的节点，实际表示图中的一条边
 * 
 * @author sinboy
 * 
 */
public class SegNode implements Cloneable {

	private int col;// 表示边的后驱，即终点

	private int row;// 表示边的前驱，即起点

	private double value;// 词频值

	private int pos;// 词性

	private ArrayList<POS> allPos;// 词性，一个词可能对应多个词性

	private String word;// 边的内容，可能有这样的表示形式：末＃＃数、末＃＃时等

	private String srcWord;// 分词前对应的原始内容

	public SegNode() {

	}

	public SegNode(int row, int col, int pos, double value, String word) {
		this.row = row;
		this.col = col;
		this.pos = pos;
		this.value = value;
		setWord(word);

	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getLen() {
		return srcWord != null ? srcWord.getBytes().length : -1;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public void addPos(POS pos) {
		if (allPos == null)
			allPos = new ArrayList<POS>();
		this.allPos.add(pos);
	}

	public ArrayList<POS> getAllPos() {
		return allPos;
	}

	public void setAllPos(ArrayList<POS> posList) {
		this.allPos = posList;
	}

	public int getPosSize() {
		return allPos != null ? allPos.size() : -1;

	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public String getWord() {
		return word;
	}

	public void appendWord(String word) {
		if (this.word == null)
			this.word = word;
		else
			this.word += word;
		setWord(word);
	}

	public void setWord(String word) {
		this.word = word;
		if (!Utility.isUnknownWord(word))
			this.srcWord = word;
	}

	public String getSrcWord() {
		return srcWord;
	}

	public void setSrcWord(String srcWord) {
		this.srcWord = srcWord;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String toString() {

		return ReflectionToStringBuilder.toString(this);

	}

	public SegNode clone() {
		SegNode result = null;
		try {
			result = (SegNode) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}
}
