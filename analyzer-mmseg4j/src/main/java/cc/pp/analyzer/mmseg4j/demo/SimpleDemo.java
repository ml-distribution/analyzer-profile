package cc.pp.analyzer.mmseg4j.demo;

import java.io.IOException;

import cc.pp.analyzer.mmseg4j.Seg;
import cc.pp.analyzer.mmseg4j.SimpleSeg;

/**
 * 
 * @author chenlb 2009-3-14 上午12:38:40
 */
public class SimpleDemo extends ComplexDemo {

	@Override
	protected Seg getSeg() {
		return new SimpleSeg(dic);
	}

	public static void main(String[] args) throws IOException {
		new SimpleDemo().run(args);
	}

}
