package com.clusterfactions.clustercore.persistence.serialization;

import java.util.UUID;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;

//Base64 Encoder
public class FactionSerializer extends VariableSerializer<Faction>{

	@Override
	public String serialize(Faction obj) {
    	if(obj == null) return null;
    	return obj.getFactionID().toString();
	}

	@Override
	public Faction deserialize(String str) {
    	if(str==null || str.isEmpty() ) return null;
    	return ClusterCore.getInstance().getFactionsManager().getFaction(UUID.fromString(str));
	}
    

}