package cc.pp.analyzer.mmseg4j.demo;

import java.io.IOException;

import cc.pp.analyzer.mmseg4j.MaxWordSeg;
import cc.pp.analyzer.mmseg4j.Seg;

public class MaxWordDemo extends ComplexDemo {

	@Override
	protected Seg getSeg() {
		return new MaxWordSeg(dic);
	}

	public static void main(String[] args) throws IOException {
		new MaxWordDemo().run(args);
	}
}
