package info.bbd.analyzer.mmseg4j.demo;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import info.bbd.analyzer.mmseg4j.lucene.SimpleAnalyzer;

/**
 * IKAnalyzer 分词器测试
 *
 * @author wanggang
 *
 */
public class MMSegAnalyzerWordSegmentDemo {

	public static void main(String[] args) throws Exception {

		String data = "今天在淘宝的聚划算上买了一件衣服。";
		//		Analyzer analyzer = new MMSegAnalyzer();
		//		Analyzer analyzer = new ComplexAnalyzer();
		//		Analyzer analyzer = new MaxWordAnalyzer();
		Analyzer analyzer = new SimpleAnalyzer();
		//打印分词结果
		TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(data));
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