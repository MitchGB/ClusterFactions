package com.clusterfactions.clustercore.core.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.util.ItemBuilder;

public enum ItemRepo {
		
	GUI_BASE(100000, Material.PAPER),
	GUI_ARROW_LEFT(120000, Material.PAPER),
	GUI_ARROW_RIGHT(121000, Material.PAPER),
	
	NICKEL_ORE(100001, Material.PAPER),
	NICKEL_INGOT(100012, Material.PAPER),
	
	TITANIUM_ORE(100002, Material.PAPER),
	TITANIUM_INGOT(100012, Material.PAPER);
	
	private ItemRepo(int customModelData, Material mat)
	{
		this.customModelData = customModelData;
		this.mat = mat;
	}
	
	public ItemStack getItem() {
		return new ItemBuilder(mat).setCustomModelData(customModelData).create();
	}
	
	public int customModelData;
	public Material mat;
	
}
