package com.clusterfactions.clustercore.persistence.serialization;

import java.util.UUID;

//Base64 Encoder
public class UUIDSerializer extends VariableSerializer<UUID>{
  private String getStringUUID(final UUID l) {
	    return l.toString();
  }

	
  private UUID getUUIDString(final String s) {
  	return UUID.fromString(s);
  }
	
	@Override
	public String serialize(UUID obj) {
		if(!(obj instanceof UUID)) return null;
		return getStringUUID((UUID)obj);
	}

	@Override
	public UUID deserialize(String str) {	
		try {
			return getUUIDString(str);
		}catch(Exception e) {
			return null;
		}
	}
  

}