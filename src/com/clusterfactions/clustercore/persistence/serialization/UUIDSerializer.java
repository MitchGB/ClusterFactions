package com.clusterfactions.clustercore.persistence.serialization;

import java.util.UUID;

//Base64 Encoder
public class UUIDSerializer extends VariableSerializer{
  private String getStringUUID(final UUID l) {
	    return l.toString();
  }

	
  private UUID getUUIDString(final String s) {
  	return UUID.fromString(s);
  }
	
	@Override
	public String serialize(Object obj) {
		if(!(obj instanceof UUID)) return null;
		return getStringUUID((UUID)obj);
	}

	@Override
	public Object deserialize(String str) {	
		return getUUIDString(str);
	}
  

}