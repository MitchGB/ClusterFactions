package com.clusterfactions.clustercore.core.factions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.util.FactionPlayerRemoveReason;
import com.clusterfactions.clustercore.core.lang.Lang_EN_US;
import com.clusterfactions.clustercore.persistence.serialization.UUIDListSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDSerializer;
import com.clusterfactions.clustercore.util.annotation.AlternateSerializable;
import com.clusterfactions.clustercore.util.annotation.DoNotSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@NoArgsConstructor
public class Faction implements Listener{

	@Getter @Setter private String factionName;
	@Getter @Setter private String factionTag;
	
	@Getter @AlternateSerializable(UUIDSerializer.class) private UUID factionOwner;
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> players;
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> moderators;
	//CLAIMED CHUNKS
	@Getter @DoNotSerialize private UUID factionID;
	
	/*
	 * CALLED WHEN A NEW FACTION IS MADE
	*/
	public Faction(Player owner, String name, String tag) {
		this.factionOwner = owner.getUniqueId();
		this.factionName = name;
		this.factionTag = tag;
		this.factionID = UUID.randomUUID();
		this.players = new ArrayList<>();
		this.players.add(owner.getUniqueId());
		saveData();
	}
	
	public void saveData() {

		ClusterCore.getInstance().getMongoHook().saveData(factionID.toString(), this, "factions");
	}
	
	public void addPlayer(UUID player) {
		players.add(player);
		messageAll(String.format(Lang_EN_US.PLAYER_JOINED_FACTION ,Bukkit.getPlayer(player).getName()) );
	}
	
	public void promotePlayer(UUID player) {
		
	}
	
	public void removePlayer(UUID player, FactionPlayerRemoveReason reason) {
		if(this.factionOwner.equals(player))
		{
			//CHOOSE MOD AND PROMOTE
		}
		messageAll(String.format(reason == FactionPlayerRemoveReason.KICKED ? Lang_EN_US.PLAYER_KICKED_FACTION : Lang_EN_US.PLAYER_LEFT_FACTION, Bukkit.getPlayer(player).getName()));
		players.remove(player);
		
	}
	
	public void messageAll(String message) {
		for(UUID uuid : players) {
			Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		}
	}
	
}




















