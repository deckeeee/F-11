package org.F11.scada.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.F11.scada.EnvironmentManager;

public class AlarmTableTitleUtil {
	private final Map<String, String> map;

	public AlarmTableTitleUtil() {
		this(getTitle());
	}

	private static String getTitle() {
		return EnvironmentManager.get("/server/alarm/attribute/title", "‘®«1, ‘®«2, ‘®«3");
	}

	public AlarmTableTitleUtil(String title) {
		map = new LinkedHashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(title, ",");
		for (int i = 1; tokenizer.hasMoreTokens() && i <= 3; i++) {
			String titleItem = tokenizer.nextToken().trim();
			map.put("‘®«" + i, titleItem);
		}
		System.out.println(map);
	}

	public String repraceStrings(String src) {
		String result = src;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			result = result.replaceFirst(key, value);
		}
		return result;
	}

	public String getAttributeString(String key) {
		return map.get(key);
	}
}
