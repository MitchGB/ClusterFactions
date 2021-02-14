package com.clusterfactions.clustercore.core.player;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.permission.PermissionGroup;
import com.clusterfactions.clustercore.persistence.serialization.PermissionGroupSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDSerializer;
import com.clusterfactions.clustercore.util.annotation.AlternateSerializable;
import com.clusterfactions.clustercore.util.annotation.DoNotSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@NoArgsConstructor
public class PlayerData implements Listener{
	
	@Getter @DoNotSerialize Player player;
	@Getter @Setter private int power;
	
	@Getter @Setter @AlternateSerializable(PermissionGroupSerializer.class) private PermissionGroup group = PermissionGroup.DEFAULT;
	@Getter @Setter @AlternateSerializable(UUIDSerializer.class) private UUID faction;
	
	public void saveData() {
		ClusterCore.getInstance().getMongoHook().saveData(player.getUniqueId().toString(), this, "players");
	}
	
	public void sendMessage(String str) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
	}
	
	/*
	 * Called each time player joins 
	 */
	public void init(Player player) {
		this.player = player;
	}
}
