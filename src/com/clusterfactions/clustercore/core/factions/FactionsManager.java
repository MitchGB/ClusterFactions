package com.clusterfactions.clustercore.core.factions;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;

public class FactionsManager {
	
	private HashMap<UUID, Faction> factionCache = new HashMap<>();

	public static final int disbandTimer = 5000;
	public HashMap<UUID, Long> disbandTimerMap = new HashMap<>();
	
	public Faction getFaction(UUID factionUUID) {		
		if(factionUUID == null) return null;
		if(factionCache.containsKey(factionUUID))
			return factionCache.get(factionUUID);
		
		Faction newFaction = ClusterCore.getInstance().getMongoHook().getObject(factionUUID.toString(), Faction.class, "factions");
		ClusterCore.getInstance().registerListener(newFaction);
		factionCache.put(factionUUID, newFaction);
		return newFaction;
		
	}
	
	public Faction getFaction(String tag) {
		if(factionCache == null) factionCache = new HashMap<>();
		Faction fac = null;
		for(Faction f : factionCache.values())
		{
			if(f != null)
				if(f.getFactionTag() != null)
					if(f.getFactionTag().equalsIgnoreCase(tag))
						fac = f;
		}
		if(fac == null) {
			fac = ClusterCore.getInstance().getMongoHook().getObject(tag.toLowerCase(), "factionLower", Faction.class, "factions");
			factionCache.put(fac.getFactionID(), fac);
		}
		return fac;
	}
	
	public Faction getFaction(Player player) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		return getFaction(playerData.getFaction());
	}
	
	public void createFaction(Player leader, String tag) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(leader);
		Faction newFaction = new Faction(leader, tag);
		data.setFaction(newFaction.getFactionID());
		data.saveData("faction");
		data.sendMessage(Lang.FACTION_CREATED);
		getFaction(newFaction.getFactionID()); // LOAD INTO CACHE
	}
	
	public void setDisbandTimer(Faction faction) {
		disbandTimerMap.put(faction.getFactionID(), System.currentTimeMillis());
	}
	
	public boolean canDisband(Faction faction) {
		if(!disbandTimerMap.containsKey(faction.getFactionID())) return false;
		if(System.currentTimeMillis() - disbandTimerMap.get(faction.getFactionID()) <= disbandTimer) return true;
		return false;
	}

}

















