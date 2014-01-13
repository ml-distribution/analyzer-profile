package cc.pp.analyzer.mmseg4j.examples;

import java.io.IOException;

public class SimpleWordsSegDemo {

	public static void main(String[] args) throws IOException {

		Simple segW = new Simple();
		String words = segW.segWords("我去年买了个表", " ");
		System.out.println(words);
	}

}
