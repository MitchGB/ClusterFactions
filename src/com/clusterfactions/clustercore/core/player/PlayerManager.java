package com.clusterfactions.clustercore.core.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.listeners.events.updates.UpdateSecondEvent;

public class PlayerManager implements Listener{
	HashMap<UUID, PlayerData> cachedPlayers = new HashMap<>();
	
	public PlayerManager() {
		ClusterCore.getInstance().registerListener(this);
	}
	
	@EventHandler
	public void UpdateSecondEvent(UpdateSecondEvent e) {
		for(PlayerData pd : cachedPlayers.values())
			pd.secondUpdater(e);
	}
	
	public PlayerData getPlayerData(UUID playerUUID) {
		if(cachedPlayers.containsKey(playerUUID))
			return cachedPlayers.get(playerUUID);
		
		PlayerData newPlayer = ClusterCore.getInstance().getMongoHook().getObject(playerUUID.toString(), PlayerData.class, "players");
		cachedPlayers.put(playerUUID, newPlayer);
		newPlayer.init(Bukkit.getPlayer(playerUUID));
		return getPlayerData(playerUUID);
		
	}
	
	public void unloadPlayerData(UUID playerUUID) {
		getPlayerData(playerUUID).saveData();
		cachedPlayers.remove(playerUUID);
	}
	
	public PlayerData getPlayerData(Player player) {
		return getPlayerData(player.getUniqueId());
	}
	
}
