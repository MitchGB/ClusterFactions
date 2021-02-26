package com.clusterfactions.clustercore.core.items.block.breakhandler;

import net.minecraft.server.v1_16_R3.BlockPosition;

public class BlockAnimationContext {

	private BlockPosition blockPos;
	private int stage = -1;

	public BlockAnimationContext(BlockPosition pos) {
		this.blockPos = pos;
	}

	public BlockPosition getBlockPos() {
		return blockPos;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}
}