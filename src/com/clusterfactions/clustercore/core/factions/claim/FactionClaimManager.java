package com.clusterfactions.clustercore.core.factions.claim;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.lang.Lang_EN_US;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.listeners.events.claim.ClaimEnterEvent;
import com.clusterfactions.clustercore.listeners.events.claim.ClaimExitEvent;
import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

public class FactionClaimManager implements Listener{
	
	/*
	 * North -z
	 * East +x
	 * South +z
	 * West -x
	 */

	public HashMap<UUID, UUID> playerCache = new HashMap<>(); //PlayerUUID, factionUUID (faction of claim they are in, null if none);
	private HashMap<Vector2Integer, UUID> chunkCache = new HashMap<>();
	
	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		UUID faction = chunkClaimedCache(getChunkVector(player.getLocation()));
		if(!playerCache.containsKey(player.getUniqueId()) && faction != null)
		{
			playerCache.put(player.getUniqueId(), faction);
			Bukkit.getPluginManager().callEvent(new ClaimEnterEvent(player, faction));
			return;
		}
		if(playerCache.containsKey(player.getUniqueId()) && faction == null) {
			playerCache.remove(player.getUniqueId());
			Bukkit.getPluginManager().callEvent(new ClaimExitEvent(player));
			return;
		}
		if(playerCache.containsKey(player.getUniqueId()) && !playerCache.get(player.getUniqueId()).equals(faction))		
		{
			playerCache.replace(player.getUniqueId(), faction);
			Bukkit.getPluginManager().callEvent(new ClaimEnterEvent(player, faction));
			return;
		}
		
	}
	
	@EventHandler
	public void ClaimExitEvent(ClaimExitEvent e) {
		Player player = e.getPlayer();
		player.sendTitle(Colors.parseColors("&2&lWilderness"), Colors.parseColors("&7You have entered unclaimed territory"), 5, 40, 5);
	}
	
	@EventHandler
	public void ClaimEnterEvent(ClaimEnterEvent e) {
		Player player = e.getPlayer();
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(e.getFaction());
		player.sendTitle(Colors.parseColors(faction.getFactionName()), Colors.parseColors(""), 5, 40, 5);
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
	
	public UUID getChunkMongo(Vector2Integer chunkLoc) {

		String ret = ClusterCore.getInstance().getMongoHook().getObject(chunkLoc.toString(), "owner", "chunks");
		chunkCache.put(chunkLoc, ret == null || ret.isEmpty() ? null : UUID.fromString(ret));
		return chunkCache.get(chunkLoc);	
	}
	
	public UUID chunkClaimed(Vector2Integer chunkLoc)
	{
		String c =ClusterCore.getInstance().getMongoHook().getObject(chunkLoc.toString(), "owner", "chunks");
		chunkCache.put(chunkLoc, c == null || c.isEmpty() ? null : UUID.fromString(c));
		return chunkCache.get(chunkLoc);	
	}
	
	public UUID chunkClaimedCache(Vector2Integer chunkLoc)
	{
		if(chunkCache.containsKey(chunkLoc)) return chunkCache.get(chunkLoc);
		String ret = ClusterCore.getInstance().getMongoHook().getObject(chunkLoc.toString(), "owner", "chunks");
		chunkCache.put(chunkLoc, ret == null || ret.isEmpty() ? null : UUID.fromString(ret));
		return chunkCache.get(chunkLoc);	
	}
	
	
	public void claimArea(Player player, int xrad, int zrad) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(ClusterCore.getInstance(), new Runnable() {
	        @Override
	        public void run() {
	        	
	        	PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
	        	Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
	        	Vector2Integer playerChunk = getChunkVector(player.getLocation());
		
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
	        			if(chunkClaimed(claimMap[xIndex][zIndex]) != null)
	        			{
	        				if(chunkClaimed(claimMap[xIndex][zIndex]).equals(playerData.getFaction()))
	        				{
	        					claimMap[xIndex][zIndex] = null;
	        					overlapping++;
	        					xIndex++;

	        					continue;
	        				}
	        				playerData.sendMessage(Lang_EN_US.CLAIM_RADIUS_OVERLAPPING);
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
	        			claimChunk(v, faction);
	        		}
	        	}
		
	        	playerData.sendMessage(Lang_EN_US.SUCCESSFULL_CLAIM_AREA, (xrad*2)*(zrad*2) - overlapping + "", overlapping + "");
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
				if(chunkClaimed(claimMap[xIndex][zIndex]) != null)
				{
					if(chunkClaimed(claimMap[xIndex][zIndex]).equals(playerData.getFaction()))
					{
						claimMap[xIndex][zIndex] = null;
						overlapping++;
						xIndex++;

						continue;
					}
					playerData.sendMessage(Lang_EN_US.CLAIM_RADIUS_OVERLAPPING);
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
				removeClaimChunk(v, faction);
			}
		}
		
		playerData.sendMessage(Lang_EN_US.SUCCESSFULL_UNCLAIM_AREA, (xrad*2)*(zrad*2) - overlapping + "");
	}
	
	public void claimChunk(Vector2Integer chunkLoc, Faction faction) {
		ClusterCore.getInstance().getMongoHook().saveObject(chunkLoc.toString(), "owner", faction.getFactionID().toString(), "chunks");
		if(chunkCache.containsKey(chunkLoc))chunkCache.remove(chunkLoc);
		chunkCache.put(chunkLoc, getChunkMongo(chunkLoc));
		faction.addClaimChunk(chunkLoc);
	}
	
	public void removeClaimChunk(Vector2Integer chunkLoc, Faction faction) {	
		if(chunkCache.containsKey(chunkLoc))chunkCache.remove(chunkLoc);
		ClusterCore.getInstance().getMongoHook().saveObject(chunkLoc.toString(), "owner", "", "chunks");
		chunkCache.put(chunkLoc, getChunkMongo(chunkLoc));
		faction.removeClaimChunk(chunkLoc);
	}
}
