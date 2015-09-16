package cc.pp.analyzer.mmseg4j.solr;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.util.plugin.SolrCoreAware;

import cc.pp.analyzer.mmseg4j.core.Dictionary;

/**
 * mmseg4j 的 solr handler，用于检测词库，查看状态等
 *
 * @author wanggang
 *
 */
public class MMseg4jHandler extends RequestHandlerBase implements SolrCoreAware {

	//private File solrHome = null;
	private SolrResourceLoader loader = null;

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getSource() {
		return "$URL: https://github.com/ml-distribution/analyzer-profile/tree/master/analyzer-mmseg4j"
				+ "/src/main/java/cc/pp/analyzer/mmseg4j/solr/MMseg4jHandler.java $";
	}

	public String getSourceId() {
		return "$Revision: 63 $";
	}

	@Override
	public String getVersion() {
		return "5.1.0";
	}

	@Override
	public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {
		rsp.setHttpCaching(false);
		final SolrParams solrParams = req.getParams();

		String dicPath = solrParams.get("dicPath");
		Dictionary dict = DicUtils.getDict(dicPath, loader);

		NamedList<Object> result = new NamedList<>();
		result.add("dicPath", dict.getDicPath().toURI());

		// 仅仅用于检测词库是否有变化
		boolean check = solrParams.getBool("check", false);
		// 用于尝试加载词库，有此参数, check 参数可以省略。
		boolean reload = solrParams.getBool("reload", false);

		check |= reload;

		boolean changed = false;
		boolean reloaded = false;
		if (check) {
			changed = dict.wordsFileIsChange();
			result.add("changed", changed);
		}
		if (changed && reload) {
			reloaded = dict.reload();
			result.add("reloaded", reloaded);
		}
		rsp.add("result", result);
	}

	@Override
	public void inform(SolrCore core) {
		loader = core.getResourceLoader();
		//		solrHome = new File(loader.getInstanceDir());
	}

}
