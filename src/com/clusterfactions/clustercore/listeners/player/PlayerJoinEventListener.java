package com.clusterfactions.clustercore.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.permission.PlayerPermissionManager;
import com.clusterfactions.clustercore.core.player.PlayerData;

public class PlayerJoinEventListener implements Listener{

	@EventHandler
	public void PlayerJoinEvent(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		data.saveData();
		
		
		//PERMISSION MANAGEMENT
		PermissionAttachment attachment = player.addAttachment(ClusterCore.getInstance());
		PlayerPermissionManager.permissionList.put(player.getUniqueId(), attachment);
		ClusterCore.getInstance().getPlayerPermissionManager().assignPermissions(player);
	}
	
}
