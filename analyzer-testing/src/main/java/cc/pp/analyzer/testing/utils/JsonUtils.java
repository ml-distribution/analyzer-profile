package cc.pp.analyzer.testing.utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtils {

	private static final ObjectMapper mapper = new ObjectMapper();

	public static ObjectMapper getObjectMapper() {
		return mapper;
	}

	/**
	 *  测试函数
	 */
	public static void main(String[] args) throws ParseException {

		HashMap<String, Integer> result = new HashMap<>();
		result.put("aaa", 1111);
		result.put("bbb", 2222);
		result.put("ccc", 3333);
		System.out.println(JsonUtils.toJson(result));
	}

	public static String toJson(Object object) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toJsonWithoutPretty(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
