package com.clusterfactions.clustercore.core.items;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.impl.TestItem;
import com.clusterfactions.clustercore.core.items.types.CustomItem;

import de.tr7zw.changeme.nbtapi.NBTItem;

public class ItemManager {

	private static Map<CustomItemType, CustomItem> customItems = new HashMap<>();
	
	private void registerItemHandler(CustomItem itemHandler)
	{
		customItems.put(itemHandler.getType(), itemHandler);
		ClusterCore.getInstance().registerListener(itemHandler);
	}
	
	public ItemManager() {
		registerItemHandler(new TestItem());
	}
	
	public CustomItem getCustomItemHandler(CustomItemType itemType)
	{
		return customItems.get(itemType);
	}
	
	public CustomItemType getCustomItemType(ItemStack itemStack)
	{
		if(itemStack == null) return null;

		NBTItem item = new NBTItem(itemStack);
		
		if(!item.hasKey(CustomItem.NBT_ITEM_TAG_TYPE)) return null;

		Integer customItemTypeOrdinal = item.getInteger(CustomItem.NBT_ITEM_TAG_TYPE);

		if(customItemTypeOrdinal > CustomItemType.values().length - 1) return null;

		return CustomItemType.values()[customItemTypeOrdinal];
	}
	
}















