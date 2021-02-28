package com.clusterfactions.clustercore.core.items;

import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.types.CustomItem;

public enum CustomItemType {

	TEST_ITEM,
	TEST_COOKABLE_ITEM,
	TEST_COOKED_ITEM,
	
	NICKEL_ORE,
	NICKEL_INGOT,
	
	TITANIUM_ORE,
	TITANIUM_INGOT;
	
	public static CustomItemType getById(String id)
	{
		return valueOf(id.toUpperCase().replace("-", "_"));
	}
	
	public ItemStack getItem() {
		return ClusterCore.getInstance().getItemManager().getCustomItemHandler(this).getNewStack();
	}
	
	public CustomItem getHandler() {
		return ClusterCore.getInstance().getItemManager().getCustomItemHandler(this);
	}
	
	public String getId()
	{
		return name().toUpperCase();
	}	
	
}
