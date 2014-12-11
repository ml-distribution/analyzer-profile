package org.ictclas4j.segment;

import java.util.ArrayList;

import org.ictclas4j.bean.Queue;
import org.ictclas4j.bean.QueueNode;
import org.ictclas4j.bean.SegNode;
import org.ictclas4j.utility.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * N-最短路径
 *
 * @author sinboy
 * @since 2007.5.17 updated
 */
public class NShortPath {

	private static Logger logger = LoggerFactory.getLogger(NShortPath.class);

	// 最短路径的数目
	private int pathCount;

	// 二叉分词图表
	private final SegGraph biSegGraph;

	// 每条路径对应的权值
	private double[][] pathWeight;

	// 记录当前节点的N个父亲点及其权重
	private Queue[] parent;

	// 分词图表中顶点个数
	private int vertex;

	public NShortPath(SegGraph bsg, int pathCount) {

		this.biSegGraph = bsg;
		this.pathCount = pathCount;

		if (bsg != null && bsg.getSize() > 0 && pathCount > 0) {
			vertex = bsg.getMaxCol() + 1;
			if (bsg.getMaxRow() + 1 > vertex)
				vertex = bsg.getMaxRow() + 1;

			parent = new Queue[vertex];
			pathWeight = new double[vertex][pathCount];
			for (int i = 0; i < pathWeight.length; i++)
				for (int j = 0; j < pathWeight[i].length; j++)
					pathWeight[i][j] = Utility.INFINITE_VALUE;

			for (int i = 0; i < vertex; i++) {
				parent[i] = new Queue();
			}
		}
	}

	/**
	 * 按列遍历图表，并把每一列中权重最小的取出来。
	 *
	 */
	private void shortPath() {

		int preNode = -1;
		double weight = 0;

		if (biSegGraph != null) {
			// 图表的列值是从1开始,所以忽略掉第0列
			for (int cur = 1; cur < vertex; cur++) {
				// 得到同一列的所有元素
				ArrayList<SegNode> colSgs = biSegGraph.getNodes(cur, true);
				if (colSgs == null || colSgs.size() == 0)
					return;

				Queue queWork = new Queue();
				for (SegNode seg : colSgs) {
					preNode = seg.getRow();
					weight = seg.getValue();

					if (preNode == 0) {
						queWork.push(new QueueNode(preNode, 0, weight));

					} else {
						if (pathWeight[preNode][0] != Utility.INFINITE_VALUE)
							queWork.push(new QueueNode(preNode, 0, weight + pathWeight[preNode][0]));
					}

				}

				// 记录每一个节点的N个前驱及权重
				QueueNode minNode = null;
				int pathIndex = 0;
				while ((minNode = queWork.pop()) != null && pathIndex < pathCount) {
					pathWeight[cur][pathIndex] = minNode.getWeight();
					parent[cur].push(minNode);
					logger.debug("pathWeight[" + cur + "][" + pathIndex + "]:" + pathWeight[cur][pathIndex]);
					logger.debug("parent[" + cur + "]:" + parent[cur]);
					pathIndex++;
				}
			}
		}
	}

	public ArrayList<ArrayList<Integer>> getPaths() {

		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> onePath = null;// 一条分词路径

		Queue queResult = null;
		int curNode, curIndex = 0;
		int pathIndex = 0;

		shortPath();
		if (vertex > 0) {

			queResult = new Queue();
			queResult.push(new QueueNode(vertex - 1, 0, 0));
			curNode = vertex - 1;
			curIndex = 0;

			while (!queResult.isEmpty()) {
				while (curNode > 0) {
					// Get its parent and store them in nParentNode,nParentIndex
					QueueNode qn = parent[curNode].pop(false);
					if (qn == null)
						qn = parent[curNode].top();
					if (qn != null) {
						curNode = qn.getParent();
						curIndex = qn.getIndex();
					}
					if (curNode > 0)
						queResult.push(new QueueNode(curNode, curIndex, 0));
				}

				if (curNode == 0) {
					// 输出一条分词路径
					QueueNode qn = null;
					onePath = new ArrayList<Integer>();
					onePath.add(curNode);
					while ((qn = queResult.pop(false)) != null)
						onePath.add(qn.getParent());
					result.add(onePath);
					queResult.resetIndex();
					pathIndex++;// 寻找下一条次短路径
					if (pathIndex == pathCount)
						break;

					// 如果找到有下一个前驱的节点，则把它的这个前驱压入栈中
					while ((qn = queResult.pop()) != null) {
						curNode = qn.getParent();
						QueueNode next = parent[curNode].pop(false);

						if (next != null) {
							curNode = next.getParent();
							next.setWeight(0);
							queResult.push(qn);
							queResult.push(next);
							break;
						}
					}
				}
			}
		}
		return result;
	}

	public int[] getPaths(int index) {

		int[] rs = null;
		ArrayList<ArrayList<Integer>> result = getPaths();
		if (result != null && index < result.size()) {
			rs = new int[result.get(index).size()];
			int i = 0;
			for (int p : result.get(index))
				rs[i++] = p;
		}

		return rs;
	}

	public void printPath(ArrayList<ArrayList<Integer>> paths) {

		if (paths != null) {
			for (int i = 0; i < paths.size(); i++) {
				String result = "path[" + i + "]:";
				for (int j : paths.get(i)) {
					result += j + ",";
				}
				logger.info(result);
			}
		}
	}

	public int getPathCount() {
		return pathCount;
	}

	public void setPathCount(int pathCount) {
		this.pathCount = pathCount;
	}
}
