package org.ictclas4j.segment;

import java.util.ArrayList;

import org.ictclas4j.bean.Sentence;
import org.ictclas4j.utility.GFString;
import org.ictclas4j.utility.Utility;


public class SentenceSeg {

	private final String src;
	private final ArrayList<Sentence> sens;

	public SentenceSeg(String src){
		this.src=src;
		sens=split();
	}

	/**
	 * 进行句子分隔
	 *
	 * @param src
	 * @return
	 */
	private ArrayList<Sentence> split( ) {

		ArrayList<Sentence> result = null;

		if (src != null) {
			result = new ArrayList<Sentence>();
			String s1 = Utility.SENTENCE_BEGIN;
			String[] ss = GFString.atomSplit(src);

			for (int i = 0; i < ss.length; i++) {
				// 如果是分隔符，比如回车换行/逗号等
				if (Utility.SEPERATOR_C_SENTENCE.indexOf(ss[i]) != -1
						|| Utility.SEPERATOR_LINK.indexOf(ss[i]) != -1
						|| Utility.SEPERATOR_C_SUB_SENTENCE.indexOf(ss[i]) != -1
						|| Utility.SEPERATOR_E_SUB_SENTENCE.indexOf(ss[i]) != -1) {
					// 如果不是回车换行和空格
					if (Utility.SEPERATOR_LINK.indexOf(ss[i]) == -1)
						s1 += ss[i];
					// 断句
					if (s1.length() > 0 && !Utility.SENTENCE_BEGIN.equals(s1)) {
						if (Utility.SEPERATOR_C_SUB_SENTENCE.indexOf(ss[i]) == -1
								&& Utility.SEPERATOR_E_SUB_SENTENCE
.indexOf(ss[i]) == -1)
							s1 += Utility.SENTENCE_END;

						result.add(new Sentence(s1, true));
						s1 = "";
					}

					// 是回车换行符或空格，则不需要进行分析处理
					if (Utility.SEPERATOR_LINK.indexOf(ss[i]) != -1) {
						result.add(new Sentence(ss[i]));
						s1 = Utility.SENTENCE_BEGIN;

					} else if (Utility.SEPERATOR_C_SENTENCE.indexOf(ss[i]) != -1
							|| Utility.SEPERATOR_E_SENTENCE.indexOf(ss[i]) != -1)
						s1 = Utility.SENTENCE_BEGIN;
					else s1 = Utility.SENTENCE_BEGIN;
					//						s1 = ss[i];

				} else
					s1 += ss[i];
			}

			if (s1.length() > 0 && !Utility.SENTENCE_BEGIN.equals(s1)) {
				s1 += Utility.SENTENCE_END;
				result.add(new Sentence(s1, true));
			}
		}
		return result;
	}

	public ArrayList<Sentence> getSens() {
		return sens;
	}

}
