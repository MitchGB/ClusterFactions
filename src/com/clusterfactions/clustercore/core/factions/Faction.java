package com.clusterfactions.clustercore.core.factions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.util.FactionPerm;
import com.clusterfactions.clustercore.core.factions.util.FactionPlayerRemoveReason;
import com.clusterfactions.clustercore.core.factions.util.FactionRole;
import com.clusterfactions.clustercore.core.lang.Lang_EN_US;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.persistence.serialization.LocationSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDListSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDSerializer;
import com.clusterfactions.clustercore.persistence.serialization.VariableSerializer;
import com.clusterfactions.clustercore.persistence.serialization.Vector2IntegerListSerializer;
import com.clusterfactions.clustercore.persistence.serialization.WarpListSerializer;
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
	

	@Setter @AlternateSerializable(WarpListSerializer.class) private HashMap<String, Location> warpList;
	@Getter @Setter @AlternateSerializable(LocationSerializer.class) private Location factionHome;
	
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> allies = new ArrayList<>();
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> enemies = new ArrayList<>(); 
	
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> players = new ArrayList<>();
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> bannedPlayers = new ArrayList<>();
	@Getter @AlternateSerializable(Vector2IntegerListSerializer.class) private ArrayList<Vector2Integer> claimedChunks = new ArrayList<>();
	@Getter @Setter private Map<String,Integer> permissionMap = new HashMap<>(); //PERMISSION, WEIGHT
	@Getter @Setter private Map<String,Integer> roleMap = new HashMap<>(); //UUID,RANK
	
	@AlternateSerializable(UUIDListSerializer.class) private List<UUID> inviteList = new ArrayList<>();
	@AlternateSerializable(UUIDListSerializer.class) private List<UUID> allyshipInviteList = new ArrayList<>();
	
	
	//CLAIMED CHUNKS
	@Getter @AlternateSerializable(UUIDSerializer.class) private UUID factionID;
	
	/*
	 * CALLED WHEN A NEW FACTION IS MADE
	*/
	public Faction(Player owner, String name, String tag) {
		this.factionName = name;
		this.factionTag = tag;
		this.factionID = UUID.randomUUID();
		
		this.players = new ArrayList<>();
		this.players.add(owner.getUniqueId());
		
		this.roleMap.put(owner.getUniqueId().toString(), FactionRole.LEADER.getWeight());
		saveData();
	}
	
	public void saveData() {
		ClusterCore.getInstance().getMongoHook().saveData(factionID.toString(), this, "factions");
	}
	
	@SuppressWarnings("unchecked")
	public void saveData(String fieldName) {
		try {
			Object data = null;
			Field[] allFields = this.getClass().getDeclaredFields(); 
			for(Field field : allFields) {
				field.setAccessible(true);
				if(field.getName().equalsIgnoreCase(fieldName))
				{
					data = field.get(this);
					if(field.getAnnotation(AlternateSerializable.class) != null) 
						data = ((VariableSerializer<Object>)field.getAnnotation(AlternateSerializable.class).value().getDeclaredConstructor().newInstance()).serialize(field.get(this));
					break;
				}
			}
			if(data == null) return;
			ClusterCore.getInstance().getMongoHook().saveObject(this.factionID.toString(), fieldName, data, "factions");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addPlayer(Player player) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		data.setFaction(this.factionID);
		data.saveData();
		players.add(player.getUniqueId());
		messageAll(String.format(Lang_EN_US.PLAYER_JOINED_FACTION, player.getName()) );
		saveData("players");
	}
	
	public void removePlayer(Player player, FactionPlayerRemoveReason reason) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		data.setFaction(null);
		players.remove(player.getUniqueId());
		data.saveData();
		saveData("players");
		
		switch(reason) {
		case KICKED:
			data.sendMessage(Lang_EN_US.YOU_HAVE_BEEN_KICKED, this.factionName);
			messageAll(String.format(Lang_EN_US.PLAYER_KICKED_FACTION, player.getName()));
			break;
		case BANNED:
			data.sendMessage(Lang_EN_US.YOU_HAVE_BEEN_BANNED, this.factionName);
			messageAll(String.format(Lang_EN_US.PLAYER_BANNED_FACTION, player.getName()));
			break;
		case LEFT:
			data.sendMessage(Lang_EN_US.LEFT_FACTION, this.factionName);
			messageAll(String.format(Lang_EN_US.PLAYER_LEFT_FACTION, player.getName()));
			break;
		
		}
	}

	public void banPlayer(Player player) {
		if(bannedPlayers == null) bannedPlayers = new ArrayList<>();
		bannedPlayers.add(player.getUniqueId());
		removePlayer(player, FactionPlayerRemoveReason.BANNED);
		saveData("bannedPlayers");
	}
	
	public void unbanPlayer(Player player) {
		if(bannedPlayers == null) bannedPlayers = new ArrayList<>();
		bannedPlayers.remove(player.getUniqueId());
		saveData("bannedPlayers");
	}
	
	public void kickPlayer(Player player) {
		removePlayer(player, FactionPlayerRemoveReason.KICKED);
	}
	
	public void uninvitePlayer(Player invitee) {
		inviteList.remove(invitee.getUniqueId());
		saveData("inviteList");
	}
	
	public void invitePlayer(Player invitee)
	{
		PlayerData inviteeData = ClusterCore.getInstance().getPlayerManager().getPlayerData(invitee);
		
		messageAll(String.format(Lang_EN_US.PLAYER_INVITED_TO_FACTION, invitee.getName()));
		inviteeData.sendMessage(Lang_EN_US.INVITED_TO_FACTION1, this.getFactionName());
		inviteeData.sendMessage(Lang_EN_US.INVITED_TO_FACTION2, this.getFactionTag());

		if(inviteList == null) inviteList = new ArrayList<>();
		inviteList.add(invitee.getUniqueId());		
		saveData("inviteList");
	}
	
	public void inviteAllyFaction(Faction invitee)
	{
		messageAll(String.format(Lang_EN_US.FACTION_INVITED_TOALLY, invitee.getFactionName()));
		invitee.messageAll(String.format(Lang_EN_US.INVITE_ALLY_FACTION1, this.getFactionName()), FactionPerm.ALLY);
		invitee.messageAll(String.format(Lang_EN_US.INVITE_ALLY_FACTION2, this.getFactionTag()), FactionPerm.ALLY);

		if(allyshipInviteList == null) allyshipInviteList = new ArrayList<>();
		allyshipInviteList.add(invitee.getFactionID());		
		saveData("allyshipInviteList");
	}
	
	public boolean isAllied(Faction faction) {
		if(allies == null) allies = new ArrayList<>();
		return allies.contains(faction.getFactionID());	
	}
	
	public void allyFaction(Faction faction) {
		if(allyshipInviteList == null) allyshipInviteList = new ArrayList<>();
		if(allies == null) allies = new ArrayList<>();
		if(enemies == null) enemies = new ArrayList<>();
		if(enemies.contains(faction.getFactionID())) enemies.remove(faction.getFactionID());
		this.allyshipInviteList.remove(faction.getFactionID());
		this.allies.add(faction.getFactionID());
		saveData("allies");
		saveData("allyshipInviteList");
	}
	
	public void unally(Faction faction) {
		if(allyshipInviteList == null) allyshipInviteList = new ArrayList<>();
		if(allies == null) allies = new ArrayList<>();
		UUID facId = faction.getFactionID();
		
		allies.remove(facId);
		messageAll(String.format(Lang_EN_US.FACTION_NO_LONGER_ALLIES, faction.getFactionName()));
		saveData("allies");
	}
	
	public void enemy(Faction faction) {
		if(allyshipInviteList == null) allyshipInviteList = new ArrayList<>();
		if(allies == null) allies = new ArrayList<>();
		if(enemies == null) enemies = new ArrayList<>();
		UUID facId = faction.getFactionID();
		
		if(allyshipInviteList.contains(facId)) allyshipInviteList.remove(facId);
		if(allies.contains(facId)) allies.remove(facId);
		enemies.add(facId);
		messageAll(String.format(Lang_EN_US.FACTION_ARE_NOW_ENEMIES, faction.getFactionName()));
		saveData("enemies");		
		saveData("allies");
		saveData("allyshipInviteList");
		
	}
	
	public void unenemy(Faction faction) {
		if(enemies == null) enemies = new ArrayList<>();
		UUID facId = faction.getFactionID();
		
		enemies.remove(facId);
		messageAll(String.format(Lang_EN_US.FACTION_NO_LONGER_ENEMIES, faction.getFactionName()));
		saveData("enemies");
		
	}
	
	public boolean isEnemy(Faction faction) {
		if(enemies == null) enemies = new ArrayList<>();
		return enemies.contains(faction.getFactionID());	
	}
	
	public int getPermWeight(FactionPerm perm) {
		if(permissionMap == null) permissionMap = new HashMap<>();
		if(!permissionMap.containsKey(perm.getId()))
			return FactionRole.getHighestWeight();
		return permissionMap.get(perm.getId());
	}
	
	public boolean hasPerm(Player player, FactionPerm perm){
		return getPlayerRole(player).getWeight() >= getPermWeight(perm);
	}
	
	public void setPermWeight(FactionPerm perm, int weight) {
		if(permissionMap == null) permissionMap = new HashMap<>();
		if(permissionMap.containsKey(perm.getId()))
			permissionMap.remove(perm.getId());
		
		permissionMap.put(perm.getId(), weight);
		saveData("permissionMap");
	}
	
	public FactionRole getPlayerRole(UUID uuid) {
		if(roleMap == null) roleMap = new HashMap<>();
		if(roleMap.containsKey(uuid.toString()))
			return FactionRole.getRoleByWeight(roleMap.get(uuid.toString()));
		return FactionRole.RECRUIT;
	}
	
	public FactionRole getPlayerRole(Player player) {
		return getPlayerRole(player.getUniqueId());
	}
	
	public void cyclePermWeight(FactionPerm perm) {
		setPermWeight(perm, getPermWeight(perm) == FactionRole.getHighestWeight() ? 1 : getPermWeight(perm)+1);
		saveData();
	}
	
	public void promotePlayer(UUID player) {
		if(roleMap == null) roleMap = new HashMap<>();
		if(!this.roleMap.containsKey(player.toString()))
			this.roleMap.put(player.toString(), getPlayerRole(player).getWeight()+1);
		else
			this.roleMap.replace(player.toString(), getPlayerRole(player).getWeight()+1);
		saveData("roleMap");
	}
	
	public void demotePlayer(UUID player) {
		if(roleMap == null) roleMap = new HashMap<>();
		if(!this.roleMap.containsKey(player.toString()))
			return;
		else if(getPlayerRole(player).getWeight()>1)
			this.roleMap.replace(player.toString(), getPlayerRole(player).getWeight()-1);
		saveData("roleMap");
	}
	
	public boolean inviteListContains(Player player) {
		if(inviteList == null) inviteList = new ArrayList<>();
		return inviteList.contains(player.getUniqueId());
	}
	
	public boolean allyInviteListContains(Faction fac) {
		if(allyshipInviteList == null) allyshipInviteList = new ArrayList<>();
		if(fac == null) return false;
		return allyshipInviteList.contains(fac.getFactionID());
	}
	
	public void acceptInvite(Player player) {
		inviteList.remove(player.getUniqueId());
		addPlayer(player);
		saveData("inviteList");
	}
	
	public void removeClaimChunk(Vector2Integer chunkLoc) {
		claimedChunks.remove(chunkLoc);
		saveData("claimedChunks");
	}
	
	public void addClaimChunk(Vector2Integer chunkLoc) {
		if(claimedChunks == null) claimedChunks = new ArrayList<>();
		this.claimedChunks.add(chunkLoc);
		saveData("claimedChunks");
	}
	
	public void addWarp(String name, Location loc) {
		if(warpList == null) warpList = new HashMap<>();
		warpList.put(name, loc);
		saveData("warpList");
	}
	
	public void removeWarp(String name) {
		if(warpList == null) warpList = new HashMap<>();
		if(!warpList.containsKey(name)) return;
		warpList.remove(name);
		saveData("warpList");
	}
	
	public boolean warpExists(String name) {
		if(warpList == null) warpList = new HashMap<>();
		return warpList.containsKey(name);
	}
	
	public Location getWarp(String name) {
		if(warpList == null) warpList = new HashMap<>();
		if(!warpExists(name)) return null;
		return warpList.get(name);
	}
	
	public List<String> getWarps(){
		if(warpList == null) warpList = new HashMap<>();
		List<String> ret = new ArrayList<>();
		for(Entry<String, Location> entrySet : warpList.entrySet())
		{
			ret.add(entrySet.getKey());
		}
		return ret;
	}
	
	public void messageAll(String message) {
		for(UUID uuid : players) {
			Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		}
	}
	public void messageAll(String message, FactionPerm perm) {
		for(UUID uuid : players) {
			if(!hasPerm(Bukkit.getPlayer(uuid), perm)) continue;
			Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		}
	}
	
}




















