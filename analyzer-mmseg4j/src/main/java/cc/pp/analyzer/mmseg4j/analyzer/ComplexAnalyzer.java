package cc.pp.analyzer.mmseg4j.analyzer;

import java.io.File;

import cc.pp.analyzer.mmseg4j.ComplexSeg;
import cc.pp.analyzer.mmseg4j.Dictionary;
import cc.pp.analyzer.mmseg4j.Seg;

/**
 * mmseg 的 complex analyzer
 * 
 * @author chenlb 2009-3-16 下午10:08:16
 */
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

	protected Seg newSeg() {
		return new ComplexSeg(dic);
	}
}
