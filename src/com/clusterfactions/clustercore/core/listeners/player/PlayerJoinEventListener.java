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
		
		if(player.getName().contains("Buby") || player.getName().contains("KevinEatsPie101"))			
			player.setResourcePack("http://192.168.250.100/dl/ClusterFactionsResourcePack.zip");
		else
			player.setResourcePack("http://180.150.50.189/dl/ClusterFactionsResourcePack.zip");
			
		
		ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		
		//PERMISSION MANAGEMENT
		PermissionAttachment attachment = player.addAttachment(ClusterCore.getInstance());
		PlayerPermissionManager.permissionList.put(player.getUniqueId(), attachment);
		ClusterCore.getInstance().getPlayerPermissionManager().assignPermissions(player);
	}
	
}
