package com.clusterfactions.clustercore.persistence.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//Base64 Encoder
public class UUIDListSerializer extends VariableSerializer{
	private String getStringUUIDList(final List<UUID> l) {
	    StringBuilder builder = new StringBuilder();
	    for(UUID uuid : l)
	    	builder.append(uuid.toString() + ",");
	    return builder.toString().trim().substring(0,builder.toString().length());
	}


	private List<UUID> getUUIDListString(final String s) {
		if(s==null || s.isEmpty()) return null;
		String[] array = s.split(",");
		List<UUID> uuids = new ArrayList<>();
		for(String str : array) {
			str = str.replace(",", "");
			if(str.isEmpty()) continue;
			uuids.add(UUID.fromString(str));
		}
		return uuids;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String serialize(Object obj) {
		if(!(obj instanceof List<?>)) return "";
		return getStringUUIDList((List<UUID>)obj);
	}

	@Override
	public Object deserialize(String str) {	
		return getUUIDListString(str);
	}
}

