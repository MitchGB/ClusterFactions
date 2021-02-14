package com.clusterfactions.clustercore.core.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;

public class PlayerManager {
	HashMap<UUID, PlayerData> cachedPlayers = new HashMap<>();
	
	public PlayerData getPlayerData(UUID playerUUID) {
		if(cachedPlayers.containsKey(playerUUID))
			return cachedPlayers.get(playerUUID);
		
		PlayerData newPlayer = ClusterCore.getInstance().getMongoHook().getObject(playerUUID.toString(), PlayerData.class, "players");
		ClusterCore.getInstance().registerListener(newPlayer);
		cachedPlayers.put(playerUUID, newPlayer);
		newPlayer.init(Bukkit.getPlayer(playerUUID));
		return getPlayerData(playerUUID);
		
	}
	
	public PlayerData getPlayerData(Player player) {
		return getPlayerData(player.getUniqueId());
	}
	
}
