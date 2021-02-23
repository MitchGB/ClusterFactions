package com.clusterfactions.clustercore.persistence.serialization;

import java.util.ArrayList;

import com.clusterfactions.clustercore.util.location.Vector2Integer;

//Base64 Encoder
public class Vector2IntegerListSerializer extends VariableSerializer<ArrayList<Vector2Integer>>{

	@Override
	public String serialize(ArrayList<Vector2Integer> obj) {
	    StringBuilder builder = new StringBuilder();
	    if(obj == null) return "";
	    for(Vector2Integer vec : obj)
	    	builder.append(vec.toString() + ",");
	    return builder.toString();
	}

	@Override
	public ArrayList<Vector2Integer> deserialize(String str) {	
		if(str==null || str.isEmpty()) return null;
		ArrayList<Vector2Integer> ints = new ArrayList<>();
		for(String s : str.split(","))
		{
			int x = Integer.parseInt(s.split(":")[0]);
			int z = Integer.parseInt(s.split(":")[1]);
			ints.add(new Vector2Integer(x,z));
		}
		return ints;
	}
}

