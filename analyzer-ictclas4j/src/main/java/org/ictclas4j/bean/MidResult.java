package org.ictclas4j.bean;

import java.util.ArrayList;

/**
 * 记录分词时产生的中间结果
 * 
 * @author sinboy
 * @since 2007.5.24
 */
public class MidResult {
	private int index;// 句子的序列编号

	private String source;// 源数据

	private ArrayList<Atom> atoms;// 原子分词的结果

	private ArrayList<SegNode> segGraph;// 分词图表

	private ArrayList<SegNode> biSegGraph;// 二叉分词图表

	private ArrayList<ArrayList<Integer>> bipath;// 二叉分词路径
 

	private ArrayList<String> firstResult;// 初次分词结果

	private ArrayList<SegNode> optSegGraph;// 优化后的分词图表

	private ArrayList<SegNode> optBiSegGraph;// 优化后的二叉分词图表

	private ArrayList<ArrayList<Integer>> optBipath;// 优化后的二叉分词路径

	private ArrayList<String> optResult;// 优化后的分词结果
 

	public void setIndex(int index) {
		this.index = index;
	}

	public void setAtoms(ArrayList<Atom> atoms) {
		this.atoms = atoms;
	}

	public void setOptBiSegGraph(ArrayList<SegNode> biOptSegGraph) {
		this.optBiSegGraph = biOptSegGraph;
	}

	public void setBiSegGraph(ArrayList<SegNode> biSegGraph) {
		this.biSegGraph = biSegGraph;
	}
 

	public void setOptSegGraph(ArrayList<SegNode> optSegGraph) {
		this.optSegGraph = optSegGraph;
	}  

	public void setSegGraph(ArrayList<SegNode> segGraph) {
		this.segGraph = segGraph;
	}

	public void setBipath(ArrayList<ArrayList<Integer>> bipath) {
		this.bipath = bipath;
	}

	public void setOptBipath(ArrayList<ArrayList<Integer>> optBipath) {
		this.optBipath = optBipath;
	}
 

	public void addFirstResult(String result) {
		if(firstResult==null)
			firstResult=new ArrayList<String>();
		firstResult.add(result);
	}
	
	public void setFirstResult(ArrayList<String> resultList){
		this.firstResult=resultList;
	}
	
	public void addOptResult(String result) {
		if(optResult==null)
			optResult=new ArrayList<String>();
		optResult.add(result);
	}
	
	public void setOptResult(ArrayList<String> resultList){
		this.optResult=resultList;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String toHTML() {
		StringBuffer html = new StringBuffer();
		if (source != null) {
			// 显示分词前的原始内容
			html.append("<p>进行句子分割后的结果：");
			html.append("<table border=\"1\" width=\"100%\">");
			html.append("<tr><td width=\"10%\">第" + index + "句</td>");
			html.append("<td width=\"90%\">" + source + "</td></tr></table>");

			// 显示经过原子分词后的结果
			if (atoms != null) {
				html.append("<p>进行原子分词后的结果：");
				html.append("<table border=\"1\" width=\"100%\">");
				html.append("<tr>");
				html.append("<td width=\"10%\" bgcolor=\"#99CCFF\">序号</td>");
				html.append("<td width=\"40%\" bgcolor=\"#99CCFF\">原子</td>");
				html.append("<td width=\"25%\" bgcolor=\"#99CCFF\">长度(字节)</td>");
				html.append("<td width=\"25%\" bgcolor=\"#99CCFF\">pos</td>");
				html.append("</tr>");
				for (int i = 0; i < atoms.size(); i++) {
					Atom atom = atoms.get(i);
					html.append("<tr>");
					html.append("<td width=\"10%\">" + i + "</td>");
					html.append("<td width=\"40%\">" + atom.getWord() + "</td>");
					html.append("<td width=\"25%\">" + atom.getLen() + "</td>");
					html.append("<td width=\"25%\">" + atom.getPos() + "</td>");
					html.append("</tr>");
				}
				html.append("</table>");
			}

			// 显示初次生成的分词图表
			if (segGraph != null && segGraph.size() > 0) {
				html.append("<p>初次生成的分词图表：");
				html.append("<table border=\"1\" width=\"100%\">");
				html.append("<tr>");
				html.append("<td width=\"5%\"></td>");
				SegNode last = segGraph.get(segGraph.size() - 1);
				int width = last.getCol();
				int height = last.getRow();

				for (int i = 1; i <= width; i++)
					html.append("<td width=\"" + (95 / (double) width) + "%\" bgcolor=\"#99CCFF\">" + i + "</td>");
				html.append("</tr>");
				for (int i = 0; i <= height; i++) {
					html.append("<tr>");
					html.append("<td width=\"5%\">" + i + "</td>");
					for (int j = 1; j <= width; j++) {
						boolean flag = false;
						for (SegNode sn : segGraph) {
							if (i == sn.getRow() && j == sn.getCol()) {
								html.append("<td width=\"" + (95 / (double) width) + "%\"><a title=\"pos=" +sn.getPos()+" value="+sn.getValue()+"\">"+ sn.getWord() + "</a></td>");
								flag = true;
								break;
							}
						}
						if (!flag)
							html.append("<td width=\"" + (95 / (double) width) + "%\">&nbsp</td>");
					}
					html.append("</tr>");
				}
				html.append("</table>");
			}

			// 显示初次生成的二叉分词图表
			if (biSegGraph != null && biSegGraph.size() > 0) {
				html.append("<p>初次生成的二叉分词图表：");
				html.append("<table border=\"1\" width=\"100%\">");
				html.append("<tr>");
				html.append("<td width=\"5%\"></td>");
				SegNode last = biSegGraph.get(biSegGraph.size() - 1);
				int width = last.getCol();
				int height = last.getRow();

				for (int i = 1; i <= width; i++)
					html.append("<td width=\"" + (95 / (double) width) + "%\" bgcolor=\"#99CCFF\">" + i + "</td>");
				html.append("</tr>");
				for (int i = 0; i <= height; i++) {
					html.append("<tr>");
					html.append("<td width=\"5%\">" + i + "</td>");
					for (int j = 1; j <= width; j++) {
						boolean flag = false;
						for (SegNode sn : biSegGraph) {
							if (i == sn.getRow() && j == sn.getCol()) {
								html.append("<td width=\"" + (95 / (double) width) + "%\"><a title=\"pos=" +sn.getPos()+" value="+sn.getValue()+"\">"+ sn.getWord() + "</a></td>");
								flag = true;
								break;
							}
						}
						if (!flag)
							html.append("<td width=\"" + (95 / (double) width) + "%\">&nbsp</td>");
					}
					html.append("</tr>");
				}
				html.append("</table>");
			}

			// 显示生成的二叉分词路径
			if (bipath != null && bipath.size() > 0) {
				html.append("<p>初次生成的二叉分词路径：");
				html.append("<table border=\"1\" width=\"100%\">");
				html.append("<tr>");
				html.append("<td width=\"10%\" bgcolor=\"#99CCFF\">序号</td>");
				html.append("<td width=\"90%\" bgcolor=\"#99CCFF\">二叉分词路径</td>");
				html.append("</tr>");
				for (int i = 0; i < bipath.size(); i++) {
					html.append("<tr>");
					html.append("<td width=\"10%\">" + i + "</td>");
					html.append("<td width=\"90%\">");
					ArrayList<Integer> list = bipath.get(i);
					for (int index : list) {
						html.append(index + "&nbsp");
					}
					html.append("</td></tr>");
				}
				html.append("</table>");
			}
 

			// 显示初次生成的分词结果
			if (firstResult != null && firstResult.size() > 0) {
				html.append("<p>初次生成的分词结果：");
				html.append("<table border=\"1\" width=\"100%\">");
				html.append("<tr>");
				html.append("<td width=\"10%\" bgcolor=\"#99CCFF\">序号</td>");
				html.append("<td width=\"90%\" bgcolor=\"#99CCFF\">分词结果</td>");
				html.append("</tr>");

				for (int i=0;i< firstResult.size();i++) {
					html.append("<tr>");
					html.append("<td width=\"10%\">"+i+"</td>"); 
					html.append("<td width=\"90%\" ><font color=\"#FF0000\"><b>"+firstResult.get(i)+"</b></font</td>");
					html.append("</tr>");
				}
				html.append("</table>");
			}
			
			//显示经过人名、地名识别后的处理结果
			if (optSegGraph != null && optSegGraph.size() > 0) {
				html.append("<p>经过人名、地名识别后的分词图表：");
				html.append("<table border=\"1\" width=\"100%\">");
				html.append("<tr>");
				html.append("<td width=\"5%\"></td>");
				SegNode last = optSegGraph.get(optSegGraph.size() - 1);
				int width = last.getCol();
				int height = last.getRow();

				for (int i = 1; i <= width; i++)
					html.append("<td width=\"" + (95 / (double) width) + "%\" bgcolor=\"#99CCFF\">" + i + "</td>");
				html.append("</tr>");
				for (int i = 0; i <= height; i++) {
					html.append("<tr>");
					html.append("<td width=\"5%\">" + i + "</td>");
					for (int j = 1; j <= width; j++) {
						boolean flag = false;
						for (SegNode sn : optSegGraph) {
							if (i == sn.getRow() && j == sn.getCol()) {
								html.append("<td width=\"" + (95 / (double) width) + "%\"><a title=\"pos=" +sn.getPos()+" value="+sn.getValue()+"\">"+ sn.getWord() + "</a></td>");
								flag = true;
								break;
							}
						}
						if (!flag)
							html.append("<td width=\"" + (95 / (double) width) + "%\">&nbsp</td>");
					}
					html.append("</tr>");
				}
				html.append("</table>");
			}
			
			//显示结过优化后的二叉分词路径 
			if (optBiSegGraph != null && optBiSegGraph.size() > 0) {
				html.append("<p>经过优化后的二叉分词图表：");
				html.append("<table border=\"1\" width=\"100%\">");
				html.append("<tr>");
				html.append("<td width=\"5%\"></td>");
				SegNode last = optBiSegGraph.get(optBiSegGraph.size() - 1);
				int width = last.getCol();
				int height = last.getRow();

				for (int i = 1; i <= width; i++)
					html.append("<td width=\"" + (95 / (double) width) + "%\" bgcolor=\"#99CCFF\">" + i + "</td>");
				html.append("</tr>");
				for (int i = 0; i <= height; i++) {
					html.append("<tr>");
					html.append("<td width=\"5%\">" + i + "</td>");
					for (int j = 1; j <= width; j++) {
						boolean flag = false;
						for (SegNode sn : optBiSegGraph) {
							if (i == sn.getRow() && j == sn.getCol()) {
								html.append("<td width=\"" + (95 / (double) width) + "%\"><a title=\"pos=" +sn.getPos()+" value="+sn.getValue()+"\">"+ sn.getWord() + "</a></td>");
								flag = true;
								break;
							}
						}
						if (!flag)
							html.append("<td width=\"" + (95 / (double) width) + "%\">&nbsp</td>");
					}
					html.append("</tr>");
				}
				html.append("</table>");
			}
			
			//结这优化后的二叉分词路径
			if (optBipath != null && optBipath.size() > 0) {
				html.append("<p>经过优化后的二叉分词路径：");
				html.append("<table border=\"1\" width=\"100%\">");
				html.append("<tr>");
				html.append("<td width=\"10%\" bgcolor=\"#99CCFF\">序号</td>");
				html.append("<td width=\"90%\" bgcolor=\"#99CCFF\">二叉分词路径</td>");
				html.append("</tr>");
				for (int i = 0; i < optBipath.size(); i++) {
					html.append("<tr>");
					html.append("<td width=\"10%\">" + i + "</td>");
					html.append("<td width=\"90%\">");
					ArrayList<Integer> list = optBipath.get(i);
					for (int index : list) {
						html.append(index + "&nbsp");
					}
					html.append("</td></tr>");
				}
				html.append("</table>");
			}
			//显示经过优化后的分词结果
			if (optResult != null && optResult.size() > 0) {
				html.append("<p>经过优化后的分词结果：");
				html.append("<table border=\"1\" width=\"100%\">");
				html.append("<tr>");
				html.append("<td width=\"10%\" bgcolor=\"#99CCFF\">序号</td>");
				html.append("<td width=\"90%\" bgcolor=\"#99CCFF\">分词结果</td>");
				html.append("</tr>");

				for (int i=0;i< optResult.size();i++) {
					html.append("<tr>");
					html.append("<td width=\"10%\">"+i+"</td>"); 
					html.append("<td width=\"90%\" ><font color=\"#FF0000\"><b>"+optResult.get(i)+"</b></font</td>");
					html.append("</tr>");
				}
				html.append("</table>");
			}
			
			//显示最终的分词结果
		}
		return html == null ? null : html.toString();
	}
}
