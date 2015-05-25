package org.apache.lucene.analysis.cn.smart.hhmm;

import org.apache.lucene.analysis.cn.smart.Utility;
import org.apache.lucene.analysis.cn.smart.WordType;

/**
 * <p>
 * Filters a {@link SegToken} by converting full-width latin to half-width, then lowercasing latin.
 * Additionally, all punctuation is converted into {@link Utility#COMMON_DELIMITER}
 * </p>
 * @lucene.experimental
 */
public class SegTokenFilter {

	/**
	 * Filter an input {@link SegToken}
	 * <p>
	 * Full-width latin will be converted to half-width, then all latin will be lowercased.
	 * All punctuation is converted into {@link Utility#COMMON_DELIMITER}
	 * </p>
	 * 
	 * @param token input {@link SegToken}
	 * @return normalized {@link SegToken}
	 */
	public SegToken filter(SegToken token) {
		switch (token.wordType) {
		case WordType.FULLWIDTH_NUMBER:
		case WordType.FULLWIDTH_STRING: /* first convert full-width -> half-width */
			for (int i = 0; i < token.charArray.length; i++) {
				if (token.charArray[i] >= 0xFF10)
					token.charArray[i] -= 0xFEE0;

				if (token.charArray[i] >= 0x0041 && token.charArray[i] <= 0x005A) /* lowercase latin */
					token.charArray[i] += 0x0020;
			}
			break;
		case WordType.STRING:
			for (int i = 0; i < token.charArray.length; i++) {
				if (token.charArray[i] >= 0x0041 && token.charArray[i] <= 0x005A) /* lowercase latin */
					token.charArray[i] += 0x0020;
			}
			break;
		case WordType.DELIMITER: /* convert all punctuation to Utility.COMMON_DELIMITER */
			token.charArray = Utility.COMMON_DELIMITER;
			break;
		default:
			break;
		}
		return token;
	}
}
