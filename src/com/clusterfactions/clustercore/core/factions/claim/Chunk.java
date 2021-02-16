package com.clusterfactions.clustercore.core.factions.claim;

import java.util.UUID;

import com.clusterfactions.clustercore.persistence.serialization.ByteListSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDSerializer;
import com.clusterfactions.clustercore.persistence.serialization.Vector2IntegerSerializer;
import com.clusterfactions.clustercore.util.annotation.AlternateSerializable;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class Chunk {
	@Getter @AlternateSerializable(Vector2IntegerSerializer.class) Vector2Integer location;
	@Getter @AlternateSerializable(UUIDSerializer.class) UUID factionUUID;
	@Getter @Setter @AlternateSerializable(ByteListSerializer.class)byte[][] colorMap;
	
	public Chunk(Vector2Integer location, UUID factionUUID, byte[][] colorMap) {
		this.location = location;
		this.factionUUID = factionUUID;
		this.colorMap = colorMap;
	}
}
