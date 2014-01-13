package cc.pp.nlp.keyword.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cc.pp.nlp.keyword.CWSTagger;
import cc.pp.nlp.keyword.Extractor;
import cc.pp.nlp.keyword.POSTagger;
import cc.pp.nlp.keyword.StopWords;
import cc.pp.nlp.keyword.WordExtract;


public class KeyWordDemo {

	/*********初始化***********/
	private final StopWords sw;
	private final CWSTagger seg;
	private final POSTagger pos;
	private final Extractor key;

	/**
	 * @author WG
	 * @param str:第一个变量是字符串
	 * @param wordcharacter:第二个参数是需要保留的词性（词性之间用半角空格隔开）
	 * 主要有：时间短语, 介词, 标点, 形容词, 人称代词, 拟声词, 地名, 省略词, 语气词, 指示代词,
	 *       叹词, 表情符, 网址, 从属连词, 机构名, 型号名, 事件名, 副词, 序数词, 把动词, 方位词,
	 *       名词, 形谓词, 能愿动词, 结构助词, 品牌名, 趋向动词, 数词, 时态词, 被动词, 限定词,
	 *       并列连词, 人名, 量词, 动词, 品牌, 疑问代词, 实体名, 结构词
	 * 地名 省略词 机构名 事件名 名词 品牌名 人名 品牌 实体名--------------名词性质  名词
	 * 把动词 形谓词 能愿动词 趋向动词 时态词 被动词 动词----------------动词性质 动词
	 * 人称代词 形容词 副词 方位词 结构助词 数词 限定词 量词 疑问代词 结构词---形容词等修饰成分 形容词
	 * @param keywordnum:第三个变量是要抽取的关键词数
	 * @throws Exception
	 */
	public KeyWordDemo() throws Exception {
		sw = new StopWords("models/stopwords");
		seg = new CWSTagger("models/seg.m");
		pos = new POSTagger(seg,"models/pos.m");
		key = new WordExtract(seg,pos,sw);
	}

	/**
	 * @return Json数据结果
	 * @return
	 * @throws IOException
	 */
	public String extractKeyword(String str, String wordcharacter, int keywordnum) throws IOException
	{
		String result = null;
		LinkedHashMap<String,Integer> temp = new LinkedHashMap<String,Integer>();
		if ((str == "") || (keywordnum == 0)) {
			temp = null;
		} else if (wordcharacter == "") {
			temp =  (LinkedHashMap<String, Integer>) key.extract(str, keywordnum);
     	} else {
			temp =  (LinkedHashMap<String, Integer>) key.extract(str, wordcharacter, keywordnum);
		}
		if (temp != null) {
			HashMap<String,String> keyword = new HashMap<String,String>();
			int i = 0;
			for (String key : temp.keySet()) {
				keyword.put(Integer.toString(i++), key);
			}
			result = JsonUtils.toJson(keyword);
		}

		return result;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		//第一个参数是字符串 ，第三个参数是需要抽取多少个关键词
		//		long time1 = System.currentTimeMillis();
		KeyWordDemo mykeyword = new KeyWordDemo();
		//		long time2 = System.currentTimeMillis();
		//		System.out.println(mykeyword.extractKeyword("朝鲜科技工作者深入进行分析今年4月发射卫星时出现的问题、改进卫星和运载火箭安全性和精确度的工作，"
		//				+ "完成了发射卫星的准备。朝鲜科技工作者深入进行分析今年4月发射卫星时出现的问题、改进卫星和运载火箭安全性和精确度的工作，完成了发射卫星的准备。"
		//				+ "朝鲜科技工作者深入进行分析今年4月发射卫星时出现的问题、改进卫星和运载火箭安全性和精确度的工作，完成了发射卫星的准备。朝鲜科技工作者深入进行"
		//				+ "分析今年4月发射卫星时出现的问题、改进卫星和运载火箭安全性和精确度的工作，完成了发射卫星的准备。朝鲜科技工作者深入进行分析今年4月发射卫星时"
		//				+ "出现的问题、改进卫星和运载火箭安全性和精确度的工作，完成了发射卫星的准备。朝鲜科技工作者深入进行分析今年4月发射卫星时出现的问题、改进卫星和"
		//				+ "运载火箭安全性和精确度的工作，完成了发射卫星的准备。朝鲜科技工作者深入进行分析今年4月发射卫星时出现的问题、改进卫星和运载火箭安全性和精确度"
		//				+ "的工作，完成了发射卫星的准备。朝鲜科技工作者深入进行分析今年4月发射卫星时出现的问题、改进卫星和运载火箭安全性和精确度的工作，完成了发射卫"
		//				+ "星的准备。朝鲜科技工作者深入进行分析今年4月发射卫星时出现的问题、改进卫星和运载火箭安全性和精确度的工作，完成了发射卫星的准备。朝鲜科技工"
		//				+ "作者深入进行分析今年4月发射卫星时出现的问题、改进卫星和运载火箭安全性和精确度的工作，完成了发射卫星的准备。朝鲜科技工作者深入进行分析今年4"
		//				+ "月发射卫星时出现的问题、改进卫星和运载火箭安全性和精确度的工作，完成了发射卫星的准备。朝鲜科技工作者深入进行分析今年4月发射卫星时出现的问"
		//				+ "题、改进卫星和运载火箭安全性和精确度的工作，完成了发射卫星的准备。朝鲜科技工作者深入进行分析今年4月发射卫星时出现的问题、改进卫星和运载火箭" + "安全性和精确度的工作，完成了发射卫星的准备。",
		//				"地名 省略词 机构名 事件名 名词 品牌名 人名 品牌 实体名", 50));
		//		long time3 = System.currentTimeMillis();
		//		System.out.println(mykeyword.extractKeyword("中国计算机学会合肥分部（简称合肥分部）将于上午在合肥工业大学（屯溪路校区学术报告中心一楼大报告厅）"
		//				+ "成立，邀请您届时参加成立大会和首次CCF分部会员学术活动，大会免费提供会后简餐。为加强CCF对会员的服务，方便会员就近参加CCF活动，促进会员间的交"
		//				+ "流和合作，CCF创建以城市为单位的CCF会员活动中心，称作CCF[城市名]会员活动中心，简称CCF[城市名]分部。", "", 20));
		//		long time4 = System.currentTimeMillis();
		//		System.out.println(time2 - time1);
		//		System.out.println(time3 - time2);
		//		System.out.println(time4 - time3);
		System.out.println(mykeyword.extractKeyword("我在淘宝网的聚划算上买了一件衣服。", "", 20));
	}

}

