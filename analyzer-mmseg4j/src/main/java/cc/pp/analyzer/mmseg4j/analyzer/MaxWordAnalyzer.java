package cc.pp.analyzer.mmseg4j.analyzer;

import java.io.File;

import cc.pp.analyzer.mmseg4j.Dictionary;
import cc.pp.analyzer.mmseg4j.MaxWordSeg;
import cc.pp.analyzer.mmseg4j.Seg;


/**
 * 最多分词方式.
 * 
 * @author chenlb 2009-4-6 下午08:43:46
 */
public class MaxWordAnalyzer extends MMSegAnalyzer {

	public MaxWordAnalyzer() {
		super();
	}

	public MaxWordAnalyzer(String path) {
		super(path);
	}

	public MaxWordAnalyzer(Dictionary dic) {
		super(dic);
	}

	public MaxWordAnalyzer(File path) {
		super(path);
	}

	protected Seg newSeg() {
		return new MaxWordSeg(dic);
	}
}
