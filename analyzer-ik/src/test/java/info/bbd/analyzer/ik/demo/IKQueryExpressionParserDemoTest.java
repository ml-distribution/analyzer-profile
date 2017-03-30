package info.bbd.analyzer.ik.demo;

import static org.junit.Assert.assertEquals;

import org.apache.lucene.search.Query;
import org.junit.Test;

import info.bbd.analyzer.ik.query.IKQueryExpressionParser;

public class IKQueryExpressionParserDemoTest {

	@Test
	public void testQueryExpressionParser() {
		IKQueryExpressionParser parser = new IKQueryExpressionParser();
		String ikQueryExp = "(id='ABcdRf' && date:{'20010101','20110101'} && keyword:'魔兽中国') || (content:'KSHT-KSH-A001-18'  || ulr='www.ik.com') - name:'分词器'";
		Query result = parser.parseExp(ikQueryExp, true);
		String parserResult = "(+id:ABcdRf +date:{20010101 TO 20110101} +keyword:\"魔 兽\" +keyword:\"中 国\") content:\"ksht ksh a001 18\" ulr:www.ik.com -name:\"分 词 器\"";
		assertEquals(parserResult, result.toString());
	}

}
