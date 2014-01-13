package cc.pp.analyzer.smallseg.core;

import java.util.List;

public class SmallSeg {

	private static Seg seg = new Seg();

	static{
		seg.useDefaultDict();
	}

	public static List<String> cut(String text){
		List<String> sr = seg.cut(text);
		return sr;
	}
}
