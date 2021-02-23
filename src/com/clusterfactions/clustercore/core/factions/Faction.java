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
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.persistence.serialization.LocationSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDListSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDSerializer;
import com.clusterfactions.clustercore.persistence.serialization.VariableSerializer;
import com.clusterfactions.clustercore.persistence.serialization.Vector2IntegerListSerializer;
import com.clusterfactions.clustercore.persistence.serialization.WarpListSerializer;
import com.clusterfactions.clustercore.util.annotation.AlternateSerializable;
import com.clusterfactions.clustercore.util.annotation.DoNotSerialize;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class Faction implements Listener{
	
	@DoNotSerialize public static final int maxPlayers = 10;
	
	@Getter @Setter private String factionTag;
	@Getter @Setter private String factionLower; //faction tag but lowercase

	@Setter @AlternateSerializable(WarpListSerializer.class) private HashMap<String, Location> warpList;
	@Getter @Setter @AlternateSerializable(LocationSerializer.class) private Location factionHome;
	
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> allies = new ArrayList<>();
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> enemies = new ArrayList<>(); 
	
	@Getter @AlternateSerializable(UUIDListSerializer.class) private ArrayList<UUID> players = new ArrayList<>();
	@Getter @AlternateSerializable(UUIDListSerializer.class) private List<UUID> bannedPlayers = new ArrayList<>();
	@Getter @AlternateSerializable(Vector2IntegerListSerializer.class) private ArrayList<Vector2Integer> claimedChunks = new ArrayList<>();
	@Getter @Setter private Map<String,Integer> permissionMap = new HashMap<>(); //PERMISSION, WEIGHT
	@Getter @Setter private Map<String,Integer> roleMap = new HashMap<>(); //UUID,RANK
	
	@AlternateSerializable(UUIDListSerializer.class) private List<UUID> inviteList = new ArrayList<>();
	
	@AlternateSerializable(UUIDListSerializer.class) private List<UUID> inviteListAlly = new ArrayList<>();
	@AlternateSerializable(UUIDListSerializer.class) private List<UUID> inviteListNeutral = new ArrayList<>();
	
	//CLAIMED CHUNKS
	@Getter @AlternateSerializable(UUIDSerializer.class) private UUID factionID;
	
	/*
	 * CALLED WHEN A NEW FACTION IS MADE
	*/
	public Faction(Player owner, String tag) {
		this.factionTag = tag;
		this.factionLower = tag.toLowerCase();
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
			ClusterCore.getInstance().getMongoHook().saveValue(this.factionID.toString(), fieldName, data, "factions");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void disband() {
		messageAll(Lang.FACTION_DISBANDED);
		if(getClaimedChunks() != null) {
			ArrayList<Vector2Integer> chunks = (ArrayList<Vector2Integer>) getClaimedChunks().clone();
			for(Vector2Integer claim : chunks)
				ClusterCore.getInstance().getFactionClaimManager().removeClaimChunk(claim, this);
		}
		ArrayList<UUID> playerL = (ArrayList<UUID>) getPlayers().clone();
		for(UUID uuid : playerL)
			removePlayer(Bukkit.getPlayer(uuid), null);
		
		ClusterCore.getInstance().getMongoHook().deleteData(this.getFactionID().toString(), "factions");
	}
	
	public void addPlayer(Player player) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		data.setFaction(this.factionID);
		data.saveData("faction");
		players.add(player.getUniqueId());
		messageAll(Lang.PLAYER_JOINED_FACTION, player.getName());
		saveData("players");
	}
	
	public void removePlayer(Player player, FactionPlayerRemoveReason reason) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		data.setFaction(null);
		data.saveData("faction");
		players.remove(player.getUniqueId());
		saveData("players");
		
		if(reason != null)
		switch(reason) {
		case KICKED:
			data.sendMessage(Lang.YOU_HAVE_BEEN_KICKED, this.factionTag);
			messageAll(Lang.PLAYER_KICKED_FACTION, player.getName());
			break;
		case BANNED:
			data.sendMessage(Lang.YOU_HAVE_BEEN_BANNED, this.factionTag);
			messageAll(Lang.PLAYER_BANNED_FACTION, player.getName());
			break;
		case LEFT:
			data.sendMessage(Lang.LEFT_FACTION, this.factionTag);
			messageAll(Lang.PLAYER_LEFT_FACTION, player.getName());
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
		
		messageAll(Lang.PLAYER_INVITED_TO_FACTION, invitee.getName());
		inviteeData.sendMessage(Lang.INVITED_TO_FACTION1, this.getFactionTag());
		inviteeData.sendMessage(Lang.INVITED_TO_FACTION2, this.getFactionTag());

		if(inviteList == null) inviteList = new ArrayList<>();
		inviteList.add(invitee.getUniqueId());		
		saveData("inviteList");
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
	
	public int getClaimCount()
	{
		if(claimedChunks == null) claimedChunks = new ArrayList<>();
		return claimedChunks.size();
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
	
	public void messageAll(String message, Object... args) {
		if(players == null) return;
		for(UUID uuid : players) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(uuid);
			playerData.sendMessage(message, args);
		}
	}
	
	public void messageAll(Lang message, Object... args) {
		if(players == null) return;
		for(UUID uuid : players) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(uuid);
			playerData.sendMessage(message, args);
		}
	}
	
	public void messageAll(Lang message, FactionPerm perm, Object... args) {
		if(players == null) return;
		for(UUID uuid : players) {
			if(!hasPerm(Bukkit.getPlayer(uuid), perm)) continue;
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(uuid);
			playerData.sendMessage(message, args);
		}
	}
	
	public int getFactionPower() {
		int power = 0;
		if(players == null) players = new ArrayList<>();
		for(UUID uuid : players)
		{
			int playerPower = ClusterCore.getInstance().getMongoHook().getValue(uuid.toString(), "power", Integer.class, "players") == null ? 0 : ClusterCore.getInstance().getMongoHook().getValue(uuid.toString(), "power", Integer.class, "players");
			power += playerPower;
		}
		return power;
	}
	
	public int getSparePower() {
		if(claimedChunks == null) claimedChunks = new ArrayList<>();
		return getFactionPower() - getClaimedChunks().size();
	}
	
	public int getPlayerCount() {
		return players.size();
	}
	
	public boolean isSame(Faction faction) {
		return faction.getFactionID().equals(getFactionID());
	}
	
	public boolean isInvitedAlly(Faction target) {
		if(inviteListAlly == null) inviteListAlly = new ArrayList<>();
		return inviteListAlly.contains(target.getFactionID());
	}
	
	public boolean isAlly(Faction target) {
		if(allies == null) allies = new ArrayList<>();
		return allies.contains(target.getFactionID());
	}
	
	public boolean isInvitedNeutral(Faction target) {
		if(inviteListNeutral == null) inviteListNeutral = new ArrayList<>();
		return inviteListNeutral.contains(target.getFactionID());
	}
	
	public boolean isNeutral(Faction target) {
		if(allies == null) allies = new ArrayList<>();
		if(enemies == null) enemies = new ArrayList<>();
		if(enemies.contains(target.getFactionID())) return false;
		if(allies.contains(target.factionID)) return false;
		return true;
	}
	
	public boolean isEnemy(Faction target) {
		if(enemies == null) enemies = new ArrayList<>();
		return enemies.contains(target.getFactionID());		
	}
	
	public void setFactionAlly(Faction target) {
		if(inviteListAlly == null) inviteListAlly = new ArrayList<>();
		if(target.inviteListAlly == null) target.inviteListAlly = new ArrayList<>();
		
		if(!target.inviteListAlly.contains(getFactionID())){
			inviteListAlly.add(target.getFactionID());
			
			messageAll(Lang.FACTION_INVITED_ALLY, target.getFactionTag());
			
			target.messageAll(Lang.FACTION_INVITE_ALLY_REQUEST_1, getFactionTag());
			target.messageAll(Lang.FACTION_INVITE_ALLY_REQUEST_2, getFactionTag());
		}else{
			target.messageAll(Lang.FACTION_FORMED_ALLY, getFactionTag());
			messageAll(Lang.FACTION_FORMED_ALLY, target.getFactionTag());
			
			if(allies == null) allies = new ArrayList<>();
			if(target.getAllies() == null) target.allies = new ArrayList<>();
			
			removeFactionEnemy(target);
			target.removeFactionEnemy(this);
			
			target.allies.add(getFactionID());
			allies.add(target.getFactionID());
			
			target.removeInvites(this);
			removeInvites(target);
			
			target.saveData();
			saveData();
		}
	}
	
	public void removeFactionAlly(Faction target) {
		if(allies == null) allies = new ArrayList<>();
		if(target.allies == null) target.allies = new ArrayList<>();
		
		if(!allies.contains(target.factionID)) return;
		
		allies.remove(target.getFactionID());
		target.allies.remove(getFactionID());
		
		messageAll(Lang.FACTION_NO_LONGER_ALLY, target.getFactionTag());
		target.messageAll(Lang.FACTION_NO_LONGER_ALLY, getFactionTag());
		
		target.saveData("allies");
		target.saveData("enemies");
		saveData("allies");
		saveData("enemies");
	}
	
	public void setFactionEnemy(Faction target) {
		if(enemies == null) enemies = new ArrayList<>();
		if(target.enemies == null) target.enemies = new ArrayList<>();
		
		target.removeFactionAlly(this);
		removeFactionAlly(target);
		
		enemies.add(target.getFactionID());
		target.enemies.add(getFactionID());
		
		target.messageAll(Lang.FACTION_FORMED_ENEMY, getFactionTag());
		messageAll(Lang.FACTION_FORMED_ENEMY, target.getFactionTag());
		
		target.saveData("allies");
		target.saveData("enemies");
		saveData("allies");
		saveData("enemies");
	}
	
	public void removeFactionEnemy(Faction target) {
		if(enemies == null) enemies = new ArrayList<>();
		if(!enemies.contains(target.factionID)) return;
		
		enemies.remove(target.getFactionID());
		target.enemies.remove(getFactionID());

		messageAll(Lang.FACTION_NO_LONGER_ENEMY, target.getFactionTag());
		target.messageAll(Lang.FACTION_NO_LONGER_ENEMY, getFactionTag());
		
		target.saveData("allies");
		target.saveData("enemies");
		saveData("allies");
		saveData("enemies");
	}
	
	public void setFactionNeutral(Faction target) {
		if(inviteListNeutral == null) inviteListNeutral = new ArrayList<>();
		if(target.inviteListNeutral == null) target.inviteListNeutral = new ArrayList<>();
		if(!target.inviteListNeutral.contains(getFactionID()) && isEnemy(target)){
			inviteListNeutral.add(target.getFactionID());
			
			messageAll(Lang.FACTION_INVITED_NEUTRAL, target.getFactionTag());
			
			target.messageAll(Lang.FACTION_INVITE_NEUTRAL_REQUEST_1, getFactionTag());
			target.messageAll(Lang.FACTION_INVITE_NEUTRAL_REQUEST_2, getFactionTag());
		}else{
			target.messageAll(Lang.FACTION_FORMED_NEUTRAL, getFactionTag());
			messageAll(Lang.FACTION_FORMED_NEUTRAL, target.getFactionTag());
			
			removeFactionEnemy(target);
			removeFactionAlly(target);
			
			target.removeFactionAlly(this);
			target.removeFactionEnemy(this);

			target.removeInvites(this);
			removeInvites(target);
			
			
			target.saveData();
			saveData();
		}
	}
	
	public void removeInvites(Faction target) {
		if(inviteListNeutral == null) inviteListNeutral = new ArrayList<>();
		if(inviteListAlly == null) inviteListAlly = new ArrayList<>();
		if(inviteListAlly.contains(target.getFactionID())) inviteListAlly.remove(target.getFactionID());
		if(inviteListNeutral.contains(target.getFactionID())) inviteListNeutral.remove(target.getFactionID());
		saveData();
	}
	
}




















