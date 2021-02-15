package com.clusterfactions.clustercore.core.chat;

import java.util.ArrayList;
import java.util.List;

public enum ChatMessageMode {
	GLOBAL,
	FACTION,
	ALLY;
	
	public static List<String> getAllList(){
		List<String> ret = new ArrayList<>();
		for(ChatMessageMode t : ChatMessageMode.values())
			ret.add(t.toString());
		return ret;
	}
}
