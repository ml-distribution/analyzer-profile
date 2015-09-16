package cc.pp.analyzer.mmseg4j.core;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.analyzer.mmseg4j.rule.LargestAvgLenRule;
import cc.pp.analyzer.mmseg4j.rule.LargestSumDegreeFreedomRule;
import cc.pp.analyzer.mmseg4j.rule.MaxMatchRule;
import cc.pp.analyzer.mmseg4j.rule.Rule;
import cc.pp.analyzer.mmseg4j.rule.SmallestVarianceRule;

/**
 * 正向最大匹配, 加四个过虑规则的分词方式
 *
 * @author wanggang
 *
 */
public class ComplexSeg extends Seg {

	private static Logger logger = LoggerFactory.getLogger(ComplexSeg.class);

	private MaxMatchRule mmr = new MaxMatchRule();
	private List<Rule> otherRules = new ArrayList<>();

	private static boolean showChunk = false;

	public ComplexSeg(Dictionary dic) {
		super(dic);
		otherRules.add(new LargestAvgLenRule());
		otherRules.add(new SmallestVarianceRule());
		otherRules.add(new LargestSumDegreeFreedomRule());
	}

	@Override
	public Chunk seg(Sentence sen) {
		char[] chs = sen.getText();
		// 记录词的尾长
		int[] tailLen = new int[3];
		// int[] maxTailLen = new int[3];
		@SuppressWarnings("unchecked")
		// 记录词尾部允许的长度
		ArrayList<Integer>[] tailLens = new ArrayList[2];
		for (int i = 0; i < 2; i++) {
			tailLens[i] = new ArrayList<>();
		}
		CharNode[] cns = new CharNode[3];

		int[] offsets = new int[3]; //每个词在sen的开始位置
		mmr.reset();
		if (!sen.isFinish()) { // sen.getOffset() < chs.length
			if (showChunk) {
				System.out.println();
			}
			int maxLen = 0;
			offsets[0] = sen.getOffset();
			/*
			 * 遍历所有不同词长,还不是从最大到0(w[0]=maxLen(chs, offsets[0]); w[0]>=0; w[0]--)
			 * 可以减少一部分多余的查找.
			 */
			maxMatch(cns, 0, chs, offsets[0], tailLens, 0);
			for (int aIdx = tailLens[0].size() - 1; aIdx >= 0; aIdx--) {

				tailLen[0] = tailLens[0].get(aIdx);

				// 第二个词的开始位置
				offsets[1] = offsets[0] + 1 + tailLen[0];

				maxMatch(cns, 1, chs, offsets[1], tailLens, 1);
				for (int bIdx = tailLens[1].size() - 1; bIdx >= 0; bIdx--) {

					tailLen[1] = tailLens[1].get(bIdx);
					offsets[2] = offsets[1] + 1 + tailLen[1];

					// 第三个词只需要最长的
					tailLen[2] = maxMatch(cns, 2, chs, offsets[2]);

					int sumChunkLen = 0;
					for (int i = 0; i < 3; i++) {
						sumChunkLen += tailLen[i] + 1;
					}
					Chunk ck = null;
					if (sumChunkLen >= maxLen) {
						// 下一个chunk块的开始位置增量
						maxLen = sumChunkLen;
						ck = createChunk(sen, chs, tailLen, offsets, cns);
						mmr.addChunk(ck);

					}
					if (showChunk) {
						if (ck == null) {
							ck = createChunk(sen, chs, tailLen, offsets, cns);
							mmr.addChunk(ck);
						}
					}
				}

			}
			// maxLen个字符已经处理完
			sen.addOffset(maxLen);
			List<Chunk> chunks = mmr.remainChunks();
			// 其它规则过虑
			for (Rule rule : otherRules) {
				if (showChunk) {
					logger.info("-------filter before {} ----------", rule);
					printChunk(chunks);
				}
				if (chunks.size() > 1) {
					rule.reset();
					rule.addChunks(chunks);
					chunks = rule.remainChunks();
				} else {
					break;
				}
			}
			if (showChunk) {
				logger.info("-------remainChunks----------");
				printChunk(chunks);
			}
			if (chunks.size() > 0) {
				return chunks.get(0);
			}
		}

		return null;
	}

	private Chunk createChunk(Sentence sen, char[] chs, int[] tailLen, int[] offsets, CharNode[] cns/*, char[][] cks*/) {
		Chunk ck = new Chunk();

		for (int i = 0; i < 3; i++) {

			if (offsets[i] < chs.length) {
				// new Word(cks[i], sen.getStartOffset()+offsets[i]);
				ck.words[i] = new Word(chs, sen.getStartOffset(), offsets[i], tailLen[i] + 1);
				// 单字的要取得"字频计算出自由度"
				if (tailLen[i] == 0) {
					// dic.head(chs[offsets[i]]);
					CharNode cn = cns[i];
					if (cn != null) {
						ck.words[i].setDegree(cn.getFreq());
					}
				}
			}
		}

		return ck;
	}

	public static boolean isShowChunk() {
		return showChunk;
	}

	public static void setShowChunk(boolean showChunk) {
		ComplexSeg.showChunk = showChunk;
	}

}
