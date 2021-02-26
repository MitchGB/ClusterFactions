package com.clusterfactions.clustercore.core.items.block.util;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_16_R3.Block;
import net.minecraft.server.v1_16_R3.BlockNote;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.BlockPropertyInstrument;
import net.minecraft.server.v1_16_R3.Blocks;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.IBlockData;
import net.minecraft.server.v1_16_R3.World;

public class SolidCustomBlock extends Block implements ICustomBlock {

	private final IBlockData blockData;

	public SolidCustomBlock(Info blockbase_info, BlockPropertyInstrument instrument, int note, boolean powered) {
		super(blockbase_info);

		this.blockData = Blocks.NOTE_BLOCK.getBlockData().set(BlockNote.INSTRUMENT, instrument)
				.set(BlockNote.NOTE, note).set(BlockNote.POWERED, powered);

		REGISTRY_ID.a(this.getBlockData(), REGISTRY_ID.getId(blockData));
	}

	@Override
	public IBlockData getCustomBlockData() {
		return blockData;
	}

	@Override
	public void stepOn(World world, BlockPosition blockposition, Entity entity) {
		if (entity instanceof EntityLiving) {
			LivingEntity livingEntity = (LivingEntity) entity.getBukkitEntity();
			livingEntity.setVelocity(new Vector(0, 1, 0));
		}
	}
	

}