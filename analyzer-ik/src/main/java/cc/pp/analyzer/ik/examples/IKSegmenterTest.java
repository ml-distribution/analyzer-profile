package cc.pp.analyzer.ik.examples;

import java.io.IOException;
import java.io.StringReader;

import cc.pp.analyzer.ik.core.IKSegmenter;
import cc.pp.analyzer.ik.core.Lexeme;

public class IKSegmenterTest {

	public static void main(String[] args) {

		String data = "今天天气,你大爷从事数据挖掘工作，今天闲着没事在淘宝的聚划算上买了一件衣服。";
		if (args.length > 0) {
			data = args[0];
		}
		IKSegmenter iKSegment = new IKSegmenter(new StringReader(data), true);
		System.err.println("当前使用的分词器是： " + iKSegment.getClass().getSimpleName());
		Lexeme lexeme = null;
		try {
			while ((lexeme = iKSegment.next()) != null) {
				System.out.print(lexeme.getLexemeText() + "  ");
			}
			System.out.println("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
