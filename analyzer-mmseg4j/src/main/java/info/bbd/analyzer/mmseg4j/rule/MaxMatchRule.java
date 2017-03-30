package info.bbd.analyzer.mmseg4j.rule;

import info.bbd.analyzer.mmseg4j.core.Chunk;

/**
 * Maximum Matching.<p/>
 *
 * chuck中各个词的长度之和
 *
 * @author wanggang
 *
 */
public class MaxMatchRule extends Rule {

	private int maxLen;

	@Override
	public void addChunk(Chunk chunk) {
		if (chunk.getLen() >= maxLen) {
			maxLen = chunk.getLen();
			super.addChunk(chunk);
		}
	}

	@Override
	protected boolean isRemove(Chunk chunk) {
		return chunk.getLen() < maxLen;
	}

	@Override
	public void reset() {
		maxLen = 0;
		super.reset();
	}

}
