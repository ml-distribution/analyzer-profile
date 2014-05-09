package cc.pp.analyzer.fudan.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 复旦关键词提取器主类
 * @author wanggang
 *
 */
public class WordExtractFudan {

	private static Extractor key;

	/**
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
	 */
	public WordExtractFudan() {
		try {
			CWSTagger seg = new CWSTagger("models/seg.m");
			key = new WordExtract(seg, new POSTagger(seg, "models/pos.m"), new StopWords("models/stopwords"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return 关键词集和Json数据结果
	 */
	public Map<String, Integer> extractMap(String str, String wordCharacter, int keywordNum) {
		Map<String, Integer> result = new HashMap<>();
		if ((str == "") || (keywordNum == 0)) {
			result = null;
		} else if (wordCharacter == "") {
			result = key.extract(str, keywordNum);
		} else {
			result = key.extract(str, wordCharacter, keywordNum);
		}
		return result;
	}

	public List<String> extractList(String str, String wordCharacter, int keywordNum) {
		Map<String, Integer> result = new HashMap<>();
		if ((str == "") || (keywordNum == 0)) {
			result = null;
		} else if (wordCharacter == "") {
			result = key.extract(str, keywordNum);
		} else {
			result = key.extract(str, wordCharacter, keywordNum);
		}
		if (result != null) {
			List<String> keyword = new ArrayList<>();
			for (String key : result.keySet()) {
				keyword.add(key);
			}
			return keyword;
		} else {
			return null;
		}
	}

}
