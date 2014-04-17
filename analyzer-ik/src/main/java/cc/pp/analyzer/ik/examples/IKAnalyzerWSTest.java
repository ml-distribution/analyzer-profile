package cc.pp.analyzer.ik.examples;

import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import cc.pp.analyzer.ik.lucene.IKAnalyzer;


/**
 * IKAnalyzer 分词器测试
 * Lucene 3.5
 * @author WG
 */
public class IKAnalyzerWSTest {

	public static void main(String[] args) throws Exception {

		String data = "今天在淘宝的聚划算上买了一件衣服。";
		IKAnalyzer analyzer = new IKAnalyzer(Version.LUCENE_47);
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