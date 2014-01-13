package cc.pp.analyzer.smallseg.core;

import java.util.List;

public class SegResult {

	public List<String> recognised;

	public List<String> unrecognised;

	@Override
	public String toString(){
		return "r:" + recognised + "\r\nu:" + unrecognised;
	}
}
