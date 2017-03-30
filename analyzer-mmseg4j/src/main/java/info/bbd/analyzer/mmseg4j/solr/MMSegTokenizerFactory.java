package info.bbd.analyzer.mmseg4j.solr;

import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.bbd.analyzer.mmseg4j.core.ComplexSeg;
import info.bbd.analyzer.mmseg4j.core.Dictionary;
import info.bbd.analyzer.mmseg4j.core.MaxWordSeg;
import info.bbd.analyzer.mmseg4j.core.Seg;
import info.bbd.analyzer.mmseg4j.core.SimpleSeg;
import info.bbd.analyzer.mmseg4j.lucene.MMSegTokenizer;

public class MMSegTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {

	private static final Logger logger = LoggerFactory.getLogger(MMSegTokenizerFactory.class);

	/* 线程内共享 */
	private final ThreadLocal<MMSegTokenizer> tokenizerLocal = new ThreadLocal<>();

	private Dictionary dic = null;

	public MMSegTokenizerFactory(Map<String, String> args) {
		super(args);
		// args不消耗，不知啥原因；官方自带的分词器可以消耗这些参数
		//		if (!args.isEmpty()) {
		//			throw new IllegalArgumentException("Unknown parameters: " + args);
		//		}
	}

	public Dictionary getDic() {
		return dic;
	}

	private Seg newSeg(Map<String, String> args) {
		Seg seg = null;
		logger.info("create new Seg ...");
		//default max-word
		String mode = args.get("mode");
		if ("simple".equals(mode)) {
			logger.info("use simple mode");
			seg = new SimpleSeg(dic);
		} else if ("complex".equals(mode)) {
			logger.info("use complex mode");
			seg = new ComplexSeg(dic);
		} else {
			logger.info("use max-word mode");
			seg = new MaxWordSeg(dic);
		}
		return seg;
	}

	@Override
	public Tokenizer create(AttributeFactory factory) {
		MMSegTokenizer tokenizer = tokenizerLocal.get();
		if (tokenizer == null) {
			tokenizer = newTokenizer(factory);
		}

		return tokenizer;
	}

	private MMSegTokenizer newTokenizer(AttributeFactory factory) {
		MMSegTokenizer tokenizer = new MMSegTokenizer(factory, newSeg(getOriginalArgs()));
		tokenizerLocal.set(tokenizer);

		return tokenizer;
	}

	@Override
	public void inform(ResourceLoader loader) {
		String dicPath = getOriginalArgs().get("dicPath");

		dic = DicUtils.getDict(dicPath, loader);

		logger.info("dic load... in={}", dic.getDicPath().toURI());
	}

}
