package org.ictclas4j.segment;

import java.util.ArrayList;

import org.ictclas4j.bean.ContextStat;
import org.ictclas4j.bean.POS;
import org.ictclas4j.bean.SegNode;
import org.ictclas4j.utility.Utility;


/**
 * 分词图表，即二维表
 *
 * @author sinboy
 * @since 2006.6
 *
 */
public class SegGraph {

	private boolean isRowFirst;// 是否按行优先

	private ArrayList<SegNode> snList;// 分词图表实际是用链表来表示

	public SegGraph() {

	}

	public SegGraph(ArrayList<SegNode> snList) {
		this.snList = snList;
	}

	public SegNode getElement(int row, int col) {

		SegNode result = new SegNode();
		result.setValue(  Utility.INFINITE_VALUE );
		// if (row > m_nRow || col > m_nCol)
		// return null;

		int index = 0;
		if (snList != null) {
			if (isRowFirst) {
				for (int i = 0; i < snList.size(); i++, index++) {
					SegNode sg = snList.get(i);
					if (row != -1 && sg.getRow() < row || col != -1 && sg.getRow() == row && sg.getCol() < col)
						continue;
					else
						break;
				}
			} else {
				for (int i = 0; i < snList.size(); i++, index++) {
					SegNode sg = snList.get(i);
					if (col != -1 && sg.getCol() < col || row != -1 && sg.getCol() == col && sg.getRow() < row)
						continue;
					else
						break;
				}
			}

			// Find it and return the value
			if (index < snList.size()) {
				SegNode sg = snList.get(index);
				if ((sg.getRow() == row || row == -1) && (sg.getCol() == col || col == -1))
					result = sg;
			}
		}

		return result;
	}

	/**
	 * 设置元素.如果能在图表中找到,重新设值.否则添加进去.
	 *
	 * @param sg
	 * @return
	 */
	public boolean setElement(SegNode sg) {

		if (sg != null) {
			if (snList == null)
				snList = new ArrayList<SegNode>();

			int i = 0;
			SegNode sgTemp = null;
			if (isRowFirst) {
				for (i = 0; i < snList.size(); i++) {
					sgTemp = snList.get(i);
					if (sgTemp.getRow() < sg.getRow() || sgTemp.getRow() == sg.getRow()
							&& sgTemp.getCol() < sg.getCol())
						continue;
					else
						break;
				}
			} else {
				for (i = 0; i < snList.size(); i++) {
					sgTemp = snList.get(i);
					if (sgTemp.getCol() < sg.getCol() || sgTemp.getCol() == sg.getCol()
							&& sgTemp.getRow() < sg.getRow())
						continue;
					else
						break;
				}
			}

			if (sgTemp != null && sgTemp.getRow() == sg.getRow() && sgTemp.getCol() == sg.getCol())
				sgTemp = sg;
			else if (i > 0)
				snList.add(i - 1, sg);

		}
		return false;
	}

	/**
	 * 得到所有列值为Col的元素
	 *
	 * @param curIndex
	 *            当前索引值，表示列值或行值
	 * @param isColFirst
	 *            是否按列优先进行遍历
	 * @return
	 */
	public ArrayList<SegNode> getNodes(int curIndex, boolean isColFirst) {
		ArrayList<SegNode> result = null;

		if (snList != null && snList.size() > 0 && curIndex >= 0) {
			result = new ArrayList<SegNode>();
			for (int i = 0; i < snList.size(); i++) {
				SegNode sg = snList.get(i);
				if (isColFirst) {
					if (sg.getCol() == curIndex)
						result.add(sg);
				} else {
					if (sg.getRow() == curIndex)
						result.add(sg);
				}

			}
		}
		return result;
	}

	/**
	 * 把SegGraph插入的列表当中
	 *
	 * @param snList
	 * @param graph
	 * @param isRowFirst
	 *            是否按行优先原则
	 * @return 如果插入成功返回True,否则返回False
	 */
	public boolean insert(SegNode graph, boolean isRowFirst) {

		SegNode sg = null;
		if (snList == null)
			snList = new ArrayList<SegNode>();

		if (graph != null) {
			if (snList.size() == 0) {
				snList.add(graph);
				return true;
			}

			for (int i = 0; i < snList.size(); i++) {
				sg = snList.get(i);

				if (isRowFirst) {
					// 到最后一个节点
					if (i == snList.size() - 1) {
						if (graph.getRow() > sg.getRow()
								|| (graph.getRow() == sg.getRow() && graph.getCol() > sg.getCol()))
							snList.add(graph);
						else {
							if (graph.getCol() == sg.getCol()) {
								snList.set(i, graph);
							} else
								snList.add(i, graph);
						}
						return true;
					}

					if (graph.getRow() > sg.getRow() || (graph.getRow() == sg.getRow() && graph.getCol() > sg.getCol()))
						continue;
					else {
						if (graph.getRow()==sg.getRow() && graph.getCol() == sg.getCol()) {
							snList.set(i, graph);
						} else
							snList.add(i, graph);

						return true;
					}
				} else {
					// 到最后一个节点
					if (i == snList.size() - 1) {
						if (graph.getCol() > sg.getCol()
								|| (graph.getCol() == sg.getCol() && graph.getRow() > sg.getRow()))
							snList.add(graph);
						else {
							if (graph.getRow()==sg.getRow() && graph.getCol() == sg.getCol()) {
								snList.set(i, graph);
							} else
								snList.add(i, graph);
						}
						return true;
					}

					if (graph.getCol() > sg.getCol() || (graph.getCol() == sg.getCol() && graph.getRow() > sg.getRow()))
						continue;
					else {
						if (graph.getRow() == sg.getRow()) {
							snList.set(i, graph);
						} else
							snList.add(i, graph);

						return true;
					}
				}
			}
		}

		return false;
	}

	public SegNode delete(int row, int col) {

		SegNode result = null;

		if (snList != null && snList.size() > 0) {
			for (SegNode sn : snList) {
				if (sn.getRow() == row && sn.getCol() == col) {
					snList.remove(sn);
					return sn;
				}
			}
		}

		return result;
	}

	/**
	 * 得到下一个行值和该列值相等的所有元素。
	 *
	 * @param snList
	 * @param curIndex
	 *            当前元素的位置
	 * @return
	 */
	public ArrayList<SegNode> getNextElements(int curIndex) {

		ArrayList<SegNode> result = null;

		if (snList != null && snList.size() > 0 && curIndex >= 0 && curIndex < snList.size()) {
			result = new ArrayList<SegNode>();
			SegNode curSg = snList.get(curIndex);

			for (int i = curIndex + 1; i < snList.size(); i++) {
				SegNode sg = snList.get(i);
				if (sg.getRow() == curSg.getCol())
					result.add(sg);

			}
		}
		return result;
	}

	public boolean isRowFirst() {
		return isRowFirst;
	}

	public void setRowFirst(boolean isRowFirst) {
		this.isRowFirst = isRowFirst;
	}

	public ArrayList<SegNode> getSnList() {
		return snList;
	}

	public void setSnList(ArrayList<SegNode> sgs) {
		this.snList = sgs;
	}

	public int getSize() {
		if (snList != null)
			return snList.size();
		else
			return -1;
	}

	public int getMaxRow() {
		int result = -1;

		if (snList != null && snList.size() > 0) {
			int size = snList.size();
			SegNode sn = snList.get(size - 1);
			result = sn.getRow();
		}

		return result;
	}

	public int getMaxCol() {
		int result = -1;

		if (snList != null && snList.size() > 0) {
			int size = snList.size();
			SegNode sn = snList.get(size - 1);
			result = sn.getCol();
		}

		return result;
	}

	/**
	 * 获取前一个词与当前词最匹配的词性位置
	 *
	 */
	public void getBestPrev(ContextStat context) {

		if (snList != null) {
			SegNode sn = null;
			ArrayList<POS> posList = null;
			for (int i = 1; i < snList.size(); i++) {
				sn = snList.get(i);
				posList = sn.getAllPos();
				for (int j = 0; posList != null && j < posList.size(); j++) {
					double minFee = 1000000;
					int minPrev = 100000;
					POS pos = posList.get(j);
					SegNode psn = snList.get(i - 1);
					ArrayList<POS> pposList = psn.getAllPos();
					for (int k = 0; pposList != null && k < pposList.size(); k++) {
						double temp = -Math.log(context
								.getPossibility(0, pposList.get(k).getTag(), pos.getTag()));
						temp += pposList.get(k).getFreq();// Add the fees
						if (temp < minFee) {
							minFee = temp;
							minPrev = k;
						}
					}
					pos.setPrev(minPrev);
					pos.setFreq(pos.getFreq() + minFee);
				}
			}
		}
	}

	public SegNode getLast() {
		if (snList != null && snList.size() > 0) {
			return snList.get(snList.size() - 1);
		} else
			return null;
	}
}
