package org.ictclas4j.bean;

import java.util.ArrayList;

import org.ictclas4j.utility.Utility;
import org.ictclas4j.utility.Utility.TAG_TYPE;


public class Span {

	public ContextStat context;

	TAG_TYPE tagType;

	private final int[][] m_nTags;

	int[][] m_nBestPrev;

	int m_nStartPos;

	int[] m_nBestTag;

	int m_nCurLength;

	String[] m_sWords;

	double[][] m_dFrequency;

	public int[][] m_nUnknownWords;

	public int m_nUnknownIndex;

	public int[] m_nWordPosition;

	public double[] m_dWordsPossibility;

	public Span() {

		m_nTags = new int[Utility.MAX_WORDS_PER_SENTENCE][Utility.MAX_POS_PER_WORD];
		if (tagType != Utility.TAG_TYPE.TT_NORMAL)
			m_nTags[0][0] = 100;// Begin tag
		else
			m_nTags[0][0] = 0;// Begin tag
		m_nTags[0][1] = -1;
		m_nBestPrev = new int[Utility.MAX_WORDS_PER_SENTENCE][Utility.MAX_POS_PER_WORD];
		m_nBestTag = new int[Utility.MAX_WORDS_PER_SENTENCE];
		m_sWords = new String[Utility.MAX_WORDS_PER_SENTENCE];
		m_nUnknownWords = new int[Utility.MAX_UNKNOWN_PER_SENTENCE][2];
		m_nWordPosition = new int[Utility.MAX_WORDS_PER_SENTENCE];
		m_dWordsPossibility = new double[Utility.MAX_UNKNOWN_PER_SENTENCE];
		m_dFrequency = new double[Utility.MAX_WORDS_PER_SENTENCE][Utility.MAX_POS_PER_WORD];

		tagType = Utility.TAG_TYPE.TT_NORMAL;
	}

	public boolean loadContext(String fileName) {
		if (fileName != null) {
			context = new ContextStat();
			return context.load(fileName);
		}
		return false;
	}

	public void setType(TAG_TYPE type) {
		tagType = type;
	}

	public boolean posTagging(ArrayList<WordResult> wrList, Dictionary coreDict, Dictionary unknownDict) {
		int i = 0;
		int j, nStartPos;
		reset(false);
		while (i > -1 && i < wrList.size()) {
			nStartPos = i;// Start Position
			i = getFrom(wrList, nStartPos, coreDict, unknownDict);
			getBestPOS();
			switch (tagType) {
			case TT_NORMAL:// normal POS tagging
				j = 1;
				// Store the best POS tagging
				while (m_nBestTag[j] != -1 && j < m_nCurLength) {
					WordResult wr = wrList.get(j + nStartPos - 1);
					wr.setHandle(m_nBestTag[j]);
					// Let 。be 0
					// Exist and update its frequncy as a POS value
					if (wr.getValue() > 0 && coreDict.isExist(wr.getWord(), -1))
						wr.setValue(coreDict.getFreq(wr.getWord(), m_nBestTag[j]));
					j += 1;
				}
				break;
			case TT_PERSON:// Person recognition
				PersonRecognize(unknownDict);
				break;
			case TT_PLACE:// Place name recognition
			case TT_TRANS_PERSON:// Transliteration Person
				PlaceRecognize(coreDict, unknownDict);
				break;
			default:
				break;
			}
			reset();
		}
		return true;
	}

	public boolean reset(boolean isContinue) {
		if (!isContinue) {
			if (tagType != Utility.TAG_TYPE.TT_NORMAL)
				m_nTags[0][0] = 100;// Begin tag
			else
				m_nTags[0][0] = 0;// Begin tag
			m_nUnknownIndex = 0;
			m_dFrequency[0][0] = 0;
			m_nStartPos = 0;
		} else {
			// Get the last POS in the last sentence
			m_nTags[0][0] = m_nTags[m_nCurLength - 1][0];
			m_dFrequency[0][0] = m_dFrequency[m_nCurLength - 1][0];
		}

		// Get the last POS in the last sentence,set the -1 as end flag
		m_nTags[0][1] = -1;
		m_nCurLength = 1;
		m_nWordPosition[1] = m_nStartPos;
		m_sWords[0] = null;
		return true;
	}

	public boolean reset() {
		return reset(true);
	}

	private boolean disamb() {
		int i, j, k, nMinCandidate;
		double dMinFee = 0;
		double dTmp = 0;

		for (i = 1; i < m_nCurLength; i++)// For every word
		{
			for (j = 0; m_nTags[i][j] >= 0; j++)// For every word
			{
				nMinCandidate = Utility.MAX_POS_PER_WORD + 1;
				for (k = 0; m_nTags[i - 1][k] >= 0; k++) {
					// ConvertPOS(m_nTags[i-1][k],&nKey,&nPrevPOS);
					// ConvertPOS(m_nTags[i][j],&nKey,&nCurPOS);
					// dTmp=m_context.GetContextPossibility(nKey,nPrevPOS,nCurPOS);
					dTmp = -Math.log(context.getPossibility(0, m_nTags[i - 1][k], m_nTags[i][j]));
					dTmp += m_dFrequency[i - 1][k];// Add the fees
					if (nMinCandidate > 10 || dTmp < dMinFee)// Get the
					// minimum fee
					{
						nMinCandidate = k;
						dMinFee = dTmp;
					}
				}
				m_nBestPrev[i][j] = nMinCandidate;// The best previous for j
				m_dFrequency[i][j] = m_dFrequency[i][j] + dMinFee;
			}
		}

		return true;
	}

	public boolean getBestPOS() {
		disamb();
		for (int i = m_nCurLength - 1, j = 0; i > 0; i--)// ,j>=0
		{
			if (m_sWords[i] != null) {// Not virtual ending
				m_nBestTag[i] = m_nTags[i][j];// Record the best POS and its
				// possibility
			}
			j = m_nBestPrev[i][j];
		}
		int nEnd = m_nCurLength;// Set the end of POS tagging
		if (m_sWords[m_nCurLength - 1] == null)
			nEnd = m_nCurLength - 1;
		m_nBestTag[nEnd] = -1;
		return true;
	}

	/**
	 * 取得没有在dictUnknown中出现过的词的下一个位置
	 * @param wrList
	 * @param index
	 * @param coreDict
	 * @param unknownDict
	 * @return
	 */
	public int getFrom(ArrayList<WordResult> wrList, int index, Dictionary coreDict, Dictionary unknownDict) {

		int[] aPOS = new int[Utility.MAX_POS_PER_WORD];
		int[] aFreq = new int[Utility.MAX_POS_PER_WORD];
		int nFreq = 0, j, nRetPos = 0, nWordsIndex = 0;
		boolean bSplit = false;// Need to split in Transliteration recognition
		int i = 1, nPOSCount;
		String sCurWord;// Current word
		nWordsIndex = index ;

		for (; i < Utility.MAX_WORDS_PER_SENTENCE && nWordsIndex < wrList.size(); i++) {
			WordResult wr = wrList.get(nWordsIndex);
			String word = wr.getWord();
			if (tagType == Utility.TAG_TYPE.TT_NORMAL || !unknownDict.isExist(word, 44)) {
				// current word
				m_sWords[i] = word;// store
				m_nWordPosition[i + 1] = m_nWordPosition[i] + m_sWords[i].getBytes().length;
			}

			// Record the position of current word
			m_nStartPos = m_nWordPosition[i + 1];
			// Move the Start POS to the ending
			if (tagType != Utility.TAG_TYPE.TT_NORMAL) {
				// Get the POSs from the unknown recognition dictionary
				sCurWord = m_sWords[i];
				if (tagType == Utility.TAG_TYPE.TT_TRANS_PERSON && i > 0
						&& Utility.charType(m_sWords[i - 1]) == Utility.CT_CHINESE) {
					if (".".equals(m_sWords[i]))
						sCurWord = "．";
					else if ("-".equals(m_sWords))
						sCurWord = "－";
				}
				ArrayList<WordItem> wis = unknownDict.getHandle(sCurWord);
				nPOSCount = wis.size() + 1;
				for (j = 0; j < wis.size(); j++) {
					aPOS[j] = wis.get(j).getHandle();
					aFreq[j] = wis.get(j).getFreq();
					m_nTags[i][j] = aPOS[j];
					m_dFrequency[i][j] = -Math.log((1 + aFreq[j]));
					m_dFrequency[i][j] += Math.log((context.getFreq(0, aPOS[j]) + nPOSCount));
				}

				if ("始##始".equals(m_sWords[i])) {
					m_nTags[i][j] = 100;
					m_dFrequency[i][j] = 0;
					j++;
				} else if ("末##末".equals(m_sWords[i])) {
					m_nTags[i][j] = 101;
					m_dFrequency[i][j] = 0;
					j++;
				} else {
					wis = coreDict.getHandle(m_sWords[i]);
					nFreq = 0;
					for (int k = 0; k < wis.size(); k++) {
						aFreq[k] = wis.get(k).getFreq();
						nFreq += aFreq[k];
					}
					if (wis.size() > 0) {
						m_nTags[i][j] = 0;
						m_dFrequency[i][j] = -Math.log(1 + nFreq);
						m_dFrequency[i][j] += Math.log(context.getFreq(0, 0) + nPOSCount);
						j++;
					}
				}
			} else// For normal POS tagging
			{
				j = 0;
				// Get the POSs from the unknown recognition dictionary
				if (wr.getHandle() > 0) {// The word has is only one POS
					// value
					// We have record its POS and nFrequncy in the items.
					m_nTags[i][j] = wr.getHandle();
					m_dFrequency[i][j] = -Math.log(wr.getValue())
							+ Math.log(context.getFreq(0, m_nTags[i][j]) + 1);

					// Not permit the value less than 0
					if (m_dFrequency[i][j] < 0)
						m_dFrequency[i][j] = 0;
					j++;
				}

				// The word has multiple POSs, we should retrieve the
				// information from Core Dictionary
				else {
					if (wr.getHandle() < 0) {// The word has is only one POS
						m_nTags[i][j] = -wr.getHandle();
						m_dFrequency[i][j++] = wr.getValue();

					}
					ArrayList<WordItem> wis = coreDict.getHandle(m_sWords[i]);
					nPOSCount = wis.size();
					for (; j < wis.size(); j++) {
						// in the unknown dictionary
						aPOS[j] = wis.get(j).getHandle();
						aFreq[j] = wis.get(j).getFreq();
						m_nTags[i][j] = aPOS[j];
						m_dFrequency[i][j] = -Math.log(1 + aFreq[j])
								+ Math.log(context.getFreq(0, m_nTags[i][j]) + nPOSCount);
					}
				}
			}

			// We donot know the POS, so we have to guess them according lexical
			// knowledge
			if (j == 0) {
				j = guessPOS(i);// Guess the POS of current word
			}
			m_nTags[i][j] = -1;// Set the ending POS

			// No ambuguity, so we can break from the loop
			if (j == 1 && m_nTags[i][j] != Utility.CT_SENTENCE_BEGIN) {
				i++;
				m_sWords[i] = null;
				break;
			}
			if (!bSplit)
				nWordsIndex++;
		}
		if (nWordsIndex == wrList.size())
			nRetPos = -1;// Reaching ending

		if (m_nTags[i - 1][1] != -1)// ||m_sWords[i][0]==0
		{// Set end for words like "张/华/平"
			if (tagType != Utility.TAG_TYPE.TT_NORMAL)
				m_nTags[i][0] = 101;
			else
				m_nTags[i][0] = 1;

			m_dFrequency[i][0] = 0;
			m_sWords[i] = null;// Set virtual ending
			m_nTags[i++][1] = -1;
		}
		m_nCurLength = i;// The current word count
		if (nRetPos != -1)
			return nWordsIndex + 1;// Next start position
		return -1;// Reaching ending

	}

	/**
	 * <pre>
	 *
	 *          BBCD 343 0.003606
	 *          BBC 2 0.000021
	 *          BBE 125 0.001314
	 *          BBZ 30 0.000315
	 *          BCD 62460 0.656624
	 *          BEE 0 0.000000
	 *          BE 13899 0.146116
	 *          BG 869 0.009136
	 *          BXD 4 0.000042
	 *          BZ 3707 0.038971
	 *          CD 8596 0.090367
	 *          EE 26 0.000273
	 *          FB 871 0.009157
	 *          Y 3265 0.034324
	 *          XD 926 0.009735
	 *
	 *          The person recognition patterns set
	 *          BBCD:姓+姓+名1+名2;
	 *          BBE: 姓+姓+单名;
	 *          BBZ: 姓+姓+双名成词;
	 *          BCD: 姓+名1+名2;
	 *          BE: 姓+单名;
	 *          BEE: 姓+单名+单名;韩磊磊
	 *          BG: 姓+后缀
	 *          BXD: 姓+姓双名首字成词+双名末字
	 *          BZ: 姓+双名成词;
	 *          B: 姓
	 *          CD: 名1+名2;
	 *          EE: 单名+单名;
	 *          FB: 前缀+姓
	 *          XD: 姓双名首字成词+双名末字
	 *          Y: 姓单名成词
	 * </pre>
	 */
	public boolean PersonRecognize(Dictionary personDict) {
		String sPOS = "z";
		String sPersonName;
		// 0 1 2 3 4 5
		final String[] patterns = { "BBCD", "BBC", "BBE", "BBZ", "BCD", "BEE", "BE", "BG", "BXD", "BZ", "CDCD", "CD",
				"EE", "FB", "Y", "XD", "" };
		// BBCD BBC BBE BBZ BCD BEE BE BG
		final double[] factor = { 0.003606, 0.000021, 0.001314, 0.000315, 0.656624, 0.000021, 0.146116, 0.009136,
		// BXD BZ CDCD CD EE FB Y XD
				0.000042, 0.038971, 0, 0.090367, 0.000273, 0.009157, 0.034324, 0.009735, 0 };
		// About parameter:

		final int patternLen[] = { 4, 3, 3, 3, 3, 3, 2, 2, 3, 2, 4, 2, 2, 2, 1, 2, 0 };
		int i = 0;
		for (i = 1; m_nBestTag[i] > -1; i++)
			// Convert to string from POS
			sPOS += (char) (m_nBestTag[i] + 'A');
		int j = 1, k, nPos;// Find the proper pattern from the first POS
		@SuppressWarnings("unused")
		int nLittleFreqCount;// Counter for the person name role with little
		// frequecy
		boolean bMatched = false;

		while (j < i) {
			bMatched = false;
			for (k = 0; !bMatched && patternLen[k] > 0; k++) {
				if (sPOS.substring(j).indexOf(patterns[k]) == 0 && !"·".equals(m_sWords[j - 1])
						&& !"·".equals(m_sWords[j + patternLen[k]])) {// Find

					String temp = sPOS.substring(j + 2);
					if (temp.length() > 1)
						temp = temp.substring(0, 1);

					// Rule 1 for exclusion:前缀+姓+名1(名2): 规则(前缀+姓)失效；
					if ("FB".equals(patterns[k]) && ("E".equals(temp) || "C".equals(temp) || "G".equals(temp))) {
						continue;
					}

					nPos = j;// Record the person position in the tag
					// sequence
					sPersonName = "";
					nLittleFreqCount = 0;// Record the number of role with
					// little frequency
					while (nPos < j + patternLen[k]) {// Get the possible
						// person name

						if (m_nBestTag[nPos] < 4
								&& personDict.getFreq(m_sWords[nPos], m_nBestTag[nPos]) < Utility.LITTLE_FREQUENCY)
							nLittleFreqCount++;// The counter increase
						sPersonName += m_sWords[nPos];
						nPos += 1;
					}
					if ("CDCD".equals(patterns[k])) {
						if (GetForeignCharCount(sPersonName) > 0)
							j += patternLen[k] - 1;
						continue;
					}
					m_nUnknownWords[m_nUnknownIndex][0] = m_nWordPosition[j];
					m_nUnknownWords[m_nUnknownIndex][1] = m_nWordPosition[j + patternLen[k]];
					m_dWordsPossibility[m_nUnknownIndex] = -Math.log(factor[k])
							+ ComputePossibility(j, patternLen[k], personDict);
					// Mutiply the factor
					m_nUnknownIndex += 1;
					j += patternLen[k];
					bMatched = true;
				}
			}
			if (!bMatched)// Not matched, add j by 1
				j += 1;
		}
		return true;
	}

	private int guessPOS(int index) {
		int j = 0, i = index, charType;
		int nLen;
		switch (tagType) {
		case TT_NORMAL:
			break;
		case TT_PERSON:
			j = 0;
			if (m_sWords[index].indexOf("××") != -1) {
				m_nTags[i][j] = 6;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 6) + 1);
			} else {
				m_nTags[i][j] = 0;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 0) + 1);
				nLen = m_sWords[index].getBytes().length;
				if (nLen >= 4) {
					m_nTags[i][j] = 0;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 0) + 1);
					m_nTags[i][j] = 11;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 11) * 8);
					m_nTags[i][j] = 12;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 12) * 8);
					m_nTags[i][j] = 13;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 13) * 8);
				} else if (nLen == 2) {
					m_nTags[i][j] = 0;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 0) + 1);
					charType = Utility.charType(m_sWords[index]);
					if (charType == Utility.CT_OTHER || charType == Utility.CT_CHINESE) {
						m_nTags[i][j] = 1;
						m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 1) + 1);
						m_nTags[i][j] = 2;
						m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 2) + 1);
						m_nTags[i][j] = 3;
						m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 3) + 1);
						m_nTags[i][j] = 4;
						m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 4) + 1);
					}
					m_nTags[i][j] = 11;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 11) * 8);
					m_nTags[i][j] = 12;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 12) * 8);
					m_nTags[i][j] = 13;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 13) * 8);
				}
			}
			break;
		case TT_PLACE:
			j = 0;
			m_nTags[i][j] = 0;
			m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 0) + 1);
			nLen = m_sWords[index].length();
			if (nLen >= 4) {
				m_nTags[i][j] = 11;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 11) * 8);
				m_nTags[i][j] = 12;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 12) * 8);
				m_nTags[i][j] = 13;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 13) * 8);
			} else if (nLen == 2) {
				m_nTags[i][j] = 0;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 0) + 1);
				charType = Utility.charType(m_sWords[index]);
				if (charType == Utility.CT_OTHER || charType == Utility.CT_CHINESE) {
					m_nTags[i][j] = 1;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 1) + 1);
					m_nTags[i][j] = 2;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 2) + 1);
					m_nTags[i][j] = 3;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 3) + 1);
					m_nTags[i][j] = 4;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 4) + 1);
				}
				m_nTags[i][j] = 11;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 11) * 8);
				m_nTags[i][j] = 12;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 12) * 8);
				m_nTags[i][j] = 13;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 13) * 8);
			}
			break;
		case TT_TRANS_PERSON:
			j = 0;
			nLen = m_sWords[index].length();

			m_nTags[i][j] = 0;
			m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 0) + 1);

			if (!Utility.isAllChinese(m_sWords[index])) {
				if (Utility.isAllLetter(m_sWords[index])) {
					m_nTags[i][j] = 1;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 1) + 1);
					m_nTags[i][j] = 11;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 11) + 1);
					m_nTags[i][j] = 2;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 2) * 2 + 1);
					m_nTags[i][j] = 3;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 3) * 2 + 1);
					m_nTags[i][j] = 12;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 12) * 2 + 1);
					m_nTags[i][j] = 13;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 13) * 2 + 1);
				}
				m_nTags[i][j] = 41;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 41) * 8);
				m_nTags[i][j] = 42;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 42) * 8);
				m_nTags[i][j] = 43;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 43) * 8);
			} else if (nLen >= 4) {
				m_nTags[i][j] = 41;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 41) * 8);
				m_nTags[i][j] = 42;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 42) * 8);
				m_nTags[i][j] = 43;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 43) * 8);
			} else if (nLen == 2) {
				charType = Utility.charType(m_sWords[index]);
				if (charType == Utility.CT_OTHER || charType == Utility.CT_CHINESE) {
					m_nTags[i][j] = 1;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 1) * 2 + 1);
					m_nTags[i][j] = 2;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 2) * 2 + 1);
					m_nTags[i][j] = 3;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 3) * 2 + 1);
					m_nTags[i][j] = 30;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 30) * 8 + 1);
					m_nTags[i][j] = 11;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 11) * 4 + 1);
					m_nTags[i][j] = 12;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 12) * 4 + 1);
					m_nTags[i][j] = 13;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 13) * 4 + 1);
					m_nTags[i][j] = 21;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 21) * 2 + 1);
					m_nTags[i][j] = 22;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 22) * 2 + 1);
					m_nTags[i][j] = 23;
					m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 23) * 2 + 1);
				}
				m_nTags[i][j] = 41;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 41) * 8);
				m_nTags[i][j] = 42;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 42) * 8);
				m_nTags[i][j] = 43;
				m_dFrequency[i][j++] = (double) 1 / (double) (context.getFreq(0, 43) * 8);
			}
			break;
		default:
			break;
		}

		return j;
	}

	int GetForeignCharCount(String personName) {
		return 0;
	}

	public boolean PlaceRecognize(Dictionary coreDict, Dictionary placeDict) {
		int nStart = 1, nEnd = 1, i = 1, nTemp;
		double dPanelty = 1.0;// Panelty value
		while (m_nBestTag[i] > -1) {
			if (m_nBestTag[i] == 1)// 1 Trigger the recognition procession
			{
				nStart = i;
				nEnd = nStart + 1;
				while (m_nBestTag[nEnd] == 1)//
				{
					if (nEnd > nStart + 1)
						dPanelty += 1.0;
					nEnd++;
				}
				while (m_nBestTag[nEnd] == 2)
					// 2,12,22
					nEnd++;
				nTemp = nEnd;
				while (m_nBestTag[nEnd] == 3) {
					if (nEnd > nTemp)
						dPanelty += 1.0;
					nEnd++;
				}
			} else if (m_nBestTag[i] == 2)// 1,11,21 Trigger the recognition
			{
				dPanelty += 1.0;
				nStart = i;
				nEnd = nStart + 1;
				while (m_nBestTag[nEnd] == 2)
					// 2
					nEnd++;
				nTemp = nEnd;
				while (m_nBestTag[nEnd] == 3)// 2
				{
					if (nEnd > nTemp)
						dPanelty += 1.0;
					nEnd++;
				}
			}
			if (nEnd > nStart) {
				m_nUnknownWords[m_nUnknownIndex][0] = m_nWordPosition[nStart];
				m_nUnknownWords[m_nUnknownIndex][1] = m_nWordPosition[nEnd];
				m_dWordsPossibility[m_nUnknownIndex++] = ComputePossibility(nStart, nEnd - nStart + 1, placeDict)
						+ Math.log(dPanelty);
				nStart = nEnd;
			}
			if (i < nEnd)
				i = nEnd;
			else
				i = i + 1;
		}
		return true;
	}

	private double ComputePossibility(int startPos, int length, Dictionary dict) {
		double retValue = 0, posPoss;
		int nFreq;
		for (int i = startPos; i < startPos + length; i++) {
			nFreq = dict.getFreq(m_sWords[i], m_nBestTag[i]);
			// nFreq is word being the POS
			posPoss = Math.log(context.getFreq(0, m_nBestTag[i]) + 1) - Math.log(nFreq + 1);
			retValue += posPoss;
		}
		return retValue;
	}
}