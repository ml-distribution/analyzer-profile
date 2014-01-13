package org.ictclas4j.segment;

import java.util.ArrayList;

import org.ictclas4j.bean.Atom;
import org.ictclas4j.bean.Dictionary;
import org.ictclas4j.bean.SegNode;
import org.ictclas4j.bean.WordItem;
import org.ictclas4j.utility.POSTag;
import org.ictclas4j.utility.Utility;

public class GraphGenerate {

	/**
	 * 全切分,生成切分图.即找出所有可能的词组
	 *
	 * @param atoms
	 * @return
	 */
	public static SegGraph generate(ArrayList<Atom> atoms,Dictionary dict) {

		SegGraph segGraph = null;
		SegNode sn = null;
		Atom atom = null;

		if (atoms != null && atoms.size() > 0 && dict != null) {
			segGraph = new SegGraph();
			for (int i = 0; i < atoms.size(); i++) {
				atom = atoms.get(i);
				String word = atom.getWord();
				if (atom.getPos() == Utility.CT_CHINESE)
					sn = new SegNode(i, i + 1, 0,0, atom.getWord());
				else {
					double value = Utility.MAX_FREQUENCE;
					int pos = 0;

					switch (atom.getPos()) {
					case Utility.CT_INDEX:
					case Utility.CT_NUM:
						pos = -POSTag.NUM;// 'm'*256
						word = Utility.UNKNOWN_NUM;
						value = 0;
						break;
					case Utility.CT_DELIMITER:
						pos = POSTag.PUNC;// 'w'*256;
						break;
					case Utility.CT_LETTER:
						pos = -POSTag.NOUN_LETTER;//
						value = 0;
						word = Utility.UNKNOWN_LETTER;
						break;
					case Utility.CT_SINGLE:// 12021-2129-3121
						if (Utility.getCharCount("+-1234567890", atom.getWord()) == atom.getLen()) {
							pos = -POSTag.NUM;// 'm'*256
							word = Utility.UNKNOWN_NUM;
						} else {
							pos = -POSTag.NOUN_LETTER;//
							word = Utility.UNKNOWN_LETTER;
						}
						value = 0;
						break;
					default:
						pos = atom.getPos();// '?'*256;
						break;
					}

					sn = new SegNode(i, i + 1,pos, value , word);
				}

				sn.setSrcWord(atom.getWord());
				segGraph.insert(sn, true);
			}

			String word = null;
			for (int i = 0; i < atoms.size(); i++) {
				int j = i + 1;
				word = atoms.get(i).getWord();
				// 如果是“月份”，不要分割
				boolean flag = false;
				if (j < atoms.size()) {
					Atom a2 = atoms.get(j);
					if ("月".equals(word) && "份".equals(a2.getWord())) {
						segGraph.delete(i, j);
						segGraph.delete(i + 1, j + 1);
						word += a2.getWord();
						flag = true;
						j++;
					}
				}

				WordItem wi = null;
				for (; j <= atoms.size(); j++) {
					int totalFreq = 0;
					wi = dict.getMaxMatch(word);
					if (wi != null) {
						// find it
						if (word.equals(wi.getWord())) {
							ArrayList<WordItem> wis = dict.getHandle(word);
							for (WordItem w : wis)
								totalFreq += w.getFreq();

							// 1年内，1999年末
							if (word.length() == 2 && segGraph.getSize() > 0) {
								SegNode g2 = segGraph.getLast();
								if (Utility.isAllNum(g2.getWord()) || Utility.isAllChinese(g2.getWord())
										&& (g2.getWord().indexOf("年") == 0 || g2.getWord().indexOf("月") == 0)) {

									if ("末内中底前间初".indexOf(word.substring(1)) != -1)
										break;
								}
							}
							// 只有一个性词，存贮它
							SegNode sg = null;
							if (wis.size() == 1)
								sg = new SegNode(i, j,wis.get(0).getHandle(),totalFreq , word);
							else
								sg = new SegNode(i, j, 0,totalFreq , word);

							segGraph.insert(sg, true);

						}
						if (flag)
							i++;
						if (j < atoms.size()) {
							String word2 = atoms.get(j).getWord();
							word += word2;
						} else
							break;
					} else
						break;
				}
			}

		}
		return segGraph;
	}

	/**
	 * 生成二叉图表,每个节点表示相邻两个词组的耦合关系,如:说@的确
	 *
	 * @param sgs
	 */
	public static SegGraph biGenerate(SegGraph seg,Dictionary dict,Dictionary biDict) {

		SegGraph segGraph = null;
		final double smoothParam = 0.1;
		double curFreq;

		if (seg != null && dict != null && biDict != null) {
			segGraph = new SegGraph();
			ArrayList<SegNode> sgs = seg.getSnList();

			for (int i = 0; sgs != null && i < sgs.size(); i++) {
				SegNode sg = sgs.get(i);
				if (sg.getPos() >= 0)
					curFreq = sg.getValue();
				else
					curFreq = dict.getFreq(sg.getWord(), 2);

				// 得到下面行值和该列值相等的所有元素
				ArrayList<SegNode> nextSgs = seg.getNextElements(i);
				for (SegNode graph : nextSgs) {
					String twoWords = sg.getWord();
					twoWords += Utility.WORD_SEGMENTER;
					twoWords += graph.getWord();

					// 计算相临两个词之间的平滑值
					// -log{a*P(Ci-1)+(1-a)P(Ci|Ci-1)} Note 0<a<1
					int twoFreq = biDict.getFreq(twoWords, 3);
					double temp = (double) 1 / Utility.MAX_FREQUENCE;
					double value = smoothParam * (1 + curFreq) / (Utility.MAX_FREQUENCE + 80000);
					value += (1 - smoothParam) * ((1 - temp) * twoFreq / (1 + curFreq) + temp);
					value = -Math.log(value);

					if (value < 0)
						value += sg.getValue();

					SegNode sg2 = new SegNode();
					// 分隔符@前的词在链表中的位置
					int wordIndex = getWordIndex(sgs, sg);
					sg2.setRow(wordIndex);

					// 分隔符@后的词在链表中的位置
					wordIndex = getWordIndex(sgs, graph);
					sg2.setCol(wordIndex);
					sg2.setWord(twoWords);
					sg2.setPos(sg.getPos());
					sg2.setValue(value);
					segGraph.insert(sg2, false);
				}
			}
		}
		return segGraph;
	}

	private static int getWordIndex(ArrayList<SegNode> sgs, SegNode graph) {

		if (sgs != null && graph != null) {
			for (int i = 0; i < sgs.size(); i++) {
				if (sgs.get(i) == graph)
					return i;
			}
		}

		return -1;
	}

}
