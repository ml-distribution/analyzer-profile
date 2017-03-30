package info.bbd.analyzer.mmseg4j.demo;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import info.bbd.analyzer.mmseg4j.lucene.MaxWordAnalyzer;

/**
 * 使用IKAnalyzer进行分词的演示
 *
 * @author wanggang
 *
 */
public class MMSegAnalyzerDemo {

	public static void main(String[] args) {
		// 构建MMSegAnalyzer分词器
		//		Analyzer analyzer = new MMSegAnalyzer();
		//		Analyzer analyzer = new ComplexAnalyzer();
		Analyzer analyzer = new MaxWordAnalyzer();
		//		Analyzer analyzer = new SimpleAnalyzer();

		// 获取Lucene的TokenStream对象
		TokenStream ts = null;
		try {
			ts = analyzer.tokenStream("myfield",
					new StringReader("这是一个中文分词的例子，你可以直接运行它！MMSegAnalyzer can analysis english text too"));
			// 获取词元位置属性
			OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
			// 获取词元文本属性
			CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
			// 获取词元文本属性
			TypeAttribute type = ts.addAttribute(TypeAttribute.class);

			// 重置TokenStream（重置StringReader）
			ts.reset();
			//  迭代获取分词结果
			while (ts.incrementToken()) {
				System.out.println(offset.startOffset() + " - " + offset.endOffset() + " : " + term.toString() + " | "
						+ type.type());
			}
			// 关闭TokenStream（关闭StringReader）
			ts.end(); // Perform end-of-stream operations, e.g. set the final offset.

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放TokenStream的所有资源
			if (ts != null) {
				try {
					ts.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			analyzer.close();
		}

	}

}
