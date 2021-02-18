package com.clusterfactions.clustercore.persistence.serialization;

import com.clusterfactions.clustercore.util.location.Vector2Integer;

//Base64 Encoder
public class Vector2IntegerSerializer extends VariableSerializer<Vector2Integer>{

	@Override
	public String serialize(Vector2Integer obj) {
		return obj.toString();
	}

	@Override
	public Vector2Integer deserialize(String str) {			
		if(str==null || str.isEmpty()) return null;
		int x = Integer.parseInt(str.split(":")[0]);
		int z = Integer.parseInt(str.split(":")[1]);
		return new Vector2Integer(x,z);
	}
}

