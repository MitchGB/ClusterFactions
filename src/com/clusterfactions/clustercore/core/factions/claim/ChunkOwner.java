package com.clusterfactions.clustercore.core.factions.claim;

import java.util.UUID;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;

import lombok.Getter;

public class ChunkOwner {
	@Getter private boolean isAdmin = false;
	@Getter private String ownerUUID;
	
	public ChunkOwner(String ownerUUID, boolean isAdmin) {
		this.isAdmin = isAdmin;
		this.ownerUUID = ownerUUID;
	}
	
	public ChunkOwner(String ownerUUID) {
		this.isAdmin = false;
		this.ownerUUID = ownerUUID;
	}
	
	public boolean isNull() {
		return ownerUUID == null || ownerUUID.isEmpty();
	}
	
	public Faction getFaction() {
		return ClusterCore.getInstance().getFactionsManager().getFaction(UUID.fromString(ownerUUID));
	}
}
