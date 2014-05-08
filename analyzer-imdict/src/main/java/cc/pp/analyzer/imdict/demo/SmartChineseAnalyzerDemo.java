package cc.pp.analyzer.imdict.demo;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import cc.pp.analyzer.imdict.analyzer.SmartChineseAnalyzer;

public class SmartChineseAnalyzerDemo {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String data = "今天在淘宝的聚划算上买了一件衣服。";
		Analyzer ca = new SmartChineseAnalyzer(true);
		TokenStream ts = ca.tokenStream("sentence", new StringReader(data));

		Token nt = new Token();
		while ((nt = ts.next(nt)) != null) {
			System.out.print(nt.term() + "  ");
		}
		ts.close();
	}

}
