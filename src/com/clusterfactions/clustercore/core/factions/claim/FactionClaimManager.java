package com.clusterfactions.clustercore.core.factions.claim;

import java.util.UUID;

import org.bukkit.Location;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
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
		return ret == null ? null : UUID.fromString(ret);	
	}
	
	public void claimChunk(Vector2Integer chunkLoc, Faction faction) {
		ClusterCore.getInstance().getMongoHook().saveObject(chunkLoc.toString(), "owner", faction.getFactionID().toString(), "chunks");
		faction.addClaimChunk(chunkLoc);
	}
}
