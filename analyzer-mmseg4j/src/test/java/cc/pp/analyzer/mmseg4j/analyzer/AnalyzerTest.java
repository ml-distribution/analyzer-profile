package cc.pp.analyzer.mmseg4j.analyzer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Before;
import org.junit.Ignore;

@SuppressWarnings("deprecation")
public class AnalyzerTest {

	String txt = "";

	@Before
	public void before() throws Exception {
		txt = "京华时报２００９年1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。";
		txt = "２００９年ゥスぁま是中 ＡＢｃｃ国абвгαβγδ首次,我的ⅠⅡⅢ在chenёlbēū全国ㄦ范围ㄚㄞㄢ内①ē②㈠㈩⒈⒑发行地方政府债券，";
	}

	@Ignore
	public void testSimple() {

		SimpleAnalyzer analyzer = new SimpleAnalyzer();
		//ēū
		//txt = "２００９年ゥスぁま是中ＡＢｃｃ国абвгαβγδ首次,我的ⅠⅡⅢ在chenёlbēū全国ㄦ范围ㄚㄞㄢ内①②㈠㈩⒈⒑发行地方政府债券，";
		try {
			printlnToken(txt, analyzer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	public void testComplex() {

		Analyzer analyzer = new ComplexAnalyzer();
		try {
			//txt = "1884年,中法战争时被派福建会办海疆事务";
			//txt = "1999年12345日报道了一条新闻,2000年中法国足球比赛";
			/*txt = "第一卷 云天落日圆 第一节 偷欢不成倒大霉";
			txt = "中国人民银行";
			txt = "我们";
			*/
			txt = "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作";
			//ComplexSeg.setShowChunk(true);
			printlnToken(txt, analyzer);
			//			txt = "核心提示：3月13日上午，近3000名全国人大代表按下表决器，高票批准了温家宝总理代表国务院所作的政府工作报告。这份工作报告起草历时3个月，由温家宝总理亲自主持。";
			printlnToken(txt, analyzer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 不能通过maven测试
	 */
	@Ignore
	public void testMaxWord() {

		Analyzer analyzer = new MaxWordAnalyzer();
		try {
			//txt = "1884年,中法战争时被派福建会办海疆事务";
			//txt = "1999年12345日报道了一条新闻,2000年中法国足球比赛";
			//txt = "第一卷 云天落日圆 第一节 偷欢不成倒大霉";
			//txt = "中国人民银行";
			//txt = "下一个 为什么";
			//txt = "我们家门前的大水沟很难过";
			//ComplexSeg.setShowChunk(true);
			printlnToken(txt, analyzer);
			//txt = "核心提示：3月13日上午，近3000名全国人大代表按下表决器，高票批准了温家宝总理代表国务院所作的政府工作报告。这份工作报告起草历时3个月，由温家宝总理亲自主持。";
			//printlnToken(txt, analyzer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试不通过
	 * @throws IOException
	 */
	@Ignore
	public void testCutLeeterDigitFilter() throws IOException {

		String myTxt = "mb991ch cq40-519tx mmseg4j ";
		List<String> words = toWords(myTxt, new MMSegAnalyzer("") {
			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				Tokenizer t = new MMSegTokenizer(newSeg());
				return new TokenStreamComponents(t, new CutLetterDigitFilter(t));
			}
		});
		System.out.println(words.toString());
		assertEquals("[mb, 991, ch, cq, 40, 519, tx, mmseg, 4, j]", words.toString());
		//		Assert.assertArrayEquals("CutLeeterDigitFilter fail", words.toArray(new String[words.size()]), "mb 991 ch cq 40 519 tx mmseg 4 j".split(" "));
	}

	private void printlnToken(String txt, Analyzer analyzer) throws IOException {

		System.out.println("---------" + txt.length() + "\n" + txt);
		TokenStream ts = analyzer.tokenStream("text", new StringReader(txt));
		/*//lucene 2.9 以下
		for(Token t= new Token(); (t=ts.next(t)) !=null;) {
			System.out.println(t);
		}*/
		/*while(ts.incrementToken()) {
			TermAttribute termAtt = (TermAttribute)ts.getAttribute(TermAttribute.class);
			OffsetAttribute offsetAtt = (OffsetAttribute)ts.getAttribute(OffsetAttribute.class);
			TypeAttribute typeAtt = (TypeAttribute)ts.getAttribute(TypeAttribute.class);

			System.out.println("("+termAtt.term()+","+offsetAtt.startOffset()+","+offsetAtt.endOffset()+",type="+typeAtt.type()+")");
		}*/
		for (Token t = new Token(); (t = TokenUtils.nextToken(ts, t)) != null;) {
			System.out.println(t);
		}
	}

	public static List<String> toWords(String txt, Analyzer analyzer) throws IOException {

		List<String> words = new ArrayList<String>();
		TokenStream ts = analyzer.tokenStream("text", new StringReader(txt));
		CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);
		try {
			ts.reset();
			while (ts.incrementToken()) {
				words.add(charTermAttribute.toString());
			}
			ts.end();
		} finally {
			ts.close();
			analyzer.close();
		}

		return words;
	}

}
