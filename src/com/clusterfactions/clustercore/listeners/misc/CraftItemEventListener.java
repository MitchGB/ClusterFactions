package com.clusterfactions.clustercore.listeners.misc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftItemEventListener implements Listener{
	
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void CraftItemEvent(CraftItemEvent e) {
    }
}
