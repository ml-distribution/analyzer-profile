package cc.pp.analyzer.web.core;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.analyzer.fudan.core.WordExtractFudan;
import cc.pp.analyzer.ik.lucene.IKAnalyzer;
import cc.pp.analyzer.mmseg4j.wordseg.MMSeg4jWordSeg;
import cc.pp.analyzer.web.domain.WordsList;

/**
 * 各类分词器组合
 * @author wanggang
 *
 */
public class AnalyzerCore {

	private static Logger logger = LoggerFactory.getLogger(AnalyzerCore.class);

	// 复旦大学关键词提取器
	private final WordExtractFudan fudanKeyword;

	// IK 分词器
	private final IKAnalyzer ikAnalyzer;

	// MMSeg4j
	private final MMSeg4jWordSeg mmseg4jAnalyzer;

	public AnalyzerCore() {
		fudanKeyword = new WordExtractFudan();
		ikAnalyzer = new IKAnalyzer(Version.LUCENE_48);
		mmseg4jAnalyzer = new MMSeg4jWordSeg();
	}

	public static void main(String[] args) {
		AnalyzerCore analyzerCore = new AnalyzerCore();
		analyzerCore.close();
	}

	/**
	 * MMSeg4j分词器
	 * @param str  待分词字符串
	 * @param type 分词器类别，0--simple, 1--maxword, 2--complex
	 * @return
	 */
	public WordsList getMMSeg4jSegWords(String str, int type) {
		try {
			WordsList result = new WordsList();
			String words = mmseg4jAnalyzer.segWords(type, new StringReader(str), ",");
			result.setCount(words.split(",").length);
			for (String word : words.split(",")) {
				result.getWords().add(word);
			}
			return result;
		} catch (IOException e) {
			logger.error("IOException: " + e);
			return null;
		}
	}

	/**
	 * IK 分词器
	 * @param str  待分词字符串
	 * @param type  是否使用智能分词: 0--否，1--是
	 * @return
	 * @throws IOException
	 */
	public WordsList getIKSegWords(String str, int type) {
		try {
			WordsList result = new WordsList();
			List<String> words = new ArrayList<>();
			ikAnalyzer.setUseSmart(type == 0 ? Boolean.FALSE : Boolean.TRUE);
			TokenStream tokenStream = ikAnalyzer.tokenStream("content", new StringReader(str));
			CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
			try {
				tokenStream.reset();
				while (tokenStream.incrementToken()) {
					words.add(charTermAttribute.toString());
				}
				tokenStream.end();
				result.setCount(words.size());
				result.setWords(words);
				return result;
			} finally {
				tokenStream.close();
			}
		} catch (IOException e) {
			logger.error("IOException: " + e);
			return null;
		}
	}

	/**
	 * 复旦大学关键词提取
	 * @param str   待分词字符串
	 * @param wordCharacter   词性
	 * @param keywordNum   分词数
	 * @return
	 */
	public WordsList getFudanKeywords(String str, String wordCharacter, int keywordNum) {
		WordsList result = new WordsList();
		List<String> keywords = fudanKeyword.extractList(str, wordCharacter, keywordNum);
		result.setCount(keywords.size());
		result.setWords(keywords);
		return result;
	}

	public WordsList getFudanKeywords(String str, int keywordNum) {
		return getFudanKeywords(str, "", keywordNum);
	}

	public WordsList getFudanKeywords(String str) {
		return getFudanKeywords(str, "", 50);
	}

	public void close() {
		ikAnalyzer.close();
	}

}
