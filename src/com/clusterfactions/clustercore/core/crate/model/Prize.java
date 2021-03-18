package com.clusterfactions.clustercore.core.crate.model;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Prize {
	public String displayName;
	public String[] desc;
	
	public Prize(String displayName, String... desc) {
		this.displayName = displayName;
		this.desc = desc;
	}
	
	public abstract void output(Player player);
	public abstract ItemStack displayItem();
}