package com.clusterfactions.clustercore.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.player.PlayerData;

public class AsyncPlayerChatEventListener implements Listener{

	@EventHandler
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		if(data.getGroup() != null) e.setFormat(data.getGroup().getGroupPrefix() + " " + player.getName() + " %2$s");
	}
	
}
