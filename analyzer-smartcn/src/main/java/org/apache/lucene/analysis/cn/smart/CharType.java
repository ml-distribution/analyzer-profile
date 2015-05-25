package org.apache.lucene.analysis.cn.smart;

/**
 * Internal SmartChineseAnalyzer character type constants.
 * @lucene.experimental
 */
public class CharType {

	/**
	 * Punctuation Characters
	 */
	public final static int DELIMITER = 0;

	/**
	 * Letters
	 */
	public final static int LETTER = 1;

	/**
	 * Numeric Digits
	 */
	public final static int DIGIT = 2;

	/**
	 * Han Ideographs
	 */
	public final static int HANZI = 3;

	/**
	 * Characters that act as a space
	 */
	public final static int SPACE_LIKE = 4;

	/**
	 * Full-Width letters
	 */
	public final static int FULLWIDTH_LETTER = 5;

	/**
	 * Full-Width alphanumeric characters
	 */
	public final static int FULLWIDTH_DIGIT = 6;

	/**
	 * Other (not fitting any of the other categories)
	 */
	public final static int OTHER = 7;

}
