package com.clusterfactions.clustercore.core.items.block.breakhandler;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_16_R3.BlockPosition;

public class BlockAnimationContext {

	@Getter private BlockPosition blockPos;
	@Getter @Setter private int stage = -1;
	@Getter @Setter private int ticksTaken = 0;
	
	public BlockAnimationContext(BlockPosition pos) {
		this.blockPos = pos;
	}
}