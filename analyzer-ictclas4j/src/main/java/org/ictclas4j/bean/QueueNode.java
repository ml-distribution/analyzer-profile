package org.ictclas4j.bean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 队列节点
 * @author sinboy
 *
 */
public class QueueNode {
	private int parent;//父节点的位置

	private int index;//分词路径的编号,即第几条分词路径

	private double weight;//权重

	public QueueNode() {

	}

	public QueueNode(int parent, int index, double weight) {
		this.parent = parent;
		this.index = index;
		this.weight = weight;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public String toString() {

		return ReflectionToStringBuilder.toString(this);

	}
}
