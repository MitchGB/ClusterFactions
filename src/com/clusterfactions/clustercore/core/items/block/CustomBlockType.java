package com.clusterfactions.clustercore.core.items.block;

import java.util.Arrays;

import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.block.util.ItemGroupGenerics;
import com.clusterfactions.clustercore.util.model.Pair;

import lombok.Getter;

public enum CustomBlockType {
	/*
	 * Custom blocks achieved with noteblocks
	 */
	NICKEL_ORE(Instrument.BANJO, 1, Sound.BLOCK_STONE_BREAK, Material.STONE, 
			e -> { 
				if(Arrays.asList(ItemGroupGenerics.pickaxes).contains(e.getInventory().getItemInMainHand().getType()))
					return CustomItemType.NICKEL_ORE.getItem();
				return null;	
			}, 
			new blockBreakingAttributes(150,
					Pair.of(Material.WOODEN_PICKAXE, 23), 
					Pair.of(Material.STONE_PICKAXE, 12), 
					Pair.of(Material.IRON_PICKAXE, 8), 
					Pair.of(Material.DIAMOND_PICKAXE, 6), 
					Pair.of(Material.NETHERITE_PICKAXE, 5), 
					Pair.of(Material.GOLDEN_PICKAXE, 4)) ),
	TITANIUM_ORE(Instrument.BANJO, 10, Sound.BLOCK_STONE_BREAK, Material.STONE, 
			e -> { 
				if(Arrays.asList(ItemGroupGenerics.pickaxes).contains(e.getInventory().getItemInMainHand().getType()))
					return CustomItemType.TITANIUM_ORE.getItem();
				return null;			
			}, 
			new blockBreakingAttributes(150,
					Pair.of(Material.WOODEN_PICKAXE, 46), 
					Pair.of(Material.STONE_PICKAXE, 24), 
					Pair.of(Material.IRON_PICKAXE, 16), 
					Pair.of(Material.DIAMOND_PICKAXE, 12), 
					Pair.of(Material.NETHERITE_PICKAXE, 10), 
					Pair.of(Material.GOLDEN_PICKAXE, 8)) );
	
	
	
	@Getter Instrument instrument;
	@Getter int note;
	@Getter Sound blockSound;
	@Getter BlockBreakOutputHandler consumer;
	@Getter blockBreakingAttributes attributes;
	@Getter Material blockBreakParticle;
	
	public static class blockBreakingAttributes {
		@Getter int defaultBreakSpeed;
		@Getter Pair<Material, Integer>[] materialData;
		
		@SafeVarargs
		public blockBreakingAttributes(int defaultBreakSpeed, Pair<Material,Integer>... materialData) {
			this.defaultBreakSpeed = defaultBreakSpeed;
			this.materialData = materialData;
		}
		
		public Integer getBreakSpeed(ItemStack item) {
			if(item == null || item.getType() == Material.AIR) return defaultBreakSpeed;
			return getBreakSpeed(item.getType());
		}
		
		public Integer getBreakSpeed(Material mat) {
			for(Pair<Material,Integer> p : materialData) {
				if(p.getLeft() == mat)
					return p.getRight();
				
			}
			return defaultBreakSpeed;
		}
	}
	
	CustomBlockType(Instrument instrument, int note, Sound blockSound, Material blockBreakParticle, BlockBreakOutputHandler consumer, blockBreakingAttributes attributes){
		this.instrument = instrument;
		this.note = note;
		this.blockSound = blockSound;
		this.consumer = consumer;
		this.attributes = attributes;
		this.blockBreakParticle = blockBreakParticle;
	}
	
	public static CustomBlockType getType(Block block) {
		if(block.getType() != Material.NOTE_BLOCK) return null;
		NoteBlock nb = (NoteBlock) block.getBlockData();
		return getType(nb.getInstrument(), Byte.toUnsignedInt(nb.getNote().getId()));
	}
	
	public static CustomBlockType getType(Instrument instrument, int note) {
		for(CustomBlockType type : CustomBlockType.values())
			if(type.instrument == instrument && type.note == note)
				return type;
		return null;
	}
}





















