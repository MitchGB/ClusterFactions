package com.clusterfactions.clustercore.core.items.types.interfaces;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.block.CustomBlockType;

public interface PlaceableItem {
	public CustomBlockType blockType();
	
	@EventHandler
	public default void interact(PlayerInteractEvent e) {
		ClusterCore.getInstance().getCustomBlockManager().placeBlock(e, e.getItem());
	}
}
