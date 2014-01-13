package org.ictclas4j.segment;

import java.util.ArrayList;

import org.ictclas4j.bean.Atom;
import org.ictclas4j.utility.GFString;
import org.ictclas4j.utility.Utility;

/**
 * 原子分词
 * @author sinboy
 * @since 2007.6.1
 */
public class AtomSeg {

	private final String str;

	private final ArrayList<Atom> atoms;

	public AtomSeg(String src) {
		this.str = src;
		atoms = atomSplit();
	}

	/**
	 * <pre>
	 *                 原子分词,分成一个个的独立字符,开始标志和结束标志做一个原子单位看等.
	 *                 比如:始##始他说的确实在理末##末--&gt;始##始 他 说 的 确 实 在 理 末##末
	 * </pre>
	 *
	 * @param str
	 *            源字符串
	 * @return
	 */
	private ArrayList<Atom> atomSplit() {

		ArrayList<Atom> result = null;

		if (str != null && str.length() > 0) {
			String sAtom = "";
			result = new ArrayList<Atom>();
			String[] ss = GFString.atomSplit(str);

			int index = str.indexOf(Utility.SENTENCE_BEGIN);
			if (index == 0) {
				Atom atom = new Atom();
				atom.setWord(Utility.SENTENCE_BEGIN);
				atom.setLen(Utility.SENTENCE_BEGIN.length());
				atom.setPos(Utility.CT_SENTENCE_BEGIN);
				result.add(atom);
				index += Utility.SENTENCE_BEGIN.length();
			}

			if (index == -1)
				index = 0;
			for (int i = index; i < ss.length; i++) {
				if (Utility.SENTENCE_END.equals(str.substring(i))) {
					Atom atom = new Atom();
					atom.setWord(Utility.SENTENCE_END);
					atom.setLen(Utility.SENTENCE_END.length());
					atom.setPos(Utility.CT_SENTENCE_END);
					result.add(atom);
					break;
				}

				String s = ss[i];
				sAtom += s;
				int curType = Utility.charType(s);
				if (".".equals(s)
						&& (i + 1 < ss.length && (Utility.charType(ss[i + 1]) == Utility.CT_NUM || GFString
								.isNumeric(ss[i+1]))))
					curType = Utility.CT_NUM;

				// 如果是汉字、分隔符等
				if (curType == Utility.CT_CHINESE || curType == Utility.CT_INDEX || curType == Utility.CT_DELIMITER
						|| curType == Utility.CT_OTHER) {

					Atom atom = new Atom();
					atom.setWord(s);
					atom.setLen(s.length());
					atom.setPos(curType);
					result.add(atom);
					sAtom = "";
				}
				// 如果是数字、字母、单字节符号，则把相邻的这些做为一个原子。比如：三星SHX-123型号的手机，则其中的SHX-123就是一个原子
				else {
					int nextType = 255;// 下一个字符的类型
					if (i < ss.length - 1)
						nextType = Utility.charType(ss[i + 1]);
					if (nextType != curType || i == ss.length - 1) {
						Atom atom = new Atom();
						atom.setWord(sAtom);
						atom.setLen(sAtom.length());
						atom.setPos(curType);
						result.add(atom);
						sAtom = "";
					}
				}
			}

		}
		return result;
	}

	public ArrayList<Atom> getAtoms() {
		return atoms;
	}

}
