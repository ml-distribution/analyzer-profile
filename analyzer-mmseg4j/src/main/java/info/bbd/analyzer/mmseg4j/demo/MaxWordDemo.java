package info.bbd.analyzer.mmseg4j.demo;

import java.io.IOException;

import info.bbd.analyzer.mmseg4j.core.MaxWordSeg;
import info.bbd.analyzer.mmseg4j.core.Seg;

public class MaxWordDemo extends ComplexDemo {

	@Override
	protected Seg getSeg() {
		return new MaxWordSeg(dic);
	}

	public static void main(String[] args) throws IOException {
		new MaxWordDemo().run(args);
	}

}
