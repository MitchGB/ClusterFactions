package com.clusterfactions.clustercore.persistence.serialization;

import com.clusterfactions.clustercore.core.factions.map.MapColour;

//Base64 Encoder
public class MapColourSerializer extends VariableSerializer<MapColour>{

	@Override
	public String serialize(MapColour obj) {
    	if(obj == null) return null;
    	return obj.toString();
	}

	@Override
	public MapColour deserialize(String str) {
    	if(str==null || str.isEmpty() ) return null;
    	return MapColour.valueOf(str);
	}
    

}