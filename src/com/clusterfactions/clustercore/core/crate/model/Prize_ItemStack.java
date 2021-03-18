package com.clusterfactions.clustercore.core.crate.model;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Prize_ItemStack extends Prize {

	ItemStack item;
	
	public Prize_ItemStack(String displayName, ItemStack item, String... desc) {
		super(displayName, desc);
		this.item = item;
	}

	@Override
	public void output(Player player) {
		player.getInventory().addItem(item);
	}

	@Override
	public ItemStack displayItem() {
		return item;
	}

}