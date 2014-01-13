package cc.pp.nlp.keyword.demo;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtils {

	private static final ObjectMapper mapper = new ObjectMapper();

	private static DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

	static {
		mapper.setDateFormat(dateFormat);
	}

	public static ObjectMapper getObjectMapper() {
		return mapper;
	}

	/**
	 *  测试函数
	 */
	public static void main(String[] args) throws ParseException {

		//		System.out.println(dateFormat.format(new Date()));
		//		Date parse = dateFormat.parse("Wed Oct 23 16:58:17 +0800 2013");
		//		System.out.println(parse);

		HashMap<String, Integer> result = new HashMap<>();
		result.put("aaa", 1111);
		result.put("bbb", 2222);
		result.put("ccc", 3333);
		System.out.println(JsonUtils.toJson(result));
	}

	public static String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
