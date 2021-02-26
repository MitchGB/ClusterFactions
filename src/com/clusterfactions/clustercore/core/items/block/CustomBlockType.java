package com.clusterfactions.clustercore.core.items.block;

import org.bukkit.Instrument;
import org.bukkit.Sound;

import com.clusterfactions.clustercore.core.items.CustomItemType;

import lombok.Getter;

public enum CustomBlockType {

	/*
	 * Custom blocks achieved with noteblocks
	 */
	NICKEL_ORE(Instrument.BANJO, 1, Sound.BLOCK_STONE_BREAK, e -> { return CustomItemType.NICKEL_ORE.getItem();}, 2000);
	
	@Getter Instrument instrument;
	@Getter int note;
	@Getter Sound blockSound;
	@Getter BlockBreakOutputHandler consumer;
	@Getter int breakDuration;
	
	CustomBlockType(Instrument instrument, int note, Sound blockSound, int breakDuration){
		this.instrument = instrument;
		this.note = note;
		this.blockSound = blockSound;
		this.breakDuration = breakDuration;
	}
	
	CustomBlockType(Instrument instrument, int note, Sound blockSound, BlockBreakOutputHandler consumer, int breakDuration){
		this.instrument = instrument;
		this.note = note;
		this.blockSound = blockSound;
		this.consumer = consumer;
		this.breakDuration = breakDuration;
	}
	
	public static CustomBlockType getType(Instrument instrument, int note) {
		for(CustomBlockType type : CustomBlockType.values())
			if(type.instrument == instrument && type.note == note)
				return type;
		return null;
	}
}
