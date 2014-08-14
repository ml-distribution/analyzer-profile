package cc.pp.analyzer.profiling;

import java.io.IOException;
import java.io.StringReader;

import cc.pp.analyzer.ik.core.IKSegmenter;
import cc.pp.analyzer.ik.core.Lexeme;

public class AnalyzerProfiling {

	public static void main(String[] args) throws IOException {

		String testData = TestingData.WEIBO_TEXT;
		//		String bigTestData = TestingData.bigTestData("big_test_data.txt");
		//		String veryBigTestData = TestingData.bigTestData("very_big_test_data.txt");
		System.err.println("待分词的数据为： ");
		System.out.println(testData);
		System.out.println();

		AnalyzerProfiling.analyzer_IK(testData);

	}

	public static void analyzer_IK(String data) throws IOException {

		IKSegmenter iKImplement = new IKSegmenter(new StringReader(data), true);
		System.err.println("当前使用的分词器：" + iKImplement.getClass().getSimpleName());
		Lexeme lexeme = null;
		while ((lexeme = iKImplement.next()) != null) {
			System.out.print(lexeme.getLexemeText() + "  ");
		}
		System.out.println();
	}

	public static void analyzer_Imdict(String data) {
		//
	}

}
