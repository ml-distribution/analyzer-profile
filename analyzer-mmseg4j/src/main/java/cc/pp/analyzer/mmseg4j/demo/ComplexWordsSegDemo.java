package cc.pp.analyzer.mmseg4j.demo;

import java.io.IOException;

public class ComplexWordsSegDemo {

	public static void main(String[] args) throws IOException {

		Complex segW = new Complex();
		String words = segW.segWords("今天在淘宝上买了件衣服", " ");
		System.out.println(words);

	}

}
