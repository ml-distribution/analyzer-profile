package cc.pp.analyzer.mmseg4j.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.pp.analyzer.mmseg4j.core.Chunk;

/**
 * 过虑规则的抽象类
 *
 * @author wanggang
 *
 */
public abstract class Rule {

	protected List<Chunk> chunks;

	public void addChunks(List<Chunk> chunks) {
		for (Chunk chunk : chunks) {
			addChunk(chunk);
		}
	}

	/**
	 * 添加 chunk
	 *
	 * @throws NullPointerException, if chunk == null.
	 */
	public void addChunk(Chunk chunk) {
		chunks.add(chunk);
	}

	/**
	 * @return 返回规则过虑后的结果
	 */
	public List<Chunk> remainChunks() {
		for (Iterator<Chunk> it = chunks.iterator(); it.hasNext();) {
			Chunk chunk = it.next();
			if (isRemove(chunk)) {
				it.remove();
			}
		}
		return chunks;
	}

	/**
	 * 判断 chunk 是否要删除
	 */
	protected abstract boolean isRemove(Chunk chunk);

	public void reset() {
		chunks = new ArrayList<>();
	}

}
