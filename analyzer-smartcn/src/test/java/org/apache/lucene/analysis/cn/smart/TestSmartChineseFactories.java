package org.apache.lucene.analysis.cn.smart;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

/**
 * Tests for {@link SmartChineseSentenceTokenizerFactory} and
 * {@link SmartChineseWordTokenFilterFactory}
 */
@Deprecated
public class TestSmartChineseFactories extends BaseTokenStreamTestCase {

	/** Test showing the behavior with whitespace */
	public void testSimple() throws Exception {
		Reader reader = new StringReader("我购买了道具和服装。");
		TokenStream stream = whitespaceMockTokenizer(reader);
		SmartChineseWordTokenFilterFactory factory = new SmartChineseWordTokenFilterFactory(
				new HashMap<String, String>());
		stream = factory.create(stream);
		// TODO: fix smart chinese to not emit punctuation tokens
		// at the moment: you have to clean up with WDF, or use the stoplist, etc
		assertTokenStreamContents(stream, new String[] { "我", "购买", "了", "道具", "和", "服装", "," });
	}

	/** Test showing the behavior with whitespace */
	public void testTokenizer() throws Exception {
		Reader reader = new StringReader("我购买了道具和服装。我购买了道具和服装。");
		SmartChineseSentenceTokenizerFactory tokenizerFactory = new SmartChineseSentenceTokenizerFactory(
				new HashMap<String, String>());
		TokenStream stream = tokenizerFactory.create();
		((Tokenizer) stream).setReader(reader);
		SmartChineseWordTokenFilterFactory factory = new SmartChineseWordTokenFilterFactory(
				new HashMap<String, String>());
		stream = factory.create(stream);
		// TODO: fix smart chinese to not emit punctuation tokens
		// at the moment: you have to clean up with WDF, or use the stoplist, etc
		assertTokenStreamContents(stream, new String[] { "我", "购买", "了", "道具", "和", "服装", ",", "我", "购买", "了", "道具",
				"和", "服装", "," });
	}

	/** Test that bogus arguments result in exception */
	public void testBogusArguments() throws Exception {
		try {
			new SmartChineseSentenceTokenizerFactory(new HashMap<String, String>() {

				private static final long serialVersionUID = 3482695037544673614L;

				{
					put("bogusArg", "bogusValue");
				}
			});
			fail();
		} catch (IllegalArgumentException expected) {
			assertTrue(expected.getMessage().contains("Unknown parameters"));
		}

		try {
			new SmartChineseWordTokenFilterFactory(new HashMap<String, String>() {

				private static final long serialVersionUID = -5042150534753809348L;

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
