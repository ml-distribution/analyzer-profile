package info.bbd.analyzer.mmseg4j.solr;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.solr.analysis.TokenizerChain;
import org.apache.solr.schema.FieldType;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.bbd.analyzer.mmseg4j.core.Dictionary;
import info.bbd.analyzer.mmseg4j.core.Seg;
import info.bbd.analyzer.mmseg4j.core.SimpleSeg;
import info.bbd.analyzer.mmseg4j.core.Dictionary.FileLoading;
import info.bbd.analyzer.mmseg4j.lucene.AnalyzerTest;
import info.bbd.analyzer.mmseg4j.solr.MMSegTokenizerFactory;

public class MMSegTokenizerFactoryTest extends AbstractSolrTestCase {

	private static final Logger logger = LoggerFactory.getLogger(MMSegTokenizerFactoryTest.class);

	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig.xml", "schema.xml", getFile("solr-4.8/sentiment").getParent(), "sentiment");
	}

	private Dictionary getDictionaryByFieldType(String fieldTypeName) {
		FieldType ft = h.getCore().getLatestSchema().getFieldTypeByName(fieldTypeName);
		Analyzer a = ft.getIndexAnalyzer();
		assertEquals(a.getClass(), TokenizerChain.class);

		TokenizerChain tc = (TokenizerChain) a;
		TokenizerFactory tf = tc.getTokenizerFactory();
		assertEquals(tf.getClass(), MMSegTokenizerFactory.class);

		MMSegTokenizerFactory mtf = (MMSegTokenizerFactory) tf;

		assertNotNull(mtf.getDic());

		return mtf.getDic();
	}

	private void assertTokenizerFactory(final String fieldName, final Seg seg) throws IOException {
		logger.info("assert TokenizerFactory field type={}", fieldName);
		FileInputStream fis = new FileInputStream("src/test/resources/text-sentence.txt");
		try {
			Dictionary.load(fis, new FileLoading() {

				@Override
				public void row(String line, int n) {
					List<String> mwords = AnalyzerTest.toMMsegWords(line, seg);

					assertU(adoc("id", String.valueOf(n), fieldName, line));
					assertU(commit());

					logger.debug("words = {}", mwords);
					for (String word : mwords) {
						assertQ(req("q", "id:" + String.valueOf(n) + " AND " + fieldName + ":" + word),
								"//*[@numFound='1']", "//result/doc[1]/int[@name='id'][.='" + String.valueOf(n) + "']");
					}
				}
			});
		} finally {
			fis.close();
		}
	}

	@Test
	@Ignore
	public void test_mmseg4j() throws IOException {
		assertTokenizerFactory("textMaxWord", new SimpleSeg(getDictionaryByFieldType("text_zh")));
		//		assertTokenizerFactory("textComplex", new ComplexSeg(getDictionaryByFieldType("text_zh")));
		//		assertTokenizerFactory("textMaxWord", new MaxWordSeg(getDictionaryByFieldType("text_zh")));
	}

}
