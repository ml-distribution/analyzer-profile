package cc.pp.analyzer.mmseg4j.demo;

import java.io.IOException;

public class MMSeg4jDemo {

	public static void main(String[] args) throws IOException {

		SimpleDemo simple = new SimpleDemo();
		String words = simple.segWords("今天在淘宝上买了件衣服", " ");
		System.out.println(words);

		ComplexDemo complex = new ComplexDemo();
		words = complex.segWords("今天在淘宝上买了件衣服", " ");
		System.out.println(words);

		MaxWordDemo maxword = new MaxWordDemo();
		words = maxword.segWords("今天在淘宝上买了件衣服", " ");
		System.out.println(words);

	}

}
