package com.clusterfactions.clustercore.core.inventory.util;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

public class InventoryClickContext {
	@Getter private final Player player;
	@Getter private final ItemStack item;
	@Getter private final boolean isRight;
	@Getter private final InventoryClickEvent eventRaw;
	
	public InventoryClickContext(Player player, ItemStack item, boolean isRight, InventoryClickEvent eventRaw)
	{
		this.player = player;
		this.item = item;
		this.isRight = isRight;
		this.eventRaw = eventRaw;
	}
}
