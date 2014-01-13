package cc.pp.analyzer.mmseg4j.analyzer;

import java.io.File;

import cc.pp.analyzer.mmseg4j.Dictionary;
import cc.pp.analyzer.mmseg4j.Seg;
import cc.pp.analyzer.mmseg4j.SimpleSeg;

/**
 * mmseg 的 simple anlayzer.
 * 
 * @author chenlb 2009-3-16 下午10:08:13
 */
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

	protected Seg newSeg() {
		return new SimpleSeg(dic);
	}
}
