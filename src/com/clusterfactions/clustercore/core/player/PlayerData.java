package com.clusterfactions.clustercore.core.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.chat.ChatMessageMode;
import com.clusterfactions.clustercore.core.permission.PermissionGroup;
import com.clusterfactions.clustercore.listeners.events.updates.UpdateSecondEvent;
import com.clusterfactions.clustercore.persistence.serialization.PermissionGroupSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDSerializer;
import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.annotation.AlternateSerializable;
import com.clusterfactions.clustercore.util.annotation.DoNotSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@NoArgsConstructor
public class PlayerData{
	
	@Getter @DoNotSerialize UUID playerUUID;
	@Getter @Setter private int power;
	
	@Getter @Setter @AlternateSerializable(PermissionGroupSerializer.class) private PermissionGroup group = PermissionGroup.MEMBER;
	@Getter @Setter @AlternateSerializable(UUIDSerializer.class) private UUID faction;
	
	@Getter @Setter @DoNotSerialize ChatMessageMode chatMode = ChatMessageMode.GLOBAL;
	
	public void saveData() {
		ClusterCore.getInstance().getMongoHook().saveData(playerUUID.toString(), this, "players");
	}
	
	public void sendMessage(String str, Object... args) {
		getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(str, args)));
	}
	
	/*
	 * Called each time player joins 
	 */
	public void init(Player player) {
		this.playerUUID = player.getUniqueId();
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(playerUUID);
	}
	
	public boolean isInFaction() {
		return faction == null;
	}
	
	public void secondUpdater(UpdateSecondEvent e) {
		setDisplayPrefix();
	}
	
	public boolean isCombatTagged() {
		return ClusterCore.getInstance().getCombatManager().isTagged(getPlayer());
	}
	
	private void setDisplayPrefix() {
		try {
			if(!Bukkit.getOnlinePlayers().contains(getPlayer())) return;
			Scoreboard scoreboard = ClusterCore.getInstance().getServer().getScoreboardManager().getMainScoreboard();
			Team team = scoreboard.getTeam(getPlayer().getName());
		
			if(team == null)
				team = scoreboard.registerNewTeam(getPlayer().getName());
        
			team.setPrefix(Colors.parseColors(getGroup().getGroupPrefix() != null ? getGroup().getGroupPrefix() + " ": ""));
			team.addEntry(getPlayer().getName());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
