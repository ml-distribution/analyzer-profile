package org.ictclas4j.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.ictclas4j.bean.POS;
import org.ictclas4j.bean.SegNode;
import org.ictclas4j.bean.SegResult;


/**
 * 日志记录工具类
 *
 * @author sinboy
 *
 */
public class DebugUtil {

	/**
	 * 把分词过程中生成的中间结果输出到HMTL文件中
	 *
	 * @param snList
	 */
	public static void output2html(SegResult sr) {
		if (sr != null) {
			try {
				String html = "<html><head><title>ictclas4j分词结果</title></head>";
				html += "<body bgcolor=\"#CCFF99\">";
				html += sr.toHTML();
				html += "</body></html>";
				writeTxtFile("output/sr.html", html, false);
			} catch (IOException e) {
			}

		}
	}

	public static void outputPostag(ArrayList<SegNode> sns) {
		if (sns != null) {
			try {
				StringBuffer html = new StringBuffer();
				html.append("<html><head><title>ictclas4j分词结果</title></head>");
				html.append("<body bgcolor=\"#CCFF99\">");
				html.append("<p>进行原子分词后的结果：");
				html.append("<table border=\"1\" width=\"100%\">");
				for (SegNode sn : sns) {
					html.append("<tr>");
					html.append("<td width=\"10%\" bgcolor=\"#99CCFF\"  rowspan=\"" + sn.getPosSize() + "\">"
							+ sn.getWord() + "</td>");
					ArrayList<POS> allPos = sn.getAllPos();
					boolean flag = false;
					for (POS pos : allPos) {
						if (flag)
							html.append("<tr>");
						html.append("<td width=\"20%\" >" + pos.getTag() + "</td>");
						html.append("<td width=\"20%\" >" + pos.getFreq() + "</td>");
						html.append("<td width=\"20%\" >" + pos.getPrev() + "</td>");
						String sBest=pos.isBest()?"true":"&nbsp";
						html.append("<td width=\"20%\" >" + sBest + "</td>");
						html.append("</tr>");
						if (!flag)
							flag = true;
					}
				}
				html.append("</table>");
				html.append("</body></html>");
				writeTxtFile("output/postag.html", html.toString(), false);
			} catch (IOException e) {
			}

		}
	}

	/**
	 * 把分词过程中生成的中间结果输出到GUI图形界面上
	 *
	 * @param snList
	 */
	public static void output2gui(SegResult sr) {
		if (sr != null) {

		}
	}

	/**
	 * 写文本文件.如果写入数据中有换行符"\n"的话,自动在写入文件中换中
	 *
	 * @param fileName
	 *            文件路径
	 * @param txt
	 *            要写入的文件信息
	 * @param isAppend
	 *            是否以追加的方式写入
	 * @return
	 * @throws IOException
	 */
	public static boolean writeTxtFile(String fileName, String txt, boolean isAppend) throws IOException {
		FileWriter fw = null;
		PrintWriter out = null;

		if (fileName != null && txt != null)
			try {
				String parent;
				File fp;

				File file = new File(fileName);
				// 如果文件不存在，就创建一个，如果目录也不存在，也创建一个
				if (!file.exists()) {
					parent = file.getParent();
					if (parent != null) {
						fp = new File(parent);

						if (!fp.isDirectory())
							fp.mkdirs();
					}

				}

				String[] msgs = txt.split("\n");
				fw = new FileWriter(file, isAppend);
				out = new PrintWriter(fw);
				for (int i = 0; i < msgs.length; i++) {
					out.println(msgs[i]);
				}
				out.flush();
				out.close();
				return true;
			} catch (IOException e) {
				throw new IOException();
			} finally {
				if (out != null)
					out.close();
			}
		return false;
	}
}
