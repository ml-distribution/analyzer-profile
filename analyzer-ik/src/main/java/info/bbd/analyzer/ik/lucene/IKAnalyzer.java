package info.bbd.analyzer.ik.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;

/**
 * IK分词器，Lucene Analyzer接口实现
 *
 * @author wanggang
 *
 */
public final class IKAnalyzer extends Analyzer {

	private boolean useSmart;

	public boolean getUseSmart() {
		return useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}

	/**
	 * IK分词器Lucene 3.5 Analyzer接口实现类，默认细粒度切分算法
	 */
	public IKAnalyzer() {
		this(Boolean.FALSE);
	}

	/**
	 * IK分词器Lucene Analyzer接口实现类
	 *
	 * @param useSmart 当为true时，分词器进行智能切分
	 */
	public IKAnalyzer(boolean useSmart) {
		this.useSmart = useSmart;
	}

	@Override
	protected TokenStreamComponents createComponents(final String fieldName) {
		Tokenizer tokenizer = new IKTokenizer(this.useSmart);
		//		return new TokenStreamComponents(tokenizer);
		return new TokenStreamComponents(tokenizer, new LowerCaseFilter(tokenizer));
	}

}
