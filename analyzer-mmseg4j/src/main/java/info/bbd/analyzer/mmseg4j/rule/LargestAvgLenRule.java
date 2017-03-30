package info.bbd.analyzer.mmseg4j.rule;

import info.bbd.analyzer.mmseg4j.core.Chunk;

/**
 * Largest Average Word Length.<p/>
 *
 * 长度(Length)/词数
 *
 * @author wanggang
 *
 */
public class LargestAvgLenRule extends Rule {

	private double largestAvgLen;

	@Override
	public void addChunk(Chunk chunk) {
		if (chunk.getAvgLen() >= largestAvgLen) {
			largestAvgLen = chunk.getAvgLen();
			super.addChunk(chunk);
		}
	}

	@Override
	protected boolean isRemove(Chunk chunk) {
		return chunk.getAvgLen() < largestAvgLen;
	}

	@Override
	public void reset() {
		largestAvgLen = 0;
		super.reset();
	}

}
