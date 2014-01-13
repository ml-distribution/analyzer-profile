package org.ictclas4j.bean;

import java.util.ArrayList;

/**
 * 队列
 * 
 * @author sinboy
 * 
 */
public class Queue {
	private int curIndex;// 当前位置

	private ArrayList<QueueNode> queue;

	public boolean push(QueueNode node) {
		if (queue == null)
			queue = new ArrayList<QueueNode>();
		if (node != null) {
			int i = 0;
			for (; i < queue.size(); i++) {
				if (queue.get(i).getWeight() < node.getWeight())
					continue;
				else
					break;
			}

			if (i == queue.size())
				queue.add(node);
			else
				queue.add(i, node);
			return true;
		}

		return false;

	}

	public QueueNode pop() {
		return pop(true);
	}

	public QueueNode pop(boolean isDelete) {
		QueueNode qn = null;

		if (queue != null && curIndex >= 0 && curIndex < queue.size()) {
			if (isDelete)
				qn = queue.remove(0);
			else
				qn = queue.get(curIndex++);
		}
		return qn;
	}

	public QueueNode top() {
		QueueNode qn = null;

		if (queue != null && queue.size() > 0) {
			if (curIndex < queue.size())
				qn = queue.get(curIndex);
			else
				qn = queue.get(queue.size() - 1);
		}
		return qn;
	}

	public boolean isEmpty() {
		if (queue == null || queue.size() == 0)
			return true;
		return false;
	}

	public boolean isSingle() {
		if (queue != null && queue.size() == 1)
			return true;
		return false;
	}

	public void resetIndex() {
		curIndex = 0;
	}
}
