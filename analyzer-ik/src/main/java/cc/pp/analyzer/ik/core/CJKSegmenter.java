package cc.pp.analyzer.ik.core;

import java.util.LinkedList;
import java.util.List;

import cc.pp.analyzer.ik.dic.Dictionary;
import cc.pp.analyzer.ik.dic.Hit;

/**
 * 中文-日韩文子分词器
 *
 * @author wanggang
 *
 */
class CJKSegmenter implements ISegmenter {

	// 子分词器标签
	static final String SEGMENTER_NAME = "CJK_SEGMENTER";
	// 待处理的分词hit队列
	private List<Hit> tmpHits;

	CJKSegmenter() {
		this.tmpHits = new LinkedList<>();
	}

	/* (non-Javadoc)
	 * @see org.wltea.analyzer.core.ISegmenter#analyze(org.wltea.analyzer.core.AnalyzeContext)
	 */
	@Override
	public void analyze(AnalyzeContext context) {
		if (CharacterUtil.CHAR_USELESS != context.getCurrentCharType()) {

			// 优先处理tmpHits中的hit
			if (!this.tmpHits.isEmpty()) {
				// 处理词段队列
				Hit[] tmpArray = this.tmpHits.toArray(new Hit[this.tmpHits.size()]);
				for (Hit hit : tmpArray) {
					hit = Dictionary.getSingleton().matchWithHit(context.getSegmentBuff(), context.getCursor(), hit);
					if (hit.isMatch()) {
						// 输出当前的词
						Lexeme newLexeme = new Lexeme(context.getBufferOffset(), hit.getBegin(), context.getCursor()
								- hit.getBegin() + 1, Lexeme.TYPE_CNWORD);
						context.addLexeme(newLexeme);

						// 不是词前缀，hit不需要继续匹配，移除
						if (!hit.isPrefix()) {
							this.tmpHits.remove(hit);
						}

					} else if (hit.isUnmatch()) {
						// hit不是词，移除
						this.tmpHits.remove(hit);
					}
				}
			}

			//*********************************
			// 再对当前指针位置的字符进行单字匹配
			Hit singleCharHit = Dictionary.getSingleton().matchInMainDict(context.getSegmentBuff(),
					context.getCursor(), 1);
			// 首字成词
			if (singleCharHit.isMatch()) {
				// 输出当前的词
				Lexeme newLexeme = new Lexeme(context.getBufferOffset(), context.getCursor(), 1, Lexeme.TYPE_CNWORD);
				context.addLexeme(newLexeme);

				// 同时也是词前缀
				if (singleCharHit.isPrefix()) {
					// 前缀匹配则放入hit列表
					this.tmpHits.add(singleCharHit);
				}
				// 首字为词前缀
			} else if (singleCharHit.isPrefix()) {
				// 前缀匹配则放入hit列表
				this.tmpHits.add(singleCharHit);
			}

		} else {
			// 遇到CHAR_USELESS字符
			// 清空队列
			this.tmpHits.clear();
		}

		// 判断缓冲区是否已经读完
		if (context.isBufferConsumed()) {
			// 清空队列
			this.tmpHits.clear();
		}

		// 判断是否锁定缓冲区
		if (this.tmpHits.size() == 0) {
			context.unlockBuffer(SEGMENTER_NAME);

		} else {
			context.lockBuffer(SEGMENTER_NAME);
		}
	}

	/* (non-Javadoc)
	 * @see org.wltea.analyzer.core.ISegmenter#reset()
	 */
	@Override
	public void reset() {
		// 清空队列
		this.tmpHits.clear();
	}

}
