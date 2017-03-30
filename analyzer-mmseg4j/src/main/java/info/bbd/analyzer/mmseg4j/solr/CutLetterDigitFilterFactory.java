package info.bbd.analyzer.mmseg4j.solr;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import info.bbd.analyzer.mmseg4j.lucene.CutLetterDigitFilter;

/**
 * CutLetterDigitFilter 支持在 solr 上配置用
 *
 * @author wanggang
 *
 */
public class CutLetterDigitFilterFactory extends TokenFilterFactory {

	public CutLetterDigitFilterFactory(Map<String, String> args) {
		super(args);
	}

	@Override
	public TokenStream create(TokenStream input) {
		return new CutLetterDigitFilter(input);
	}

}
