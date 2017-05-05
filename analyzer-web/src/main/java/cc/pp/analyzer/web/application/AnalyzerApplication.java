package cc.pp.analyzer.web.application;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import cc.pp.analyzer.web.core.AnalyzerCore;
import cc.pp.analyzer.web.domain.WordsList;
import cc.pp.analyzer.web.resource.AnalyzerResource;

public class AnalyzerApplication extends Application {

	private final AnalyzerCore analyzerCore;

	public AnalyzerApplication() {
		analyzerCore = new AnalyzerCore();
	}

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/analyzer/{analyzer}/type/{type}/text/{text}", AnalyzerResource.class);
		return router;
	}

	/**
	 * MMSeg4j分词器
	 * @param type 分词器类别，0--simple, 1--maxword, 2--complex
	 */
	public WordsList getMMSeg4jSegWords(String str, int type) {
		return analyzerCore.getMMSeg4jSegWords(str, type);
	}

	/**
	 * IK 分词器
	 * @param type  是否使用智能分词: 0--否，1--是
	 */
	public WordsList getIKSegWords(String str, int type) {
		return analyzerCore.getIKSegWords(str, type);
	}

	/**
	 * 复旦大学关键词提取
	 */
	//	public WordsList getFudanKeywords(String str) {
	//		return analyzerCore.getFudanKeywords(str, 1_000);
	//	}

	public void close() {
		analyzerCore.close();
	}

}
