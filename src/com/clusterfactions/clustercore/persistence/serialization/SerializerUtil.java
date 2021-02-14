package com.clusterfactions.clustercore.persistence.serialization;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class SerializerUtil {
	public static Map<String, Integer> convertWithStream(String mapAsString) {
		Map<String, Integer> map = Arrays.stream(mapAsString.split(","))
		  .map(entry -> entry.replace("}", "").replace("{", "").split("="))
		  .collect(Collectors.toMap(entry -> ((String)entry[0]).trim(), entry -> Integer.parseInt(entry[1])));
		return map != null ? map : null; 
		}
	
}
