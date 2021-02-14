package com.clusterfactions.clustercore.core.permission;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.player.PlayerData;

/*
 * Probably should be it's own plugin
 */
public class PlayerPermissionManager {
	public static PermissionGroup defaultGroup = PermissionGroup.DEFAULT;
	
	public static HashMap<UUID,PermissionAttachment> permissionList = new HashMap<>();
	
	public void removePermission(Player player, String perm) {
		permissionList.get(player.getUniqueId()).unsetPermission(perm);
	}
	
	public void setPermission(Player player, String perm) {
		setPermission(player, perm, true);
	}
	
	public void setPermission(Player player, String perm, boolean value) {
		if(!permissionList.containsKey(player.getUniqueId())) return;
		permissionList.get(player.getUniqueId()).setPermission(perm, value);
	}
	
	public void setPlayerGroup(Player player, PermissionGroup group) {
		PlayerData user = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		user.setGroup(group);
		assignPermissions(player);
		user.saveData();
	}
	
	/*
	 * Called on join
	 */
	public void assignPermissions(Player player) {
		PlayerData user = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		if(user.getGroup() == null) user.setGroup(defaultGroup);
		for(String str : user.getGroup().getAllPerms())
		{
			setPermission(player, str);
		}
	}
	
}














