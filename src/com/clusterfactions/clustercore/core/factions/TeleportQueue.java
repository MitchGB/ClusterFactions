package com.clusterfactions.clustercore.core.factions;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.listeners.events.player.PlayerCombatTagEvent;
import com.clusterfactions.clustercore.core.listeners.events.updates.UpdateSecondEvent;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.model.Pair;

public class TeleportQueue implements Listener{
	HashMap<UUID, Pair<Long,Location>> playerMap = new HashMap<>();
	
	public TeleportQueue() {
		ClusterCore.getInstance().registerListener(this);
	}
	
	public void scheduleTeleport(Player player, Long duration, Location loc) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		playerData.sendMessage(Lang.TELEPORTING_IN, duration/1000);
		playerMap.put(player.getUniqueId(), new Pair<Long, Location>(System.currentTimeMillis() + duration,loc));
	}
	
	@EventHandler
	public void PlayerCombatTagEvent(PlayerCombatTagEvent e) {
		if(!playerMap.containsKey(e.getPlayer().getUniqueId())) return;
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(e.getPlayer());
		playerData.sendMessage(Lang.TELEPORT_CANCELLED_COMBAT_TAG);
		playerMap.remove(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void UpdateSecondEvent(UpdateSecondEvent e) {
		for(Entry<UUID, Pair<Long, Location>> entry : playerMap.entrySet()) {
			if(System.currentTimeMillis() >= entry.getValue().getLeft())
			{
				Bukkit.getPlayer(entry.getKey()).teleport(entry.getValue().getRight());
				playerMap.remove(entry.getKey());
			}
		}
	}
	
}























