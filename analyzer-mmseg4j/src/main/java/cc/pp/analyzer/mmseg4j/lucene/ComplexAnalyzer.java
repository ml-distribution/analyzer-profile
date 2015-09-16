package cc.pp.analyzer.mmseg4j.lucene;

import java.io.File;

import cc.pp.analyzer.mmseg4j.core.ComplexSeg;
import cc.pp.analyzer.mmseg4j.core.Dictionary;
import cc.pp.analyzer.mmseg4j.core.Seg;

public class ComplexAnalyzer extends MMSegAnalyzer {

	public ComplexAnalyzer() {
		super();
	}

	public ComplexAnalyzer(String path) {
		super(path);
	}

	public ComplexAnalyzer(Dictionary dic) {
		super(dic);
	}

	public ComplexAnalyzer(File path) {
		super(path);
	}

	@Override
	protected Seg newSeg() {
		return new ComplexSeg(dic);
	}

}
