package com.clusterfactions.clustercore.core.items;

import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;

public enum CustomItemType {

	TEST_ITEM,
	TEST_COOKABLE_ITEM,
	TEST_COOKED_ITEM,
	
	NICKEL_ORE,
	NICKEL_INGOT;
	
	public static CustomItemType getById(String id)
	{
		return valueOf(id.toUpperCase().replace("-", "_"));
	}
	
	public ItemStack getItem() {
		return ClusterCore.getInstance().getItemManager().getCustomItemHandler(this).getNewStack();
	}
	
	public String getId()
	{
		return name().toUpperCase();
	}	
	
}
