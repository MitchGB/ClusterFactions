package com.clusterfactions.clustercore.core.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

public class PlayerResourcePackStatusEventListener implements Listener{
	
	@EventHandler
	public void PlayerResourcePackStatusEvent(PlayerResourcePackStatusEvent e) {
		Player player = e.getPlayer();
		if(e.getStatus() == Status.FAILED_DOWNLOAD || e.getStatus() == Status.DECLINED) {
			player.kickPlayer("Cluster Factions requires the resource pack to play.");
		}
	}
}