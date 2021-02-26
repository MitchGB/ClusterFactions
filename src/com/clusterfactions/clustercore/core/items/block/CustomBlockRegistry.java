package com.clusterfactions.clustercore.core.items.block;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.block.util.SolidCustomBlock;

import net.minecraft.server.v1_16_R3.BlockBase.Info;
import net.minecraft.server.v1_16_R3.BlockPropertyInstrument;
import net.minecraft.server.v1_16_R3.IRegistry;
import net.minecraft.server.v1_16_R3.MaterialMapColor;

public class CustomBlockRegistry {
	private Field BLOCK_MATERIAL_MAP;

	@SuppressWarnings("unchecked")
	public void registerBlock(net.minecraft.server.v1_16_R3.Block block, String name) {
		IRegistry.a(IRegistry.BLOCK, "fakeblocks:" + name, block);
		try {
			((Map<net.minecraft.server.v1_16_R3.Block, net.minecraft.server.v1_16_R3.Material>) BLOCK_MATERIAL_MAP.get(null)).put(block, net.minecraft.server.v1_16_R3.Material.STONE);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public CustomBlockRegistry() {
		try {
			BLOCK_MATERIAL_MAP = CraftMagicNumbers.class.getDeclaredField("BLOCK_MATERIAL");
			BLOCK_MATERIAL_MAP.setAccessible(true);
		} catch (ReflectiveOperationException ex) {
			Bukkit.getPluginManager().disablePlugin(ClusterCore.getInstance());
		}
		
		registerBlock(new SolidCustomBlock(Info.a(net.minecraft.server.v1_16_R3.Material.STONE, MaterialMapColor.m).h().a(1.5F, 6.0F),BlockPropertyInstrument.DIDGERIDOO, 17, true), "test");
		registerBlock(new SolidCustomBlock(Info.a(net.minecraft.server.v1_16_R3.Material.STONE, MaterialMapColor.m).h().a(1.5F, 6.0F),BlockPropertyInstrument.BANJO, 1, false), "nickel_ore");

	}
	
}
