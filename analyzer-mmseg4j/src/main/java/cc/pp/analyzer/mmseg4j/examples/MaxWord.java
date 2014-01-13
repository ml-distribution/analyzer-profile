package cc.pp.analyzer.mmseg4j.examples;

import java.io.IOException;

import cc.pp.analyzer.mmseg4j.MaxWordSeg;
import cc.pp.analyzer.mmseg4j.Seg;

public class MaxWord extends Complex {

	protected Seg getSeg() {

		return new MaxWordSeg(dic);
	}

	public static void main(String[] args) throws IOException {
		new MaxWord().run(args);
	}
}
