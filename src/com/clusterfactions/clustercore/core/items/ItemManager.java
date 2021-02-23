package com.clusterfactions.clustercore.core.items;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.crafting.RecipeIngredient;
import com.clusterfactions.clustercore.core.items.impl.TestItem;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.CraftableItem;

import de.tr7zw.changeme.nbtapi.NBTItem;

public class ItemManager {

	private static Map<CustomItemType, CustomItem> customItems = new HashMap<>();
	private static Map<RecipeIngredient[][], CustomItemType> craftingMap = new HashMap<>();
	
	public ItemManager() {
		registerItemHandler(new TestItem());
		registerRecipes();
	}
	
	private void registerItemHandler(CustomItem itemHandler){
		customItems.put(itemHandler.getType(), itemHandler);
		ClusterCore.getInstance().registerListener(itemHandler);
	}
	
	public void registerRecipes() {
		for(Entry<CustomItemType, CustomItem> entrySet : customItems.entrySet())
		{
			if(entrySet.getValue() instanceof CraftableItem) 
			{
				craftingMap.put(((CraftableItem)entrySet.getValue()).recipe(), entrySet.getKey());
				Bukkit.addRecipe(RecipeIngredient.fromMap(entrySet.getValue().getNewStack(), ((CraftableItem)entrySet.getValue()).recipe() ));
			}
		}
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
	
	public ItemStack getRecipeMaterialOutput(ItemStack[][] map){
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
					if(!recipe[x][z].isMat(map[x][z]))
						return null;
				}

			return craftingMap.get(recipe).getItem();
		}
		return null;
	}
	
	public boolean isRecipeApplicable(ItemStack[][] map) {
		return getRecipeOutput(map) != null;
	}
	
	/*
	 * checks if materials within grid are applicable to recipe (DOES NOT INCLUDE CUSTOMITEMDATA)
	 */
	public boolean isMaterialsApplicable(ItemStack[][] map){
		return getRecipeMaterialOutput(map) != null;
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















