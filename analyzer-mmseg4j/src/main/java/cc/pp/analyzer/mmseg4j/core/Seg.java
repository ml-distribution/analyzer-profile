package cc.pp.analyzer.mmseg4j.core;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分词抽象类
 *
 * @author wanggang
 *
 */
public abstract class Seg {

	private static Logger logger = LoggerFactory.getLogger(Seg.class);

	protected Dictionary dic;

	public Seg(Dictionary dic) {
		super();
		this.dic = dic;
	}

	/**
	 * 输出 chunks, 调试用
	 */
	protected void printChunk(List<Chunk> chunks) {
		for (Chunk ck : chunks) {
			logger.info(ck + " -> " + ck.toFactorString());
		}
	}

	/**
	 * @see Dictionary#isUnit(Character)
	 */
	protected boolean isUnit(int codePoint) {
		return dic.isUnit((char) codePoint);
	}

	/**
	 * 查找chs[offset]后面的 tailLen个char是否为词
	 *
	 * @return 返回chs[offset]字符结点下的词尾索引号,没找到返回 -1
	 */
	protected int search(char[] chs, int offset, int tailLen) {
		if (tailLen == 0) {
			return -1;
		}
		CharNode cn = dic.head(chs[offset]);

		return search(cn, chs, offset, tailLen);
	}

	/**
	 * 没有数组的复制.
	 */
	protected int search(CharNode cn, char[] chs, int offset, int tailLen) {
		if (tailLen == 0 || cn == null) {
			return -1;
		}

		return dic.search(cn, chs, offset, tailLen);
	}

	/**
	 * 最大匹配<br/>
	 * 从 chs[offset] 开始匹配, 同时把 chs[offset] 的字符结点保存在 cns[cnIdx]
	 *
	 * @return 最大匹配到的词尾长, > 0 找到
	 */
	protected int maxMatch(CharNode[] cns, int cnIdx, char[] chs, int offset) {
		CharNode cn = null;
		if (offset < chs.length) {
			cn = dic.head(chs[offset]);
		}
		cns[cnIdx] = cn;

		return dic.maxMatch(cn, chs, offset);
	}

	/**
	 * 匹配,同时找出长度. <br/>
	 * 从 chs[offset] 开始找所有匹配的词, 找到的放到 tailLens[tailLensIdx] 中. <br/>
	 * 同时把 chs[offset] 的字符结点保存在 cns[cnIdx].
	 */
	protected void maxMatch(CharNode[] cns, int cnIdx, char[] chs, int offset, ArrayList<Integer>[] tailLens,
			int tailLensIdx) {
		CharNode cn = null;
		if (offset < chs.length) {
			cn = dic.head(chs[offset]);
		}
		cns[cnIdx] = cn;
		dic.maxMatch(cn, tailLens[tailLensIdx], chs, offset);
	}

	/**
	 * 对句子 sen 进行分词
	 *
	 * @return 不返回 null
	 */
	public abstract Chunk seg(Sentence sen);

}
