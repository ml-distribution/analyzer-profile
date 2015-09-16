package cc.pp.analyzer.mmseg4j.lucene;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import cc.pp.analyzer.mmseg4j.core.Word;

/**
 * 切分“字母和数”混在一起的过虑器。比如：mb991ch 切为 "mb 991 ch"
 *
 * @author wanggang
 *
 */
public class CutLetterDigitFilter extends TokenFilter {

	protected Queue<PackedTokenAttributeImpl> tokenQueue = new LinkedList<>();

	private CharTermAttribute termAtt;
	private OffsetAttribute offsetAtt;
	private TypeAttribute typeAtt;
	private PackedTokenAttributeImpl reusableToken;

	public CutLetterDigitFilter(TokenStream input) {
		super(input);

		reusableToken = new PackedTokenAttributeImpl();
		termAtt = addAttribute(CharTermAttribute.class);
		offsetAtt = addAttribute(OffsetAttribute.class);
		typeAtt = addAttribute(TypeAttribute.class);
	}

	private PackedTokenAttributeImpl nextToken(PackedTokenAttributeImpl reusableToken) throws IOException {
		assert reusableToken != null;

		// 先使用上次留下来的
		PackedTokenAttributeImpl nextToken = tokenQueue.poll();
		if (nextToken != null) {
			return nextToken;
		}

		nextToken = TokenUtils.nextToken(input, reusableToken);

		if (nextToken != null
				&& (Word.TYPE_LETTER_OR_DIGIT.equalsIgnoreCase(nextToken.type()) || Word.TYPE_DIGIT_OR_LETTER
						.equalsIgnoreCase(nextToken.type()))) {
			final char[] buffer = nextToken.buffer();
			final int length = nextToken.length();
			// 与上次的字符是否同类
			byte lastType = (byte) Character.getType(buffer[0]);
			int termBufferOffset = 0;
			int termBufferLength = 0;
			for (int i = 0; i < length; i++) {
				byte type = (byte) Character.getType(buffer[i]);
				if (type <= Character.MODIFIER_LETTER) {
					type = Character.LOWERCASE_LETTER;
				}
				// 与上一次的不同
				if (type != lastType) {
					addToken(nextToken, termBufferOffset, termBufferLength, lastType);

					termBufferOffset += termBufferLength;
					termBufferLength = 0;

					lastType = type;
				}

				termBufferLength++;
			}
			// 最后一次
			if (termBufferLength > 0) {
				addToken(nextToken, termBufferOffset, termBufferLength, lastType);
			}
			nextToken = tokenQueue.poll();
		}

		return nextToken;
	}

	private void addToken(PackedTokenAttributeImpl oriToken, int termBufferOffset, int termBufferLength, byte type) {
		PackedTokenAttributeImpl token = TokenUtils.subToken(oriToken, termBufferOffset, termBufferLength);

		if (type == Character.DECIMAL_DIGIT_NUMBER) {
			token.setType(Word.TYPE_DIGIT);
		} else {
			token.setType(Word.TYPE_LETTER);
		}

		tokenQueue.offer(token);
	}

	@Override
	public void close() throws IOException {
		super.close();
		tokenQueue.clear();
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		tokenQueue.clear();
	}

	@Override
	public final boolean incrementToken() throws IOException {
		clearAttributes();
		PackedTokenAttributeImpl token = nextToken(reusableToken);
		if (token != null) {
			termAtt.copyBuffer(token.buffer(), 0, token.length());
			offsetAtt.setOffset(token.startOffset(), token.endOffset());
			typeAtt.setType(token.type());
			return true;
		} else {
			end();
			return false;
		}
	}

	@Override
	public void end() {
		try {
			reset();
		} catch (IOException e) {
		}
	}

}
