package org.apache.lucene.analysis.cn.smart;

import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

/**
 * Factory for {@link HMMChineseTokenizer}
 * <p>
 * Note: this class will currently emit tokens for punctuation. So you should either add
 * a WordDelimiterFilter after to remove these (with concatenate off), or use the
 * SmartChinese stoplist with a StopFilterFactory via:
 * <code>words="org/apache/lucene/analysis/cn/smart/stopwords.txt"</code>
 * @lucene.experimental
 */
public final class HMMChineseTokenizerFactory extends TokenizerFactory {

	/** Creates a new HMMChineseTokenizerFactory */
	public HMMChineseTokenizerFactory(Map<String, String> args) {
		super(args);
		if (!args.isEmpty()) {
			throw new IllegalArgumentException("Unknown parameters: " + args);
		}
	}

	@Override
	public Tokenizer create(AttributeFactory factory) {
		return new HMMChineseTokenizer(factory);
	}

}
