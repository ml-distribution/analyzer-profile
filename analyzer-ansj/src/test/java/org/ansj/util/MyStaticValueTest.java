package org.ansj.util;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ansj.app.crf.SplitWord;
import org.junit.Test;

public class MyStaticValueTest {

	@Test
	public void CRFSplitTest() {
		SplitWord split = MyStaticValue.getCRFSplitWord();
		List<String> cut = split.cut("协会主席亚拉·巴洛斯说他们是在1990年开始寻找野生金刚鹦鹉的");
		Set<String> words = new HashSet<String>(cut);
		assertTrue(words.contains("亚拉·巴洛斯"));
	}
}
