package com.clusterfactions.clustercore.persistence.serialization;

import com.clusterfactions.clustercore.util.location.Vector2Integer;

//Base64 Encoder
public class Vector2IntegerSerializer extends VariableSerializer{
	private String getStringVector2IntegerList(final Vector2Integer l) {
	    return l.toString();
	}


	private Vector2Integer getStringVector2IntegerList(final String s) {
		if(s==null || s.isEmpty()) return null;
		int x = Integer.parseInt(s.split(":")[0]);
		int z = Integer.parseInt(s.split(":")[1]);
		 return new Vector2Integer(x,z);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String serialize(Object obj) {
		if(!(obj instanceof Vector2Integer)) return "";
		return getStringVector2IntegerList((Vector2Integer)obj);
	}

	@Override
	public Object deserialize(String str) {	
		return getStringVector2IntegerList(str);
	}
}

