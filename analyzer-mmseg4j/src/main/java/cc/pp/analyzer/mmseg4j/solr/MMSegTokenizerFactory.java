package cc.pp.analyzer.mmseg4j.solr;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import cc.pp.analyzer.mmseg4j.ComplexSeg;
import cc.pp.analyzer.mmseg4j.Dictionary;
import cc.pp.analyzer.mmseg4j.MaxWordSeg;
import cc.pp.analyzer.mmseg4j.Seg;
import cc.pp.analyzer.mmseg4j.SimpleSeg;
import cc.pp.analyzer.mmseg4j.analyzer.MMSegTokenizer;

public class MMSegTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {

	static final Logger log = Logger.getLogger(MMSegTokenizerFactory.class.getName());
	/* 线程内共享 */
	private final ThreadLocal<MMSegTokenizer> tokenizerLocal = new ThreadLocal<MMSegTokenizer>();
	private Dictionary dic = null;

	public MMSegTokenizerFactory(Map<String, String> args) {
		super(args);
		// args不消耗，不知啥原因；官方自带的分词器可以消耗这些参数
		//		if (!args.isEmpty()) {
		//			throw new IllegalArgumentException("Unknown parameters: " + args);
		//		}
	}

	private Seg newSeg(Map<String, String> args) {
		Seg seg = null;
		log.info("create new Seg ...");
		//default max-word
		String mode = args.get("mode");
		if ("simple".equals(mode)) {
			log.info("use simple mode");
			seg = new SimpleSeg(dic);
		} else if ("complex".equals(mode)) {
			log.info("use complex mode");
			seg = new ComplexSeg(dic);
		} else {
			log.info("use max-word mode");
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

		dic = Utils.getDict(dicPath, loader);

		log.info("dic load... in=" + dic.getDicPath().toURI());
	}

}
