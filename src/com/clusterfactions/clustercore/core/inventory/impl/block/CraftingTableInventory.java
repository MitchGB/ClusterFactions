package com.clusterfactions.clustercore.core.inventory.impl.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.model.BlockInventoryBase;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.InteractableSlots;
import com.clusterfactions.clustercore.core.items.ItemManager;
import com.clusterfactions.clustercore.core.items.crafting.RecipeIngredient;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

public class CraftingTableInventory extends BlockInventoryBase implements InteractableSlots{

	public static final Integer[] interactableSlots = new Integer[] {1, 2, 3, 10, 11, 12, 19, 20, 21};
	public static final Integer outputSlot = 15;
	
	public CraftingTableInventory(Player player, Block block) {
		super(player, "CRAFTING_TABLE_OVERRIDE_MENU", "&f" + CharRepo.CRAFTING_TABLE_OVERRIDE_CONTAINER_36, 36, block);
	}
	
	@Override
	public void inventoryClickEvent(InventoryClickEvent e) {
		if(e.getSlot() == outputSlot) outputClickedEvent(e);
		evaluateMatrix();
	}
	
	@Override
	public void inventoryDragEvent(InventoryDragEvent e) {
		evaluateMatrix();
	}
	
	@Override
	public void closeInventoryEvent(InventoryCloseEvent e) {
		handlers.remove(e.getPlayer().getUniqueId());
		for(ItemStack item : invInstance.getContents())
			if(item != null)
				e.getPlayer().getInventory().addItem(item);
	}
	
	public void evaluateMatrix() {
		Bukkit.getScheduler().runTaskLater(ClusterCore.getInstance(), new Runnable() {
	        @Override
	        public void run() {
	    		ItemStack[][] recipeMap = RecipeIngredient.mapFromSlots(invInstance, interactableSlots);
	    		ItemManager manager = ClusterCore.getInstance().getItemManager();
	    		ItemStack output = manager.getRecipeOutput(recipeMap);

	    		if(output == null)
	    			output = getBukkitRecipeFromMatrix(interactableSlots);
	    		
	    		invInstance.setItem(outputSlot, output);
	    		updateAllHandlers();
	        }
		}, 1);
	}
	
	public void outputClickedEvent(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if(item == null) return;
		if(e.getClickedInventory().getType() == InventoryType.PLAYER) return;
		if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
			int amount = 64;
			for(Integer i : interactableSlots) {
				if(invInstance.getItem(i) == null || invInstance.getItem(i).getType() == Material.AIR) continue;
				if(invInstance.getItem(i).getAmount() < amount) amount = invInstance.getItem(i).getAmount();
			}
			
			ItemStack addItem = item.clone();
			addItem.setAmount(amount * addItem.getAmount());
			e.getWhoClicked().getInventory().addItem(addItem);
			for(Integer i : interactableSlots) {
				if(invInstance.getItem(i) == null || invInstance.getItem(i).getType() == Material.AIR) continue;
				invInstance.getItem(i).add(-amount);
			}

			invInstance.setItem(e.getSlot(), null);
			return;
		}
		if(e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT) {

			if(e.getCursor() != null && e.getCursor().getType() != Material.AIR) return;
			invInstance.setItem(e.getSlot(), null);
			e.getWhoClicked().setItemOnCursor(item);
			for(Integer i : interactableSlots) {
				if(invInstance.getItem(i) == null || invInstance.getItem(i).getType() == Material.AIR) continue;
				invInstance.getItem(i).add(-1);
			}
			return;
		}
	}
	

	@Override
	public List<Integer> interactableSlots() {
		return Arrays.asList(interactableSlots);
	}
	
	private Character getMatrixChar(Map<Character,ItemStack> ingredientMap, ItemStack item) {
		if(item != null && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() != 0) return '!';
		for(Entry<Character,ItemStack> map : ingredientMap.entrySet()) {
			
			if(map.getValue() == null && item == null) {
				Character c = map.getKey();
				ingredientMap.remove(map.getKey());
				return c;
			}
			if(map.getValue() == null) continue;
			if(item == null) continue;
			if(map.getValue().getType().equals(item.getType())) {
				Character c = map.getKey();
				ingredientMap.remove(map.getKey());
				return c;
			}
		}
		if(item == null || item.getType() == Material.AIR)
			return null;
		else
			return '?';
	}
	
	
	private boolean isSame(Object s1, Object s2) {
		if(s1 == null && s2 == null) return true;
		if(s1 == null && s2 != null) return false;
		if(s1 != null && s2 == null) return false;
		if(s1.equals(s2)) return true;
		return false;
	}
	
	private boolean stringMatrixMatches(String[] bukkitMap, String[] matrixMap) {
		for(int i = 0; i < 3; i++) {
			if(!isSame(bukkitMap[i],(matrixMap[i])))
				return false;
		}
		return true;
	}
	
	private static String[] forceFitMap(String[] map, int size) {
		String[] newMap = new String[size];
		int iterator = 0;
		for(String str : map){
			newMap[iterator] = str;
			iterator++;
		}
		return newMap;
	}
	
	public ItemStack getBukkitRecipeFromMatrix(Integer... matrix) {
		Iterator<Recipe> recipeIterator = Bukkit.getServer().recipeIterator();

		first:
		while(recipeIterator.hasNext()) {
			Recipe recipe = recipeIterator.next();
			if(recipe instanceof ShapedRecipe) {
				Map<Character, ItemStack> ingredientMap = ((ShapedRecipe) recipe).getIngredientMap();
				ingredientMap = new HashMap<>(ingredientMap);

			    String[] map = new String[3];
			    int index = 0;
			    for(int x = 0; x < 3; x++) {
			    	StringBuilder sb = new StringBuilder();
			    	for(int y = 0; y < 3; y++) {

				    	ItemStack matrixItem = invInstance.getItem(matrix[index]);
				    	Character c = getMatrixChar(ingredientMap, matrixItem);
				    	sb.append(c == null ? ' ' : c);
			    		index++;
			    	}
			    	String toAppend = sb.toString().replaceAll("\\s+$", "");
			    	map[x] = toAppend.isEmpty() ? null : toAppend;
			    }
		        if(stringMatrixMatches( forceFitMap(((ShapedRecipe) recipe).getShape(), 3), map)) {
		        	return recipe.getResult();
		        }
			}
			if(recipe instanceof ShapelessRecipe) {
				ShapelessRecipe shapeless = (ShapelessRecipe)recipe;
				List<Material> matList = new ArrayList<>();
				for(ItemStack item : shapeless.getIngredientList())
					matList.add(item.getType());
				int contains = 0;
				second:
				for(Integer i : matrix) {
					ItemStack it = invInstance.getItem(i);
					if(it == null) continue second;
					if(it.getItemMeta().hasCustomModelData() && it.getItemMeta().getCustomModelData() != 0) continue first;
					if(!matList.contains(it.getType())) continue first;
					
					contains++;
				}
				if(contains == matList.size()) {
					return recipe.getResult();
				}
			}
		}
		return null;
	}

}





















