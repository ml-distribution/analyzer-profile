package org.apache.lucene.analysis.cn.smart.demo;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.cn.smart.WordSegmenter;
import org.apache.lucene.analysis.cn.smart.hhmm.SegToken;

public class WordSegmenterDemo {

	public static void main(String[] args) throws IOException {

		String data = "今天在淘宝的聚划算上买了一件衣服。";
		WordSegmenter wordSegmenter = new WordSegmenter();
		System.err.println("当前使用的分词器是： " + wordSegmenter.getClass().getSimpleName());
		List<SegToken> tokens = wordSegmenter.segmentSentence(data, 1);
		for (SegToken token : tokens) {
			System.out.print(String.valueOf(token.charArray) + "  ");
		}

	}

}
