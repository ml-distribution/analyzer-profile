package cc.pp.analyzer.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 分词结果
 * @author wanggang
 *
 */
public class WordsList {

	private int count;
	private List<String> words = new ArrayList<>();

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

}
