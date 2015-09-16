package cc.pp.analyzer.mmseg4j.solr;

import java.io.File;

import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.solr.core.SolrResourceLoader;

import cc.pp.analyzer.mmseg4j.core.Dictionary;

public class DicUtils {

	public static Dictionary getDict(String dicPath, ResourceLoader loader) {
		Dictionary dic = null;
		if (dicPath != null) {
			File f = new File(dicPath);
			// 相对目录
			if (!f.isAbsolute() && loader instanceof SolrResourceLoader) {
				SolrResourceLoader srl = (SolrResourceLoader) loader;
				dicPath = srl.getInstanceDir() + dicPath;
				f = new File(dicPath);
			}

			dic = Dictionary.getInstance(f);
		} else {
			dic = Dictionary.getInstance();
		}

		return dic;
	}

}
