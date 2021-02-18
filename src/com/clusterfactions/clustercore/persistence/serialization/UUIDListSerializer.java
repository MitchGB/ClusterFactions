package com.clusterfactions.clustercore.persistence.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//Base64 Encoder
public class UUIDListSerializer extends VariableSerializer<List<UUID>>{


	@Override
	public String serialize(List<UUID> obj) {
	    StringBuilder builder = new StringBuilder();
	    if(obj == null) return "";
	    for(UUID uuid : obj)
	    	builder.append(uuid.toString() + ",");
	    return builder.toString();
	}

	@Override
	public List<UUID> deserialize(String s) {	
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
}

