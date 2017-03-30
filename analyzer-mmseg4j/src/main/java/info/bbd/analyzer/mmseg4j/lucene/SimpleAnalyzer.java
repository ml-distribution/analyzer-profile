package info.bbd.analyzer.mmseg4j.lucene;

import java.io.File;

import info.bbd.analyzer.mmseg4j.core.Dictionary;
import info.bbd.analyzer.mmseg4j.core.Seg;
import info.bbd.analyzer.mmseg4j.core.SimpleSeg;

public class SimpleAnalyzer extends MMSegAnalyzer {

	public SimpleAnalyzer() {
		super();
	}

	public SimpleAnalyzer(String path) {
		super(path);
	}

	public SimpleAnalyzer(Dictionary dic) {
		super(dic);
	}

	public SimpleAnalyzer(File path) {
		super(path);
	}

	@Override
	protected Seg newSeg() {
		return new SimpleSeg(dic);
	}

}
