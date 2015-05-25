package org.apache.lucene.analysis.cn.smart;

import java.util.Map;

import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

/**
 * Factory for the SmartChineseAnalyzer {@link SentenceTokenizer}
 * @lucene.experimental
 * @deprecated Use {@link HMMChineseTokenizerFactory} instead
 */
@Deprecated
public class SmartChineseSentenceTokenizerFactory extends TokenizerFactory {

	/** Creates a new SmartChineseSentenceTokenizerFactory */
	public SmartChineseSentenceTokenizerFactory(Map<String, String> args) {
		super(args);
		if (!args.isEmpty()) {
			throw new IllegalArgumentException("Unknown parameters: " + args);
		}
	}

	@Override
	public SentenceTokenizer create(AttributeFactory factory) {
		return new SentenceTokenizer(factory);
	}

}
