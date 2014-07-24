package org.ansj.domain;

import java.io.Serializable;

/**
 * google语义模型用到的词与词之间的关联频率
 * 
 * @author wanggang
 *
 */
public class BigramEntry implements Comparable<BigramEntry>, Serializable {

	private static final long serialVersionUID = 5994821814571715244L;

	public int id;
	public int freq;

	public BigramEntry(int id, int freq) {
		this.id = id;
		this.freq = freq;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		return this.id == obj.hashCode();
	}

	@Override
	public int compareTo(BigramEntry o) {
		return this.id - o.id;
	}

}
