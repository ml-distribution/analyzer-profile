package cc.pp.analyzer.paoding.examples.ch0;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

public class BoldFormatter implements Formatter {

	@Override
	public String highlightTerm(String originalText, TokenGroup group) {

		if (group.getTotalScore() <= 0) {
			return originalText;
		}
		return "<b>" + originalText + "</b>";
	}
}
