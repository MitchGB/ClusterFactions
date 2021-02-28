package com.clusterfactions.clustercore.core.items;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.crafting.RecipeIngredient;
import com.clusterfactions.clustercore.core.items.impl.TestItem;
import com.clusterfactions.clustercore.core.items.impl.ingot.NickelIngot;
import com.clusterfactions.clustercore.core.items.impl.ingot.TitaniumIngot;
import com.clusterfactions.clustercore.core.items.impl.ore.NickelOre;
import com.clusterfactions.clustercore.core.items.impl.ore.TitaniumOre;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.CraftableItem;
import com.clusterfactions.clustercore.util.ItemBuilder;

import de.tr7zw.changeme.nbtapi.NBTItem;

public class ItemManager implements Listener{

	private static Map<CustomItemType, CustomItem> customItems = new HashMap<>();
	private static Map<RecipeIngredient[][], CustomItemType> craftingMap = new HashMap<>();
	
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
		/*
		 * Register all instances that take paper
		 */
		int registered = 0;

		Bukkit.getConsoleSender().sendMessage("[ClusterCore] Registered " + registered + " custom recipes.");
		for(Entry<CustomItemType, CustomItem> entrySet : customItems.entrySet())
		{
			if(entrySet.getValue() instanceof CraftableItem) 
			{
				craftingMap.put(((CraftableItem)entrySet.getValue()).recipe(), entrySet.getKey());
				Bukkit.addRecipe(RecipeIngredient.fromMap(entrySet.getValue().getNewStack(), ((CraftableItem)entrySet.getValue()).recipe() ));
				registered++;
			}
		}
		Bukkit.getConsoleSender().sendMessage("[ClusterCore] Registered " + registered + " custom recipes.");
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
	
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        int xIndex = 0;
        int zIndex = 0;
        ItemStack[][] map = new ItemStack[3][3];
        
    	for (ItemStack itemStack : e.getInventory().getMatrix()) {
            if(xIndex == 3) {
            	xIndex = 0;
            	zIndex++;
            }
            map[zIndex][xIndex] = itemStack;
            xIndex++;
        }
    	if(ClusterCore.getInstance().getItemManager().isMaterialsApplicable(map)) {
    		if(!ClusterCore.getInstance().getItemManager().isRecipeApplicable(map))
    			e.getInventory().setResult(null);
    		else
    			e.getInventory().setResult(new ItemBuilder(Material.PAPER).coloredName("TEST").create());
    	}
    	
    }
}















