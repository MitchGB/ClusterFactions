package com.clusterfactions.clustercore.persistence.serialization;

import java.util.ArrayList;
import java.util.List;

import com.clusterfactions.clustercore.util.location.Vector2Integer;

//Base64 Encoder
public class Vector2IntegerListSerializer extends VariableSerializer<ArrayList<Vector2Integer>>{
	private String getStringVector2IntegerList(final ArrayList<Vector2Integer> l) {
		
	    StringBuilder builder = new StringBuilder();
	    for(Vector2Integer vec : l)
	    	builder.append(vec.toString() + ",");
	    return builder.toString().trim().substring(0,builder.toString().length());
	}


	private ArrayList<Vector2Integer> getStringVector2IntegerList(final String s) {
		if(s==null || s.isEmpty()) return null;
		ArrayList<Vector2Integer> ints = new ArrayList<>();
		for(String str : s.split(","))
		{
			int x = Integer.parseInt(str.split(":")[0]);
			int z = Integer.parseInt(str.split(":")[1]);
			ints.add(new Vector2Integer(x,z));
		}
		return ints;
	}

	@Override
	public String serialize(ArrayList<Vector2Integer> obj) {
		if(!(obj instanceof List<?>)) return "";
		return getStringVector2IntegerList((ArrayList<Vector2Integer>)obj);
	}

	@Override
	public ArrayList<Vector2Integer> deserialize(String str) {	
		return getStringVector2IntegerList(str);
	}
}

