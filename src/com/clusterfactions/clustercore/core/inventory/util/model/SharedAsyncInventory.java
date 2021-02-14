package com.clusterfactions.clustercore.core.inventory.util.model;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class SharedAsyncInventory extends InventoryBase{

	private Player[] playerHandlers;
	private Inventory inv;
	
	public SharedAsyncInventory(String ID, String displayName, int inventorySize, Player... handlers) {
		super(null, ID, displayName, inventorySize);
		this.playerHandlers = handlers;
	}
	
	public void updateHandlers() {
		for(Player player : playerHandlers) {
			player.closeInventory(Reason.OPEN_NEW);
			player.openInventory(getInventory(player));
		}
	}

	@Override
	public Inventory getInventory(Player player) {
		if(inv == null)
		{
			inv = Bukkit.createInventory(null, inventorySize, displayName);
		
			itemAddList.forEach(item -> inv.addItem(item));
		
			for(Entry<Integer, ItemStack> e : itemList.entrySet())
				inv.setItem(e.getKey(), e.getValue());
			return inv;
		}
		else
		{
			inv.clear();
			itemAddList.forEach(item -> inv.addItem(item));
			
			for(Entry<Integer, ItemStack> e : itemList.entrySet())
				inv.setItem(e.getKey(), e.getValue());
			return inv;
		}
	}

	
}
