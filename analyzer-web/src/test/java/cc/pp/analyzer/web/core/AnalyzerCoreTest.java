package cc.pp.analyzer.web.core;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import zx.soft.utils.json.JsonUtils;

public class AnalyzerCoreTest {

	private static AnalyzerCore analyzerCore;

	@BeforeClass
	public static void doInit() {
		analyzerCore = new AnalyzerCore();
	}

	@AfterClass
	public static void doClose() {
		analyzerCore.close();
	}

	@Test
	public void testGetMMSeg4jSegWords() {
		assertEquals(
				"{\"count\":11,\"words\":[\"这是\",\"需要\",\"分词\",\"的\",\"语句\",\"淘\",\"宝\",\"特卖\",\"中华\",\"名\",\"国\"]}",
				JsonUtils.toJsonWithoutPretty(analyzerCore.getMMSeg4jSegWords("这是需要分词的语句，淘宝特卖，中华名国", 0)));
		assertEquals(
				"{\"count\":11,\"words\":[\"这是\",\"需要\",\"分词\",\"的\",\"语句\",\"淘\",\"宝\",\"特卖\",\"中华\",\"名\",\"国\"]}",
				JsonUtils.toJsonWithoutPretty(analyzerCore.getMMSeg4jSegWords("这是需要分词的语句，淘宝特卖，中华名国", 1)));
		assertEquals(
				"{\"count\":11,\"words\":[\"这是\",\"需要\",\"分词\",\"的\",\"语句\",\"淘\",\"宝\",\"特卖\",\"中华\",\"名\",\"国\"]}",
				JsonUtils.toJsonWithoutPretty(analyzerCore.getMMSeg4jSegWords("这是需要分词的语句，淘宝特卖，中华名国", 2)));
	}

	@Test
	public void testGetIKSegWords() {
		assertEquals("{\"count\":9,\"words\":[\"这是\",\"需要\",\"分词\",\"语句\",\"淘宝\",\"特卖\",\"中华\",\"名\",\"国\"]}",
				JsonUtils.toJsonWithoutPretty(analyzerCore.getIKSegWords("这是需要分词的语句，淘宝特卖，中华名国", 0)));
		assertEquals("{\"count\":9,\"words\":[\"这是\",\"需要\",\"分词\",\"语句\",\"淘宝\",\"特卖\",\"中华\",\"名\",\"国\"]}",
				JsonUtils.toJsonWithoutPretty(analyzerCore.getIKSegWords("这是需要分词的语句，淘宝特卖，中华名国", 1)));
	}

	@Test
	public void testGetFudanKeywords() {
		//		assertEquals("{\"count\":6,\"words\":[\"分词\",\"名国\",\"中华\",\"淘宝\",\"特卖\",\"语句\"]}",
		//				JsonUtils.toJsonWithoutPretty(analyzerCore.getFudanKeywords("这是需要分词的语句，淘宝特卖，中华名国", 1_000)));
	}

}
