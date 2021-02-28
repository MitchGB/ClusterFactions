package com.clusterfactions.clustercore.core.items.impl;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.crafting.RecipeIngredient;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.CraftableItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.InteractableItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.StackableItem;
import com.clusterfactions.clustercore.util.ItemBuilder;

public class TestItem extends CustomItem implements InteractableItem, StackableItem, CraftableItem{

	public TestItem() {
		super(CustomItemType.TEST_ITEM, new ItemBuilder(Material.ACACIA_BUTTON).create());
		
	}

	@Override
	public void leftClick(PlayerInteractEvent e) {
		
	}

	@Override
	public void rightClick(PlayerInteractEvent e) {
		if(!isApplicableItem(e.getItem())) return;
	
	}

	@Override
	public RecipeIngredient[][] recipe() {
		return RecipeIngredient.getMap(new RecipeIngredient(Material.PAPER));
	}

}




