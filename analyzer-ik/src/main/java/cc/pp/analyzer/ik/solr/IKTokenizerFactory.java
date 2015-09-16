package cc.pp.analyzer.ik.solr;

import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.analyzer.ik.lucene.IKTokenizer;

/**
 * IK中文分词器工厂实现
 *
 * @author wanggang
 *
 */
public class IKTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {

	private static Logger logger = LoggerFactory.getLogger(IKTokenizerFactory.class);

	/* 线程共享 */
	private final ThreadLocal<IKTokenizer> tokenizerLocal = new ThreadLocal<>();

	private boolean useSmart = false;

	public IKTokenizerFactory(Map<String, String> args) {
		super(args);
		// args不消耗，不知啥原因；官方自带的分词器可以消耗这些参数
		if (!args.isEmpty()) {
			throw new IllegalArgumentException("Unknown parameters: " + args);
		}
	}

	@Override
	public Tokenizer create(AttributeFactory factory) {
		IKTokenizer tokenizer = tokenizerLocal.get();
		if (tokenizer == null) {
			tokenizer = newTokenizer(factory);
		}
		return tokenizer;
	}

	private IKTokenizer newTokenizer(AttributeFactory factory) {
		IKTokenizer tokenizer = new IKTokenizer(factory, useSmart);
		tokenizerLocal.set(tokenizer);
		return tokenizer;
	}

	@Override
	public void inform(ResourceLoader loader) {
		String useSmartParam = getOriginalArgs().get("useSmart");
		useSmart = (useSmartParam != null ? Boolean.parseBoolean(useSmartParam) : false);
		logger.info("IKTokenizerFactory loading resource ...");
	}

}
