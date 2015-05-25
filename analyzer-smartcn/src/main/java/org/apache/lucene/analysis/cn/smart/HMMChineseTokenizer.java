package org.apache.lucene.analysis.cn.smart;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.Iterator;
import java.util.Locale;

import org.apache.lucene.analysis.cn.smart.hhmm.SegToken;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.SegmentingTokenizerBase;
import org.apache.lucene.util.AttributeFactory;

/**
 * Tokenizer for Chinese or mixed Chinese-English text.
 * <p>
 * The analyzer uses probabilistic knowledge to find the optimal word segmentation for Simplified Chinese text.
 * The text is first broken into sentences, then each sentence is segmented into words.
 */
public class HMMChineseTokenizer extends SegmentingTokenizerBase {

	/** used for breaking the text into sentences */
	private static final BreakIterator sentenceProto = BreakIterator.getSentenceInstance(Locale.ROOT);

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

	private final WordSegmenter wordSegmenter = new WordSegmenter();
	private Iterator<SegToken> tokens;

	/** Creates a new HMMChineseTokenizer */
	public HMMChineseTokenizer() {
		this(DEFAULT_TOKEN_ATTRIBUTE_FACTORY);
	}

	/** Creates a new HMMChineseTokenizer, supplying the AttributeFactory */
	public HMMChineseTokenizer(AttributeFactory factory) {
		super(factory, (BreakIterator) sentenceProto.clone());
	}

	@Override
	protected void setNextSentence(int sentenceStart, int sentenceEnd) {
		String sentence = new String(buffer, sentenceStart, sentenceEnd - sentenceStart);
		tokens = wordSegmenter.segmentSentence(sentence, offset + sentenceStart).iterator();
	}

	@Override
	protected boolean incrementWord() {
		if (tokens == null || !tokens.hasNext()) {
			return false;
		} else {
			SegToken token = tokens.next();
			clearAttributes();
			termAtt.copyBuffer(token.charArray, 0, token.charArray.length);
			offsetAtt.setOffset(correctOffset(token.startOffset), correctOffset(token.endOffset));
			typeAtt.setType("word");
			return true;
		}
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		tokens = null;
	}

}
