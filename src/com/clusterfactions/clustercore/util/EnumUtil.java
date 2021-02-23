package com.clusterfactions.clustercore.util;

import java.util.ArrayList;
import java.util.List;

public interface EnumUtil {
	public static <E extends Enum<E>> List<String> getAllList(Class<E> clazz){
		List<String> ret = new ArrayList<>();
		for(Enum<E> t : clazz.getEnumConstants())
		{
			ret.add(t.toString());
		}
		return ret;
	}
}
