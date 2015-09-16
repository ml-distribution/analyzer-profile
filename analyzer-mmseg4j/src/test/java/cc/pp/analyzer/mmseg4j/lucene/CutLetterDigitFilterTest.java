package cc.pp.analyzer.mmseg4j.lucene;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Tokenizer;
import org.junit.Assert;
import org.junit.Test;

public class CutLetterDigitFilterTest {

	@Test
	public void testCutLeeterDigitFilter() throws IOException {
		String myTxt = "mb991ch cq40-519tx mmseg4j ";
		List<String> words = AnalyzerTestOne.toWords(myTxt, new MMSegAnalyzer("") {

			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				Tokenizer t = new MMSegTokenizer(newSeg());
				return new TokenStreamComponents(t, new CutLetterDigitFilter(t));
			}

		});

		Assert.assertArrayEquals("CutLeeterDigitFilter fail", words.toArray(new String[words.size()]),
				"mb 991 ch cq 40 519 tx mmseg 4 j".split(" "));
	}

}
