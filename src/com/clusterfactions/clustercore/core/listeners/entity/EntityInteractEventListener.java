package com.clusterfactions.clustercore.core.listeners.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;

public class EntityInteractEventListener implements Listener{

	@EventHandler
	public void EntityInteractEvent(EntityInteractEvent e) {
		if(e instanceof Player) return;
		e.setCancelled(true);
	}
}