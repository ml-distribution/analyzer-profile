package cc.pp.analyzer.mmseg4j.wordseg;

import java.io.IOException;
import java.io.Reader;

import cc.pp.analyzer.mmseg4j.core.ComplexSeg;
import cc.pp.analyzer.mmseg4j.core.Dictionary;
import cc.pp.analyzer.mmseg4j.core.MMSeg;
import cc.pp.analyzer.mmseg4j.core.MaxWordSeg;
import cc.pp.analyzer.mmseg4j.core.SimpleSeg;
import cc.pp.analyzer.mmseg4j.core.Word;

/**
 * 三种分词器
 *
 * @author wanggang
 *
 */
public class MMSeg4jWordSeg {

	protected Dictionary dic;

	private final SimpleSeg simple;
	private final MaxWordSeg maxword;
	private final ComplexSeg complex;

	public MMSeg4jWordSeg() {
		dic = Dictionary.getInstance();
		simple = new SimpleSeg(dic);
		maxword = new MaxWordSeg(dic);
		complex = new ComplexSeg(dic);
	}

	/**
	 * 分词
	 *
	 * @param type: 分词器类别，0--simple, 1--maxword, 2--complex
	 * @param input
	 * @param wordSpilt
	 * @return
	 * @throws IOException
	 */
	public String segWords(int type, Reader input, String wordSpilt) throws IOException {

		StringBuilder sb = new StringBuilder();
		// 取得不同的分词具体算法
		MMSeg mmSeg = null;
		if (type == 0) {
			mmSeg = new MMSeg(input, simple);
		} else if (type == 1) {
			mmSeg = new MMSeg(input, maxword);
		} else {
			mmSeg = new MMSeg(input, complex);
		}
		Word word = null;
		boolean first = true;
		while ((word = mmSeg.next()) != null) {
			if (!first) {
				sb.append(wordSpilt);
			}
			String w = word.getString();
			sb.append(w);
			first = false;
		}

		return sb.toString();
	}

}
