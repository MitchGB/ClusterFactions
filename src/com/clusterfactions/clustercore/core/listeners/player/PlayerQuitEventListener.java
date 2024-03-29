package com.clusterfactions.clustercore.core.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.player.PlayerData;

public class PlayerQuitEventListener implements Listener{

	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		ClusterCore.getInstance().getPlayerManager().unloadPlayerData(data.getPlayerUUID());
		data.saveData();
		
		if(ClusterCore.getInstance().getCombatManager().isTagged(player))
			player.setHealth(0);
		
		ClusterCore.getInstance().getFactionMapGeneratorManager().removeMap(player);
	}
	
}
