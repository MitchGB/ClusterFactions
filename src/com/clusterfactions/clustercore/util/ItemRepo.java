package com.clusterfactions.clustercore.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ItemRepo {
		
	GUI_BASE(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100000).create());
	
	private ItemRepo(ItemStack mat)
	{
		this.mat = mat;
	}
	
	public ItemStack mat;
	
}
