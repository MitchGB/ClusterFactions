package com.clusterfactions.clustercore.core.items.types.interfaces;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public interface InteractableItem{

	@EventHandler
	public default void interact(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)
        {
        	rightClick(e);
        }
        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR)
        {
        	leftClick(e);
        }
	}
	
	public abstract void leftClick(PlayerInteractEvent e);
	public abstract void rightClick(PlayerInteractEvent e);
	
}
