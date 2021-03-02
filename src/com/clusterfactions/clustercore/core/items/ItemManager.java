package com.clusterfactions.clustercore.core.items;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.crafting.RecipeIngredient;
import com.clusterfactions.clustercore.core.items.impl.TestItem;
import com.clusterfactions.clustercore.core.items.impl.ingot.NickelIngot;
import com.clusterfactions.clustercore.core.items.impl.ingot.TitaniumIngot;
import com.clusterfactions.clustercore.core.items.impl.ore.NickelOre;
import com.clusterfactions.clustercore.core.items.impl.ore.TitaniumOre;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.CraftableItem;

import de.tr7zw.changeme.nbtapi.NBTItem;

public class ItemManager implements Listener{

	private static LinkedHashMap<CustomItemType, CustomItem> customItems = new LinkedHashMap<>();
	private static LinkedHashMap<RecipeIngredient[][], CustomItemType> craftingMap = new LinkedHashMap<>();
	
	public ItemManager() {
		registerItemHandler(
				new TestItem(),
				
				new NickelIngot(),
				new NickelOre(),
				
				new TitaniumIngot(),
				new TitaniumOre()
				);
		registerRecipes();
		ClusterCore.getInstance().registerListener(this);
	}
	
	@SuppressWarnings("unused")
	private void registerItemHandler(CustomItem itemHandler){
		customItems.put(itemHandler.getType(), itemHandler);
		ClusterCore.getInstance().registerListener(itemHandler);
	}
	
	private void registerItemHandler(CustomItem... itemHandlers){
		for(CustomItem itemHandler : itemHandlers) {
			customItems.put(itemHandler.getType(), itemHandler);
			ClusterCore.getInstance().registerListener(itemHandler);
		}
	}
	
	public void registerRecipes() {
		int registered = 0;

		for(Entry<CustomItemType, CustomItem> entrySet : customItems.entrySet())
		{
			if(entrySet.getValue() instanceof CraftableItem) 
			{
				craftingMap.put(((CraftableItem)entrySet.getValue()).recipe(), entrySet.getKey());
				registered++;
			}
		}
		Bukkit.getConsoleSender().sendMessage("[ClusterFactions] Registered " + registered + " custom recipes.");
	}
	
	public ItemStack getRecipeOutput(ItemStack[][] map){
		for(RecipeIngredient[][] recipe : craftingMap.keySet())
		{
			for(int x = 0; x < 3; x++) 
				for(int z = 0; z < 3; z++)
				{
					if(recipe[x][z] == null && map[x][z] != null)
						return null;
					if(recipe[x][z] == null) 
						continue;
					if(map[x][z] == null) 
						return null;
					if(!recipe[x][z].isApplicable(map[x][z]))
						return null;
				}

			return craftingMap.get(recipe).getItem();
		}
		return null;
	}
	
	public boolean isRecipeApplicable(ItemStack[][] map) {
		return getRecipeOutput(map) != null;
	}
	
	public CustomItem getCustomItemHandler(CustomItemType itemType)
	{
		if(itemType == null) return null;
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















