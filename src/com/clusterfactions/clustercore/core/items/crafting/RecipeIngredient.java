package com.clusterfactions.clustercore.core.items.crafting;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.types.CustomItem;

import lombok.Getter;

public class RecipeIngredient {
	
	@Getter private int modelData;
	@Getter private Material mat;
	
	public RecipeIngredient(Material mat, int modelData)
	{
		this.mat = mat;
		this.modelData = modelData;
	}
	
	public RecipeIngredient(CustomItemType item) {
		this(item.getHandler());
	}
	
	public RecipeIngredient(CustomItem item) {
		this.mat = item.getItemType();
		this.modelData = item.getCustomModelData();
	}
	
	public RecipeIngredient(Material mat) {
		this.mat = mat;
		this.modelData = 0;
	}
	
	public boolean isMat(ItemStack stack) {
		return mat == stack.getType();
	}
	
	public boolean isApplicable(ItemStack stack) {
		if(stack.getType() != mat) return false;
		ItemMeta meta = stack.getItemMeta();
		
		int model = meta.hasCustomModelData() ? meta.getCustomModelData() : 0;
		if(modelData == model) return true;
		return false;
	}
	
	public static RecipeIngredient[][] getMap(RecipeIngredient... ingredients){
		if(ingredients.length > 9) return null;
		if(ingredients.length == 0) return null;
		RecipeIngredient[][] ret = new RecipeIngredient[3][3];
		for(int x = 0; x < 3; x++)
		{
			for(int z = 0; z < 3; z++)
			{
				int index = x * 3 + z;
				if(ingredients.length <= index) continue;
				ret[x][z] = ingredients[index];
			}
		}
		return ret;
	}
	
	public static ShapedRecipe fromMap(ItemStack output, RecipeIngredient[][] map) {
		NamespacedKey key = new NamespacedKey(ClusterCore.getInstance(), UUID.randomUUID().toString());
		ShapedRecipe recipe = new ShapedRecipe(key, output);

		char[] chars = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
		
		recipe.shape("ABC", "DEF", "GHI");
		int index = 0;
		for(int x = 0; x < 3; x++) {
			for(int z = 0; z < 3; z++)
			{
				if(map[x][z] != null)
				recipe.setIngredient(chars[index], map[x][z].getMat());
				index++;
			}
		}
		
		return recipe;
	}
}


























