package com.clusterfactions.clustercore.core.items;

import java.util.ArrayList;
import java.util.List;

public enum CustomItemType {

	TEST_ITEM;

	public static List<String> getAllList(){
		List<String> ret = new ArrayList<>();
		for(CustomItemType t : CustomItemType.values())
			ret.add(t.toString());
		return ret;
	}
	
	public static CustomItemType getById(String id)
	{
		return valueOf(id.toUpperCase().replace("-", "_"));
	}
	
	public String getId()
	{
		return name().toUpperCase();
	}	
	
}
