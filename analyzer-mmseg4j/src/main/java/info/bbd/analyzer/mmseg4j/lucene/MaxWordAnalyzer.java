package info.bbd.analyzer.mmseg4j.lucene;

import java.io.File;

import info.bbd.analyzer.mmseg4j.core.Dictionary;
import info.bbd.analyzer.mmseg4j.core.MaxWordSeg;
import info.bbd.analyzer.mmseg4j.core.Seg;

/**
 * 最多分词方式
 *
 * @author wanggang
 *
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

	@Override
	protected Seg newSeg() {
		return new MaxWordSeg(dic);
	}

}
