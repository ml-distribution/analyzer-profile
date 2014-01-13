package cc.pp.analyzer.smallseg.examples;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import cc.pp.analyzer.smallseg.core.Seg;

public class Benchmark {

	public static void main(String[] args) throws IOException {

		Seg seg = new Seg();
		seg.useDefaultDict();
		File textFile = new File(Thread.currentThread().getContextClassLoader().getResource("text.txt").getPath()
				.replace("%20", " "));
		String text = FileUtils.readFileToString(textFile,"utf-8");
		for (int i = 1; i < 101; i++) {
			long start = System.currentTimeMillis();
			for (int j = 0; j < i; j++)
				seg.cut(text);
			long cost = System.currentTimeMillis() - start;
			System.out.println(i + "times,cost:" + cost);
		}
	}
}
