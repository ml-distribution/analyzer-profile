package cc.pp.analyzer.profiling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestingData {

	public final static String WEIBO_TEXT = "天黑请闭眼是一款高智商者参与的较量口才和分析判断能力的推理游戏，受到全国很多高校大学生的追捧。 "
			+ "快来加入吧哈哈，这个游戏太爽了，元芳，你怎么看？！跟我一起来吧http://t.cn/zR05TbU";

	public static String bigTestData(String file) throws IOException {

		StringBuffer result = new StringBuffer();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String str = "";
		while ((str = br.readLine()) != null) {
			result.append(str);
		}

		return result.toString();
	}

}
