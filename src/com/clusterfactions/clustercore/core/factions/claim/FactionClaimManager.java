package com.clusterfactions.clustercore.core.factions.claim;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.lang.Lang_EN_US;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

public class FactionClaimManager {
	
	/*
	 * North -z
	 * East +x
	 * South +z
	 * West -x
	 */
	
	public Vector2Integer getChunkVector(Location loc) {
		return new Vector2Integer((int)Math.ceil(loc.getX()/16), (int)Math.ceil(loc.getZ()/16));
	}
	
	public UUID chunkClaimed(Vector2Integer chunkLoc)
	{
		String ret = ClusterCore.getInstance().getMongoHook().getObject(chunkLoc.toString(), "owner", "chunks");
		return ret == null || ret.isEmpty() ? null : UUID.fromString(ret);	
	}
	
	public void claimArea(Player player, int xrad, int zrad) {
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
				claimChunk(v, faction);
			}
		}
		
		playerData.sendMessage(Lang_EN_US.SUCCESSFULL_CLAIM_AREA, (xrad*2)*(zrad*2) - overlapping + "", overlapping + "");
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
		faction.addClaimChunk(chunkLoc);
	}
	
	public void removeClaimChunk(Vector2Integer chunkLoc, Faction faction) {
		ClusterCore.getInstance().getMongoHook().saveObject(chunkLoc.toString(), "owner", "", "chunks");
		faction.removeClaimChunk(chunkLoc);
	}
}
