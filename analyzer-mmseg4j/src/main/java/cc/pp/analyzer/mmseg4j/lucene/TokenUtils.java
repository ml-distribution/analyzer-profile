package cc.pp.analyzer.mmseg4j.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class TokenUtils {

	/**
	 * @param input
	 * @param reusableToken is null well new one auto.
	 * @return null - if not next token or input is null.
	 * @throws IOException
	 */
	public static PackedTokenAttributeImpl nextToken(TokenStream input, PackedTokenAttributeImpl reusableToken)
			throws IOException {
		if (input == null) {
			return null;
		}
		if (!input.incrementToken()) {
			return null;
		}

		CharTermAttribute termAtt = input.getAttribute(CharTermAttribute.class);
		OffsetAttribute offsetAtt = input.getAttribute(OffsetAttribute.class);
		TypeAttribute typeAtt = input.getAttribute(TypeAttribute.class);

		if (reusableToken == null) {
			reusableToken = new PackedTokenAttributeImpl();
		}

		reusableToken.clear();
		if (termAtt != null) {
			reusableToken.copyBuffer(termAtt.buffer(), 0, termAtt.length());
		}
		if (offsetAtt != null) {
			reusableToken.setOffset(offsetAtt.startOffset(), offsetAtt.endOffset());
		}

		if (typeAtt != null) {
			reusableToken.setType(typeAtt.type());
		}

		return reusableToken;
	}

	public static PackedTokenAttributeImpl subToken(PackedTokenAttributeImpl oriToken, int termBufferOffset,
			int termBufferLength) {
		PackedTokenAttributeImpl token = new PackedTokenAttributeImpl();
		token.copyBuffer(oriToken.buffer(), termBufferOffset, termBufferLength);
		token.setOffset(oriToken.startOffset() + termBufferOffset, oriToken.startOffset() + termBufferOffset
				+ termBufferLength);
		token.setType(oriToken.type());

		return token;
	}

}
