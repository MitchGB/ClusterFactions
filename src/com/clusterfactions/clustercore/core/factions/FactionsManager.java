package com.clusterfactions.clustercore.core.factions;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.lang.Lang_EN_US;
import com.clusterfactions.clustercore.core.player.PlayerData;

public class FactionsManager {
	
	private HashMap<UUID, Faction> factionCache = new HashMap<>();
	
	public Faction getFaction(UUID factionUUID) {		
		if(factionCache.containsKey(factionUUID))
			return factionCache.get(factionUUID);
		
		Faction newFaction = ClusterCore.getInstance().getMongoHook().getObject(factionUUID.toString(), Faction.class, "factions");
		ClusterCore.getInstance().registerListener(newFaction);
		factionCache.put(factionUUID, newFaction);
		return newFaction;
		
	}
	
	public Faction getFaction(String tag) {
		//CHANGE THIS TO MONGO SEARCH
		for(Faction f : factionCache.values())
		{
			if(f.getFactionTag().equalsIgnoreCase(tag))
				return f;
		}
		return null;
	}
	
	public void createFaction(Player leader, String name, String tag) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(leader);
		Faction newFaction = new Faction(leader, name, tag);
		data.setFaction(newFaction);
		data.saveData();
		data.sendMessage(Lang_EN_US.FACTION_CREATED);
		getFaction(newFaction.getFactionID()); // LOAD INTO CACHE
	}

}

















