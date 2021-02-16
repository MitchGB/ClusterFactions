package com.clusterfactions.clustercore.listeners.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;

public class EntityInteractEventListener implements Listener{

	@EventHandler
	public void EntityInteractEvent(EntityInteractEvent e) {
		e.setCancelled(true);
	}
}