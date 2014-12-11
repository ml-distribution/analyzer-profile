package org.ansj.splitWord.analysis;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.junit.Test;

public class ToAnalysisTest {

	@Test
	public void testParse() {
		assertEquals("[我, 想, 说, ,, 这事/r, 的确, 定, 不, 下来, ,, 我, 得, 想, !]", ToAnalysis.parse("我想说,这事的确定不下来,我得想!")
				.toString());
	}

	@Test
	public void testRecognition() {
		String str = "结婚的和尚未结婚的孙建是一个好人";
		List<Term> terms = ToAnalysis.parse(str);
		new NatureRecognition(terms).recognition();
		assertEquals("[结婚/v, 的/uj, 和/c, 尚未/d, 结婚/v, 的/uj, 孙建/nr, 是/v, 一个/m, 好/a, 人/n]", terms.toString());
	}

}
