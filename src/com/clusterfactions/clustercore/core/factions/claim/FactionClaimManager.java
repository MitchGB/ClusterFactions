package com.clusterfactions.clustercore.core.factions.claim;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.util.FactionPerm;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.lang.LanguageManager;
import com.clusterfactions.clustercore.core.listeners.events.claim.ClaimEnterEvent;
import com.clusterfactions.clustercore.core.listeners.events.claim.ClaimExitEvent;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

public class FactionClaimManager implements Listener{
	
	/*
	 * North -z
	 * East +x
	 * South +z
	 * West -x
	 */

	public HashMap<UUID, ChunkOwner> playerCache = new HashMap<>();
	private LinkedHashMap<Vector2Integer, ChunkOwner> chunkCache = new LinkedHashMap<>();
	
	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		ChunkOwner owner = getChunkOwnerCache(getChunkVector(player.getLocation()));
		if(!playerCache.containsKey(player.getUniqueId()))
			playerCache.put(player.getUniqueId(), null);
		
		if(!owner.isNull() && playerCache.get(player.getUniqueId()) == null){
			playerCache.put(player.getUniqueId(), owner);
			Bukkit.getPluginManager().callEvent(new ClaimEnterEvent(player, owner));
			return;
		}
		if(owner.isNull() && playerCache.get(player.getUniqueId()) != null) {
			playerCache.put(player.getUniqueId(), null);
			Bukkit.getPluginManager().callEvent(new ClaimExitEvent(player));
			return;
		}
		if(playerCache.get(player.getUniqueId()) != null && !playerCache.get(player.getUniqueId()).getOwnerUUID().equals(owner.getOwnerUUID())){
			playerCache.replace(player.getUniqueId(), owner);
			Bukkit.getPluginManager().callEvent(new ClaimEnterEvent(player, owner));
			return;
		}
		
	}
	
	@EventHandler
	public void ClaimExitEvent(ClaimExitEvent e) {
		Player player = e.getPlayer();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		LanguageManager langMan = ClusterCore.getInstance().getLanguageManager();
		player.sendTitle(langMan.getString(playerData.getLocale(), Lang.WILDERNESS_TAG), langMan.getString(playerData.getLocale(), Lang.WILDERNESS_SUBTEXT), 5, 40, 5);
	}
	
	@EventHandler
	public void ClaimEnterEvent(ClaimEnterEvent e) {
		Player player = e.getPlayer();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		LanguageManager langMan = ClusterCore.getInstance().getLanguageManager();
		if(e.getOwner().isAdmin()) {
			switch(e.getOwner().getOwnerUUID().toUpperCase()) {
			case "WARZONE":
				player.sendTitle(langMan.getString(playerData.getLocale(), Lang.WARZONE_TAG), langMan.getString(playerData.getLocale(), Lang.WARZONE_SUBTEXT), 5, 40, 5);
				break;
			case "SAFEZONE":
				player.sendTitle(langMan.getString(playerData.getLocale(), Lang.SAFEZONE_TAG), langMan.getString(playerData.getLocale(), Lang.SAFEZONE_SUBTEXT), 5, 40, 5);
				break;
			}
		}else {
			Faction faction = e.getOwner().getFaction();
			player.sendTitle(Colors.parseColors(faction.getFactionTag()), Colors.parseColors(""), 5, 40, 5);
		}
	}
	
	public boolean canManipulateBlock(Location loc, Player player) {
		ChunkOwner owner = getChunkOwner(getChunkVector(loc));
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		if(playerData.isAdminOverrideMode()) return true;
		if(owner.isNull()) return true;
		if(owner.isAdmin()) return false;
		if(owner.getFaction().containsPlayer(player)) {
			if(owner.getFaction().hasPerm(player, FactionPerm.BUILD))
				return true;
		}
		return false;
	}
	
	public FactionClaimManager() {
		ClusterCore.getInstance().registerListener(this);
	}
	
	public Vector2Integer getChunkVector(Location loc) {
		return new Vector2Integer((int)Math.ceil(loc.getX()/16), (int)Math.ceil(loc.getZ()/16));
	}
	
	public Vector2Integer getChunkVector(int x, int z) {
		return new Vector2Integer((int)Math.ceil(x/16), (int)Math.ceil(z/16));
	}
	
	public void claimChunk(Vector2Integer chunkLoc, Faction faction) {
		claimChunk(chunkLoc, new ChunkOwner(faction.getFactionID().toString()));
	}
	
	public void claimChunk(Vector2Integer chunkLoc, ChunkOwner owner) {
		ClusterCore.getInstance().getMongoHook().saveValue(chunkLoc.toString(), "owner", owner.getOwnerUUID(), "chunks");
		ClusterCore.getInstance().getMongoHook().saveValue(chunkLoc.toString(), "isAdmin", owner.isAdmin(), "chunks");
		chunkCache.put(chunkLoc, owner);
		if(!owner.isAdmin())
			owner.getFaction().addClaimChunk(chunkLoc);
	}
	
	public void unclaimChunk(Vector2Integer chunkLoc, Faction faction) {
		unclaimChunk(chunkLoc, new ChunkOwner(faction.getFactionID().toString()));
	}
	
	public void unclaimChunk(Vector2Integer chunkLoc, ChunkOwner owner) {
		ClusterCore.getInstance().getMongoHook().saveValue(chunkLoc.toString(), "owner", "", "chunks");
		ClusterCore.getInstance().getMongoHook().saveValue(chunkLoc.toString(), "isAdmin", "", "chunks");		
		chunkCache.put(chunkLoc, null);
		if(!owner.isAdmin())
			owner.getFaction().removeClaimChunk(chunkLoc);
	}
	
	public ChunkOwner getChunkOwner(Vector2Integer chunkLoc) {
		String ownerUUID = ClusterCore.getInstance().getMongoHook().getValue(chunkLoc.toString(), "owner", String.class, "chunks");
		Object adminObj = ClusterCore.getInstance().getMongoHook().getValue(chunkLoc.toString(), "isAdmin", Boolean.class, "chunks");
		Boolean isAdmin = adminObj instanceof Boolean ? (Boolean)adminObj : false;
		ChunkOwner owner = new ChunkOwner(ownerUUID, isAdmin);
		chunkCache.put(chunkLoc, owner);
		return owner;
	}

	public ChunkOwner getChunkOwnerCache(Location loc) {
		return chunkCache.containsKey(getChunkVector(loc)) ? chunkCache.get(getChunkVector(loc)) : getChunkOwner(getChunkVector(loc));
	}
	
	public ChunkOwner getChunkOwnerCache(Vector2Integer chunkLoc) {
		return chunkCache.containsKey(chunkLoc) ? chunkCache.get(chunkLoc) : getChunkOwner(chunkLoc);
	}
	
	public boolean isChunkClaimed(Location loc) {
		return isChunkClaimed(getChunkVector(loc));
	}
	
	public boolean isChunkClaimed(Vector2Integer chunkLoc) {
		return getChunkOwnerCache(chunkLoc) != null;
	}
	
	public Vector2Integer[] getClaimNeighbours(Location claim) {
		return getClaimNeighbours(getChunkVector(claim));
	}
	
	public Vector2Integer[] getClaimNeighbours(Vector2Integer claim) {
		return new Vector2Integer[] {
				new Vector2Integer(claim.getX()+1,claim.getZ()+0),
				new Vector2Integer(claim.getX()-1,claim.getZ()+0),
				new Vector2Integer(claim.getX()+0,claim.getZ()+1),
				new Vector2Integer(claim.getX()+0,claim.getZ()-1)
		};
	}
	
	public int getEmptyClaimNeighbours(Location claim) {
		return getEmptyClaimNeighbours(getChunkVector(claim));
	}
	
	public int getEmptyClaimNeighbours(Vector2Integer claim) {
		ChunkOwner claimed = getChunkOwnerCache(claim);
		int empty = 0;
		for(Vector2Integer neighbour : getClaimNeighbours(claim))
		{
			if(getChunkOwnerCache(neighbour) == null || !getChunkOwnerCache(neighbour).equals(claimed))
				empty++;
		}
		return empty;
	}
	
	public void claimArea(Player player, Location loc, ChunkOwner owner, int xrad, int zrad) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(ClusterCore.getInstance(), new Runnable() {
	        @Override
	        public void run() {
	        	PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
	        	Vector2Integer playerChunk = getChunkVector(loc);
		
	        	Vector2Integer[][] claimMap = new Vector2Integer[xrad*2][zrad*2];
		
	        	int pX = playerChunk.getX();
	        	int pZ = playerChunk.getZ();
		
	        	int uX = pX + xrad;
	        	int uZ = pZ + zrad;
		
	        	int lX = pX - xrad;
	        	int lZ = pZ - zrad;
	        	int xIndex = 0;
	        	int zIndex = 0;

	        	int overlapping = 0;
		
	        	for(int z = lZ; z < uZ; z++)
	        	{
	        		xIndex = 0;
	        		for(int x = lX; x <uX; x++)
	        		{
	        			claimMap[xIndex][zIndex] = new Vector2Integer(x,z);
	        			if(!getChunkOwnerCache(claimMap[xIndex][zIndex]).isNull())
	        			{
	        				if(getChunkOwnerCache(claimMap[xIndex][zIndex]).getOwnerUUID().equals(owner.getOwnerUUID()))
	        				{
	        					claimMap[xIndex][zIndex] = null;
	        					overlapping++;
	        					xIndex++;

	        					continue;
	        				}
	        				playerData.sendMessage(Lang.CLAIM_RADIUS_OVERLAPPING);
	        				return;
	        			}

	        			xIndex++;
	        		}
	        		zIndex++;
	        	}
		
	        	for(Vector2Integer[] vA : claimMap) {
	        		for(Vector2Integer v : vA)
	        		{
	        			if(v == null) continue;
	        			claimChunk(v, owner);
	        		}
	        	}
		
	        	playerData.sendMessage(Lang.SUCCESSFULL_CLAIM_AREA, (xrad*2)*(zrad*2) - overlapping + "", overlapping + "");
	        }
		}, 1);
	}
	
	public void removeClaimArea(Player player, int xrad, int zrad) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
    	Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		Vector2Integer playerChunk = getChunkVector(player.getLocation());
		
		Vector2Integer[][] claimMap = new Vector2Integer[xrad*2][zrad*2];
		
		int pX = playerChunk.getX();
		int pZ = playerChunk.getZ();

		int uX = pX + xrad;
		int uZ = pZ + zrad ;
		
		int lX = pX - xrad;
		int lZ = pZ - zrad;
		int xIndex = 0;
		int zIndex = 0;

		int overlapping = 0;
		
		for(int z = lZ; z < uZ; z++)
		{
			xIndex = 0;
			for(int x = lX; x <uX; x++)
			{
				claimMap[xIndex][zIndex] = new Vector2Integer(x,z);
    			if(getChunkOwnerCache(claimMap[xIndex][zIndex]) != null)
    			{
    				if(getChunkOwnerCache(claimMap[xIndex][zIndex]).getOwnerUUID().equals(playerData.getFaction().toString()))
    				{
						claimMap[xIndex][zIndex] = null;
						overlapping++;
						xIndex++;

						continue;
					}
					playerData.sendMessage(Lang.CLAIM_RADIUS_OVERLAPPING);
					return;
				}

				xIndex++;
			}
			zIndex++;
		}
		
		for(Vector2Integer[] vA : claimMap) {
			for(Vector2Integer v : vA)
			{
				if(v == null) continue;
				unclaimChunk(v, faction);
			}
		}
		
		playerData.sendMessage(Lang.SUCCESSFULL_UNCLAIM_AREA, (xrad*2)*(zrad*2) - overlapping + "");
	}

}
