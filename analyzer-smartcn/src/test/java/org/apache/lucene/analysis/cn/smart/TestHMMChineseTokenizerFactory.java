package org.apache.lucene.analysis.cn.smart;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;

/**
 * Tests for {@link HMMChineseTokenizerFactory}
 */
public class TestHMMChineseTokenizerFactory extends BaseTokenStreamTestCase {

	/** Test showing the behavior */
	public void testSimple() throws Exception {
		Reader reader = new StringReader("我购买了道具和服装。");
		TokenizerFactory factory = new HMMChineseTokenizerFactory(new HashMap<String, String>());
		Tokenizer tokenizer = factory.create(newAttributeFactory());
		tokenizer.setReader(reader);
		// TODO: fix smart chinese to not emit punctuation tokens
		// at the moment: you have to clean up with WDF, or use the stoplist, etc
		assertTokenStreamContents(tokenizer, new String[] { "我", "购买", "了", "道具", "和", "服装", "," });
	}

	/** Test that bogus arguments result in exception */
	public void testBogusArguments() throws Exception {
		try {
			new HMMChineseTokenizerFactory(new HashMap<String, String>() {

				private static final long serialVersionUID = -1669504921900304740L;

				{
					put("bogusArg", "bogusValue");
				}
			});
			fail();
		} catch (IllegalArgumentException expected) {
			assertTrue(expected.getMessage().contains("Unknown parameters"));
		}
	}

}
