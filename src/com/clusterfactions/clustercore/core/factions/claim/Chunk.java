package com.clusterfactions.clustercore.core.factions.claim;

import java.util.UUID;

import com.clusterfactions.clustercore.persistence.serialization.UUIDSerializer;
import com.clusterfactions.clustercore.persistence.serialization.Vector2IntegerSerializer;
import com.clusterfactions.clustercore.util.annotation.AlternateSerializable;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

import lombok.Getter;

public class Chunk {
	@Getter @AlternateSerializable(Vector2IntegerSerializer.class) Vector2Integer location;
	@Getter @AlternateSerializable(UUIDSerializer.class) UUID factionUUID;
	
	public Chunk(Vector2Integer location, UUID factionUUID) {
		this.location = location;
		this.factionUUID = factionUUID;
	}
}
