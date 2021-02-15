package com.clusterfactions.clustercore.core.factions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.util.FactionPlayerRemoveReason;
import com.clusterfactions.clustercore.core.lang.Lang_EN_US;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.persistence.serialization.LocationSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDListSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDSerializer;
import com.clusterfactions.clustercore.persistence.serialization.Vector2IntegerListSerializer;
import com.clusterfactions.clustercore.util.annotation.AlternateSerializable;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@NoArgsConstructor
public class Faction implements Listener{

	@Getter @Setter private String factionName;
	@Getter @Setter private String factionTag;
	

	@Getter @Setter @AlternateSerializable(LocationSerializer.class) private Location factionHome;
	
	@Getter @AlternateSerializable(UUIDSerializer.class) private UUID factionOwner;
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> players = new ArrayList<>();
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> moderators = new ArrayList<>();
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> coLeaders = new ArrayList<>();
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> banList = new ArrayList<>();
	@Getter @AlternateSerializable(Vector2IntegerListSerializer.class) private ArrayList<Vector2Integer> claimedChunks = new ArrayList<>();
	
	@AlternateSerializable(UUIDListSerializer.class) private List<UUID> inviteList = new ArrayList<>();
	//CLAIMED CHUNKS
	@Getter @AlternateSerializable(UUIDSerializer.class) private UUID factionID;
	
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
	
	public void addPlayer(Player player) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		data.setFaction(this);
		data.saveData();
		
		players.add(player.getUniqueId());
		messageAll(String.format(Lang_EN_US.PLAYER_JOINED_FACTION, player.getName()) );
		saveData();
	}
	
	public void removePlayer(Player player, FactionPlayerRemoveReason reason) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		data.sendMessage( String.format(reason == FactionPlayerRemoveReason.LEFT ? Lang_EN_US.LEFT_FACTION : Lang_EN_US.KICKED_FROM_FACTION, this.factionName));
		data.setFaction(null);
		data.saveData();
		
		
		if(this.factionOwner.equals(player))
		{
			//CHOOSE MOD AND PROMOTE
		}
		messageAll(String.format(reason == FactionPlayerRemoveReason.KICKED ? Lang_EN_US.PLAYER_KICKED_FACTION : Lang_EN_US.PLAYER_LEFT_FACTION, player.getName()));
		players.remove(player.getUniqueId());
		saveData();
	}
	
	public void promotePlayer(UUID player) {
		
	}
	
	public void invitePlayer(Player invter, Player invitee)
	{
		PlayerData inviteeData = ClusterCore.getInstance().getPlayerManager().getPlayerData(invitee);
		
		messageAll(String.format(Lang_EN_US.PLAYER_INVITED_TO_FACTION, invitee.getName()));
		inviteeData.sendMessage(String.format(Lang_EN_US.INVITED_TO_FACTION1, this.getFactionName()));
		inviteeData.sendMessage(String.format(Lang_EN_US.INVITED_TO_FACTION2, this.getFactionTag()));

		if(inviteList == null) inviteList = new ArrayList<>();
		inviteList.add(invitee.getUniqueId());		
		saveData();
	}
	
	public boolean isCoLeader(Player player) {
		if(coLeaders == null) coLeaders = new ArrayList<>();
		return coLeaders.contains(player.getUniqueId());
	}
	
	public boolean isLeader(Player player) {
		return factionOwner.equals(player.getUniqueId());
	}
	
	public boolean isModerator(Player player) {
		if(moderators == null) moderators = new ArrayList<>();
		return moderators.contains(player.getUniqueId());
	}
	
	public boolean inviteListContains(Player player) {
		if(inviteList == null) inviteList = new ArrayList<>();
		for(UUID uuid : inviteList) {
			System.out.print(uuid.toString());
		}
		return inviteList.contains(player.getUniqueId());
	}
	
	public void acceptInvite(Player player) {
		inviteList.remove(player.getUniqueId());
		addPlayer(player);
		saveData();
	}
	
	public void removeClaimChunk(Vector2Integer chunkLoc) {
		claimedChunks.remove(chunkLoc);
		saveData();
	}
	
	public void addClaimChunk(Vector2Integer chunkLoc) {
		if(claimedChunks == null) claimedChunks = new ArrayList<>();
		this.claimedChunks.add(chunkLoc);
		saveData();
	}
	
	public void messageAll(String message) {
		for(UUID uuid : players) {
			Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		}
	}
	
}




















