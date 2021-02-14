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
	
	public void createFaction(Player leader, String name, String tag) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(leader);
		if(data.getFaction() != null) {
			data.sendMessage(Lang_EN_US.ALREADY_IN_FACTION);
			return;
		}
		if(tag.contains(" "))
		{
			data.sendMessage(Lang_EN_US.TAG_CANNOT_CONTAIN_SPACE);
			return;
		}
		if(ClusterCore.getInstance().getMongoHook().valueExists("factionTag", tag, "factions"))
		{
			data.sendMessage(Lang_EN_US.FACTION_TAG_TAKEN);
			return;
		}
		
		Faction newFaction = new Faction(leader, name, tag);
		data.setFaction(newFaction.getFactionID());
		data.saveData();
		data.sendMessage(Lang_EN_US.FACTION_CREATED);
	}
	
	public void leave(Player player) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		if(data.getFaction() == null) {
			data.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		data.sendMessage(String.format(Lang_EN_US.LEFT_FACTION, getFaction(data.getFaction()).getFactionName()));
		data.setFaction(null);
		data.saveData();
	}
}
