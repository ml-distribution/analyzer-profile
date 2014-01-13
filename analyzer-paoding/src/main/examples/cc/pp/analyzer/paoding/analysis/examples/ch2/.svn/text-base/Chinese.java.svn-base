package net.paoding.analysis.examples.gettingstarted.ch2;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import net.paoding.analysis.examples.gettingstarted.BoldFormatter;
import net.paoding.analysis.examples.gettingstarted.ContentReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class Chinese {
	private static String FIELD_NAME = "content";
	private static String QUERY = "共和国";

	public static void main(String[] args) throws Exception {
		if (args.length != 0) {
			QUERY = args[0];
		}
		// 将庖丁封装成符合Lucene要求的Analyzer规范
		Analyzer analyzer = new PaodingAnalyzer();
		
		//读取本类目录下的text.txt文件
		String content = ContentReader.readText(Chinese.class);

		//接下来是标准的Lucene建立索引和检索的代码
		Directory ramDir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(ramDir, analyzer);
		Document doc = new Document();
		Field fd = new Field(FIELD_NAME, content, Field.Store.YES,
				Field.Index.TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
		doc.add(fd);
		writer.addDocument(doc);
		writer.optimize();
		writer.close();

		IndexReader reader = IndexReader.open(ramDir);
		String queryString = QUERY;
		QueryParser parser = new QueryParser(FIELD_NAME, analyzer);
		Query query = parser.parse(queryString);
		Searcher searcher = new IndexSearcher(ramDir);
		query = query.rewrite(reader);
		System.out.println("Searching for: " + query.toString(FIELD_NAME));
		Hits hits = searcher.search(query);

		BoldFormatter formatter = new BoldFormatter();
		Highlighter highlighter = new Highlighter(formatter, new QueryScorer(
				query));
		highlighter.setTextFragmenter(new SimpleFragmenter(50));
		for (int i = 0; i < hits.length(); i++) {
			String text = hits.doc(i).get(FIELD_NAME);
			int maxNumFragmentsRequired = 5;
			String fragmentSeparator = "...";
			TermPositionVector tpv = (TermPositionVector) reader
					.getTermFreqVector(hits.id(i), FIELD_NAME);
			TokenStream tokenStream = TokenSources.getTokenStream(tpv);
			String result = highlighter.getBestFragments(tokenStream, text,
					maxNumFragmentsRequired, fragmentSeparator);
			System.out.println("\n" + result);
		}
		reader.close();
	}
}
