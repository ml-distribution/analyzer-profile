package cc.pp.analyzer.ik.demo;

import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import cc.pp.analyzer.ik.lucene.IKAnalyzer;

/**
 * IKAnalyzer 分词器测试
 *
 * @author wanggang
 *
 */
public class IKAnalyzerWordSegmentDemo {

	public static void main(String[] args) throws Exception {

		String data = "今天在淘宝的聚划算上买了一件衣服。";
		IKAnalyzer analyzer = new IKAnalyzer();
		//使用智能分词
		analyzer.setUseSmart(true);
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