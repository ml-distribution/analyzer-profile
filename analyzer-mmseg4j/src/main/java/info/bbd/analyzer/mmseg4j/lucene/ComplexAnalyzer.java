package info.bbd.analyzer.mmseg4j.lucene;

import java.io.File;

import info.bbd.analyzer.mmseg4j.core.ComplexSeg;
import info.bbd.analyzer.mmseg4j.core.Dictionary;
import info.bbd.analyzer.mmseg4j.core.Seg;

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
