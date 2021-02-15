package com.clusterfactions.clustercore.persistence.serialization;

import java.util.UUID;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;

//Base64 Encoder
public class FactionSerializer extends VariableSerializer{
    private String getStringFaction(final Faction l) {
    	if(l == null) return null;
    	return l.getFactionID().toString();
    }
 
	
    private Faction getFactionString(final String s) {
    	if(s==null || s.isEmpty() ) return null;
    	return ClusterCore.getInstance().getFactionsManager().getFaction(UUID.fromString(s));
    }
	
	@Override
	public String serialize(Object obj) {
		if(!(obj instanceof Faction)) return null;
		return getStringFaction((Faction)obj);
	}

	@Override
	public Object deserialize(String str) {
		return getFactionString(str);
	}
    

}