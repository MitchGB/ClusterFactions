package com.clusterfactions.clustercore.core.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.permission.PlayerPermissionManager;

public class PlayerJoinEventListener implements Listener{

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		player.sendTitle(" ", " ", 0, 1, 0);
		
		ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		
		//PERMISSION MANAGEMENT
		PermissionAttachment attachment = player.addAttachment(ClusterCore.getInstance());
		PlayerPermissionManager.permissionList.put(player.getUniqueId(), attachment);
		ClusterCore.getInstance().getPlayerPermissionManager().assignPermissions(player);
	}
	
}
