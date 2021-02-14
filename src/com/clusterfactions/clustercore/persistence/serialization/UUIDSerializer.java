package com.clusterfactions.clustercore.persistence.serialization;

import java.util.UUID;

//Base64 Encoder
public class UUIDSerializer extends VariableSerializer{
  private String getStringLocation(final UUID l) {
	    return l.toString();
  }

	
  private UUID getLocationString(final String s) {
  	return UUID.fromString(s);
  }
	
	@Override
	public String serialize(Object obj) {
		if(!(obj instanceof UUID)) return null;
		return getStringLocation((UUID)obj);
	}

	@Override
	public Object deserialize(String str) {	
		return getLocationString(str);
	}
  

}