package info.bbd.analyzer.mmseg4j.lucene;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import info.bbd.analyzer.mmseg4j.core.ComplexSeg;
import info.bbd.analyzer.mmseg4j.core.Dictionary;
import info.bbd.analyzer.mmseg4j.core.MMSeg;
import info.bbd.analyzer.mmseg4j.core.MaxWordSeg;
import info.bbd.analyzer.mmseg4j.core.Seg;
import info.bbd.analyzer.mmseg4j.core.SimpleSeg;
import info.bbd.analyzer.mmseg4j.core.Word;
import info.bbd.analyzer.mmseg4j.core.Dictionary.FileLoading;
import info.bbd.analyzer.mmseg4j.lucene.ComplexAnalyzer;
import info.bbd.analyzer.mmseg4j.lucene.MaxWordAnalyzer;
import info.bbd.analyzer.mmseg4j.lucene.SimpleAnalyzer;
import info.bbd.analyzer.mmseg4j.lucene.TokenUtils;

public class AnalyzerTest {

	String txt = "";
	private static Dictionary dic;

	@BeforeClass
	public static void beforeClass() {
		dic = Dictionary.getInstance();
	}

	@AfterClass
	public static void afterClass() {
	}

	public static List<String> toMMsegWords(String txt, Seg seg) {
		List<String> words = new ArrayList<String>();
		MMSeg mmSeg = new MMSeg(new StringReader(txt), seg);
		Word word = null;
		try {
			while ((word = mmSeg.next()) != null) {
				String w = word.getString();
				words.add(w);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return words;
	}

	private static class AssertFileLoading implements FileLoading {

		Seg seg;
		Analyzer analyzer;

		public AssertFileLoading(Seg seg, Analyzer analyzer) {
			this.seg = seg;
			this.analyzer = analyzer;
		}

		@Override
		public void row(String line, int n) {
			List<String> mwords = toMMsegWords(line, seg);
			List<String> awords = toWords(line, analyzer);
			Assert.assertEquals("assert line[" + n + "] split word fail", mwords, awords);
		}

	}

	@Test
	public void testSimple() throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream("src/test/resources/text-sentence.txt");
		try {
			Dictionary.load(fis, new AssertFileLoading(new SimpleSeg(dic), new SimpleAnalyzer()));
		} finally {
			fis.close();
		}
	}

	@Test
	public void testStandard() throws IOException {
		FileInputStream fis = new FileInputStream("src/test/resources/text-sentence.txt");
		Dictionary.load(fis, new FileLoading() {

			StandardAnalyzer sa = new StandardAnalyzer();

			@Override
			public void row(String line, int n) {
				//System.out.println("n=" + n + " -> " + toWords(line, sa));
				// 保证标准的可运行
				toWords(line, sa);
			}

		});
	}

	@Test
	public void testComplex() throws IOException {
		FileInputStream fis = new FileInputStream("src/test/resources/text-sentence.txt");
		try {
			Dictionary.load(fis, new AssertFileLoading(new ComplexSeg(dic), new ComplexAnalyzer()));
		} finally {
			fis.close();
		}
	}

	@Test
	public void testMaxWord() throws IOException {
		FileInputStream fis = new FileInputStream("src/test/resources/text-sentence.txt");
		try {
			Dictionary.load(fis, new AssertFileLoading(new MaxWordSeg(dic), new MaxWordAnalyzer()));
		} finally {
			fis.close();
		}
	}

	public static void printlnToken(String txt, Analyzer analyzer) throws IOException {
		System.out.println("---------" + txt.length() + "\n" + txt);
		TokenStream ts = analyzer.tokenStream("text", new StringReader(txt));
		ts.reset();
		for (PackedTokenAttributeImpl t = new PackedTokenAttributeImpl(); (t = TokenUtils.nextToken(ts, t)) != null;) {
			System.out.println(t);
		}
		ts.close();
	}

	public static List<String> toWords(String txt, Analyzer analyzer) {
		List<String> words = new ArrayList<>();
		TokenStream ts = null;
		try {
			ts = analyzer.tokenStream("text", new StringReader(txt));
			ts.reset();
			for (PackedTokenAttributeImpl t = new PackedTokenAttributeImpl(); (t = TokenUtils.nextToken(ts, t)) != null;) {
				words.add(t.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ts != null) {
				try {
					ts.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return words;
	}

}
