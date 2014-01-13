package org.apache.lucene.analysis.cn.examples;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class SmartChineseAnalyzerDemo {

	public static void main(String[] args) throws IOException {

		final String text = "今天在淘宝的聚划算上买了一件衣服。";
		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_46, false);
		TokenStream tokenStream = analyzer.tokenStream("myfiled", new StringReader(text));
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				System.out.println(charTermAttribute.toString());
			}
			tokenStream.end();
		} finally {
			tokenStream.close();
			analyzer.close();
		}
	}

}
