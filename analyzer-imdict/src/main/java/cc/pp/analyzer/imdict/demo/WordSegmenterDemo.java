package cc.pp.analyzer.imdict.demo;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import cc.pp.analyzer.imdict.core.SentenceTokenizer;
import cc.pp.analyzer.imdict.core.WordSegmenter;

public class WordSegmenterDemo {

	public static void main(String[] args) throws IOException {

		String data = "今天在淘宝的聚划算上买了一件衣服";
		WordSegmenter wordSegmenter = new WordSegmenter();
		System.err.println("当前使用的分词器是： " + wordSegmenter.getClass().getSimpleName());
		Token sentenceToken = new Token();
		TokenStream in = new SentenceTokenizer(new StringReader(data));
		List<Token> tokens = wordSegmenter.segmentSentence(in.next(sentenceToken), 1);
		for (Token token : tokens) {
			System.out.print(token.term() + "  ");
		}

	}

}
