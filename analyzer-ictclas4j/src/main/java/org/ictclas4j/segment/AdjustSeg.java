package org.ictclas4j.segment;

import java.util.ArrayList;

import org.ictclas4j.bean.SegNode;
import org.ictclas4j.utility.NumUtil;
import org.ictclas4j.utility.POSTag;
import org.ictclas4j.utility.Utility;


/**
 * 分词调整
 * 
 * @author sinboy
 * @since 2007.6.1
 */
public class AdjustSeg {
	/**
	 * 对初次分词结果进行调整，主要是对时间、日期、数字等进行合并或拆分
	 * 
	 * @return
	 */
	public static ArrayList<SegNode> firstAdjust(ArrayList<SegNode> sgs) {

		ArrayList<SegNode> wordResult = null;
		int index = 0;
		int j = 0;
		int pos = 0;

		if (sgs != null) {
			wordResult = new ArrayList<SegNode>();

			for (int i = 0; i < sgs.size(); i++, index++) {
				SegNode sn = sgs.get(i);
				String srcWord = null;
				String curWord = sn.getSrcWord();
				SegNode newsn = new SegNode();
				pos = sn.getPos();

				boolean isNum = false;
				if ((Utility.isAllNum(curWord) || Utility.isAllChineseNum(curWord))) {
					isNum = true;
					for (j = i + 1; j < sgs.size() - 1; j++) {
						String temp = sgs.get(j).getSrcWord();
						// 如果相邻的几点字符都是数字，则把它们进行合并
						if (Utility.isAllNum(temp) || Utility.isAllChineseNum(temp)) {
							isNum = true;
							index = j;
							curWord += temp;
						} else
							break;

					}
				}

				// 如果不是数字，但是可以和前面的数字构成日期，则重新设置前一个节点
				// 否则，直接把该节点添加到结果集中
				if (!isNum) {
					SegNode prevsn = null;
					if (wordResult.size() > 0)
						prevsn = wordResult.get(wordResult.size() - 1);
					if (Utility.isDelimiter(curWord)) {
						// 如果上一个字符也是分隔符，则进行合并
						if (prevsn != null && Utility.isDelimiter(prevsn.getWord())) {
							prevsn.setCol(sn.getCol());
							prevsn.appendWord(curWord);
							continue;
						} else
							// 'w'*256;Set the POS with 'w'
							pos = POSTag.PUNC;
					} else if (curWord.length() == 1 && "月日时分秒".indexOf(curWord) != -1 || "月份".equals(curWord)) {
						if (prevsn != null && prevsn.getPos() == -POSTag.NUM) {
							prevsn.setCol(sn.getCol());
							prevsn.setWord(Utility.UNKNOWN_TIME);
							prevsn.setSrcWord(prevsn.getSrcWord() + curWord);
							prevsn.setPos(-POSTag.TIME);
							continue;
						}
					} else if ("年".equals(curWord)) {
						if (prevsn != null && Utility.isYearTime(prevsn.getSrcWord())) {
							prevsn.setCol(sn.getCol());
							prevsn.setWord(Utility.UNKNOWN_TIME);
							prevsn.setSrcWord(prevsn.getSrcWord() + curWord);
							prevsn.setPos(-POSTag.TIME);
							continue;
						}
					}
				} else {

					// 如果当前字符串仅仅是有数字字符组成的而不是一个数字，则把他对应的原始节点信息也添加到结果集中
					if (NumUtil.isNumStrNotNum(curWord)) {
						for (int k = i; k <= index; k++)
							wordResult.add(sgs.get(k));
						continue;
					}
					// 是一个数字
					else {
						// 如果是类似这样的形式：
						// 3-4月，即当前元素是一个数字，前一个是分隔符，前前一个也是数字，则当前元素应该是数字
						boolean flag = false;
						int size = wordResult.size();
						if (wordResult.size() > 1) {
							SegNode prevPrevsn = wordResult.get(size - 2);
							SegNode prevsn = wordResult.get(size - 1);
							if (NumUtil.isNumDelimiter(prevPrevsn.getPos(), prevsn.getWord())) {
								pos = POSTag.NUM;
								flag = true;
							}
						}
						if (!flag) {
							if (curWord.indexOf("点") == curWord.length() - 1) {
								pos = -POSTag.TIME;
								srcWord = curWord;
								curWord = Utility.UNKNOWN_TIME;
							} else if (curWord.length() > 1) {
								String last = curWord.substring(curWord.length() - 1);
								// 如果当前词的最后一个字符不是如下几种情况，则说明他是一个数字。否则最后一个字符就是一个标点，并把它分离出来
								if ("∶·．／./".indexOf(last) == -1) {
									pos = -POSTag.NUM;
									srcWord = curWord;
									curWord = Utility.UNKNOWN_NUM;

								} else {
									if (".".equals(last) || "/".equals(last)) {
										pos = -POSTag.NUM;
										srcWord = curWord.substring(0, curWord.length() - 1);
										curWord = Utility.UNKNOWN_NUM;
										index--;
									} else if (curWord.length() > 2) {
										pos = -POSTag.NUM;
										srcWord = curWord.substring(0, curWord.length() - 2);
										curWord = Utility.UNKNOWN_NUM;
										index -= 2;
									}
								}
							}
						}
					}

				}

				int col = index > i ? sgs.get(index).getCol() : sn.getCol();
				newsn.setCol(col);
				newsn.setRow(sn.getRow());
				newsn.setWord(curWord);
				newsn.setPos(pos);
				newsn.setValue(sn.getValue());
				if (srcWord != null)
					newsn.setSrcWord(srcWord);
				wordResult.add(newsn);
				i = index;
			}
		}

		return wordResult;
	}

	/**
	 * 对分词结果做最终的调整，主要是人名的拆分或重叠词的合并
	 * 
	 * @param optSegPath
	 * @param personTagger
	 * @param placeTagger
	 * @return
	 */
	public static ArrayList<SegNode> finaAdjust(ArrayList<SegNode> optSegPath, PosTagger personTagger,
			PosTagger placeTagger) {
		ArrayList<SegNode> result = null;
		SegNode wr = null;

		if (optSegPath != null && optSegPath.size() > 0 && personTagger != null && placeTagger != null) {

			result = new ArrayList<SegNode>();
			for (int i = 0; i < optSegPath.size(); i++) {
				boolean isBeProcess = false;
				wr = optSegPath.get(i);
				// if (wr.getPos() == POSTag.NOUN_PERSON
				// && (pname = Utility.chineseNameSplit(wr.getSrcWord(),
				// personTagger)) != null
				// && !"叶利钦".equals(wr.getSrcWord())) {
				// if (pname.getFirstName() != null) {
				// SegNode wr2 = new SegNode();
				// wr2.setWord(pname.getFirstName());
				// wr2.setPos(POSTag.NOUN_PERSON);
				// result.add(wr2);
				// }
				//
				// if (pname.getMidName() != null) {
				// SegNode wr2 = new SegNode();
				// wr2.setWord(pname.getMidName());
				// wr2.setPos(POSTag.NOUN_PERSON);
				// result.add(wr2);
				// }
				//
				// if (pname.getLastName() != null) {
				// SegNode wr2 = new SegNode();
				// wr2.setWord(pname.getLastName());
				// wr2.setPos(POSTag.NOUN_PERSON);
				// result.add(wr2);
				// }
				//
				// isBeProcess = true;
				// }
				// Rule2 for overlap words ABB 一段段、一片片
				if (wr.getPos() == POSTag.NUM && i + 2 < optSegPath.size() && optSegPath.get(i + 1).getLen() == 2
						&& optSegPath.get(i + 1).getSrcWord().equals(optSegPath.get(i + 2).getSrcWord())) {
					SegNode wr2 = new SegNode();
					wr2.setWord(wr.getSrcWord() + optSegPath.get(i + 1).getSrcWord()
							+ optSegPath.get(i + 2).getSrcWord());
					wr2.setPos(POSTag.NUM);
					result.add(wr2);
					i += 2;
					isBeProcess = true;
				}
				// Rule3 for overlap words AA
				else if (wr.getLen() == 2 && i + 1 < optSegPath.size()
						&& wr.getSrcWord().equals(optSegPath.get(i + 1).getSrcWord())) {
					SegNode wr2 = new SegNode();
					wr2.setWord(wr.getSrcWord() + optSegPath.get(i + 1).getSrcWord());
					wr2.setPos(POSTag.ADJ);
					if (wr.getPos() == POSTag.VERB || optSegPath.get(i + 1).getPos() == POSTag.VERB)// 30208='v'8256
						wr2.setPos(POSTag.VERB);

					if (wr.getPos() == POSTag.NOUN || optSegPath.get(i + 1).getPos() == POSTag.NOUN)// 30208='v'8256
						wr2.setPos(POSTag.NOUN);

					i += 1;
					if (optSegPath.get(i + 1).getLen() == 2) {// AAB:洗/洗/脸、蒙蒙亮
						if ((wr2.getPos() == POSTag.VERB && optSegPath.get(i + 1).getPos() == POSTag.NOUN)
								|| (wr2.getPos() == POSTag.ADJ && optSegPath.get(i + 1).getPos() == POSTag.ADJ)) {
							wr2.setWord(wr2.getWord() + optSegPath.get(i + 1).getSrcWord());
							i += 1;
						}
					}
					isBeProcess = true;
					result.add(wr2);
				}
				// Rule 4: AAB 洗/洗澡
				else if (wr.getLen() == 2 && i + 1 < optSegPath.size()
						&& (wr.getPos() == POSTag.VERB || wr.getPos() == POSTag.ADJ)
						&& optSegPath.get(i + 1).getLen() == 4
						&& optSegPath.get(i + 1).getSrcWord().indexOf(wr.getSrcWord()) == 0) {
					SegNode wr2 = new SegNode();
					wr2.setWord(wr.getWord() + optSegPath.get(i + 1).getSrcWord());
					wr2.setPos(POSTag.ADJ); // 24832=='a'*256

					if (wr.getPos() == POSTag.VERB || optSegPath.get(i + 1).getPos() == POSTag.VERB)// 30208='v'8256
						wr2.setPos(POSTag.VERB);

					i += 1;
					isBeProcess = true;
					result.add(wr2);
				} else if (wr.getPos() / 256 == 'u' && wr.getPos() % 256 != 0)// uj,ud,uv,uz,ul,ug->u
					wr.setPos('u' * 256);
				// AABB,朴朴素素
				else if (wr.getLen() == 2 && i + 2 < optSegPath.size() && optSegPath.get(i + 1).getLen() == 4
						&& optSegPath.get(i + 1).getWord().indexOf(wr.getWord()) == 0
						&& optSegPath.get(i + 1).getWord().indexOf(optSegPath.get(i + 2).getWord()) == 0) {
					SegNode wr2 = new SegNode();
					wr2.setWord(wr.getWord() + optSegPath.get(i + 1).getWord() + optSegPath.get(i + 2).getWord());
					wr2.setPos(optSegPath.get(i + 1).getPos());
					i += 2;
					isBeProcess = true;
					result.add(wr2);
				}
				// 28275=='n'*256+'s' 地名+X
				else if (wr.getPos() == POSTag.NOUN_SPACE && i + 1 < optSegPath.size())// PostFix
				{
					SegNode next = optSegPath.get(i + 1);
					if (placeTagger.getUnknownDict().isExist(next.getSrcWord(), 4)) {
						SegNode wr2 = new SegNode();
						wr2.setWord(wr.getSrcWord() + next.getSrcWord());
						wr2.setPos(POSTag.NOUN_SPACE);
						i += 1;
						isBeProcess = true;
						result.add(wr2);
					} else if ("队".equals(next.getSrcWord())) {
						SegNode wr2 = new SegNode();
						wr2.setWord(wr.getSrcWord() + next.getSrcWord());
						wr2.setPos(POSTag.NOUN_ORG);
						i += 1;
						isBeProcess = true;
						result.add(wr2);
					} else if (optSegPath.get(i + 1).getLen() == 2 && "语文字杯".indexOf(next.getSrcWord()) != -1) {
						SegNode wr2 = new SegNode();
						wr2.setWord(wr.getSrcWord() + next.getSrcWord());
						wr2.setPos(POSTag.NOUN_ZHUAN);
						i += 1;
						isBeProcess = true;
						result.add(wr2);
					} else if ("裔".equals(next.getSrcWord())) {
						SegNode wr2 = new SegNode();
						wr2.setWord(wr.getSrcWord() + next.getSrcWord());
						wr2.setPos(POSTag.NOUN);
						i += 1;
						isBeProcess = true;
						result.add(wr2);
					}
				} else if (wr.getPos() == POSTag.VERB  || wr.getPos() == POSTag.VERB_NOUN  ||wr.getPos() == POSTag.NOUN)// v
				{
					if (i + 1 < optSegPath.size() && "员".equals(optSegPath.get(i + 1).getSrcWord())) {
						SegNode wr2 = new SegNode();
						wr2.setWord(wr.getSrcWord() + optSegPath.get(i + 1).getSrcWord());
						wr2.setPos(POSTag.NOUN);
						i += 1;
						isBeProcess = true;
						result.add(wr2);
					}
				}
				// www/nx ./w sina/nx;
				// ＥＩＭ/nx -６０１/m
				// ＳＨＭ/nx －/w １０１/m
				// 28280=='n'*256+'r'
				// 27904=='m'*256
				else if (wr.getPos() == POSTag.NOUN_LETTER && i + 1 < optSegPath.size()) {
					SegNode wr2 = new SegNode();
					wr2.setWord(wr.getSrcWord());
					wr2.setPos(POSTag.NOUN_LETTER);
					while (true) {
						SegNode nextSN = optSegPath.get(i + 1);
						if (nextSN.getPos() == POSTag.NOUN_LETTER || ".．-－".indexOf(nextSN.getSrcWord()) != -1
								|| (nextSN.getPos() == POSTag.NUM && Utility.isAllNum(nextSN.getSrcWord()))) {
							wr2.setWord(wr2.getSrcWord() + nextSN.getSrcWord());
							i++;
						} else
							break;
					}
					isBeProcess = true;
					result.add(wr2);
				}
				// If not processed,that's mean: not need to adjust;
				// just copy to the final result
				if (!isBeProcess) {
					SegNode wr2 = new SegNode();
					wr2.setWord(wr.getSrcWord());
					wr2.setPos(wr.getPos());
					result.add(wr2);

				}
			}
		}

		return result;
	}

}
