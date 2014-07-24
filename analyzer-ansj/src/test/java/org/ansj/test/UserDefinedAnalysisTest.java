package org.ansj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;

public class UserDefinedAnalysisTest {

	@Test
	public void test() {
		String newWord = "爸爸去哪儿";
		String nature = "aaaaa";
		String str = "上海电力2012年财务报表如下怎爸爸去哪儿么办";

		//增加新词
		UserDefineLibrary.insertWord(newWord, nature, 1000);

		List<Term> parse = ToAnalysis.parse(str);
		HashMap<String, Term> hs = new HashMap<String, Term>();
		for (Term term : parse) {
			hs.put(term.getName(), term);
		}

		assertTrue(hs.containsKey(newWord));

		assertEquals(hs.get(newWord).getNatrue().natureStr, nature);

		//删除词
		UserDefineLibrary.removeWord(newWord);
		parse = ToAnalysis.parse(str);
		hs = new HashMap<String, Term>();
		for (Term term : parse) {
			hs.put(term.getName(), term);
		}

		assertTrue(!hs.containsKey(newWord));
	}

}
