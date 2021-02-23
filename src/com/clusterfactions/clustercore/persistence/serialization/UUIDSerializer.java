package com.clusterfactions.clustercore.persistence.serialization;

import java.util.UUID;

//Base64 Encoder
public class UUIDSerializer extends VariableSerializer<UUID>{
	
	@Override
	public String serialize(UUID obj) {
		if(obj == null) return "";
		return obj.toString();
	}

	@Override
	public UUID deserialize(String str) {	
		try {  	
			return UUID.fromString(str);
		}catch(Exception e) {
			return null;
		}
	}
  

}