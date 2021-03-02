package com.clusterfactions.clustercore.core.inventory.util.model;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.clusterfactions.clustercore.ClusterCore;


public abstract class BlockInventoryBase extends InventoryBase{

	protected Block block;
	
	public BlockInventoryBase(Player player, String ID, String displayName, int inventorySize, Block block) {
		super(player, ID, displayName, inventorySize);
		
		ClusterCore.getInstance().getInventoryManager().blockCache.put(block, this);		
		this.block = block;
	}

	@Override
	public void closeInventoryEvent(InventoryCloseEvent e) {
		handlers.remove(e.getPlayer().getUniqueId());
	}
	
	public void blockBreakEvent(BlockBreakEvent e) {
		ClusterCore.getInstance().getInventoryManager().blockCache.remove(block);
		for(UUID p : handlers){
			Bukkit.getPlayer(p).closeInventory();
		}
	}
}