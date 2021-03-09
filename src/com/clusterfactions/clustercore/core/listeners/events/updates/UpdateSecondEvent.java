package com.clusterfactions.clustercore.core.listeners.events.updates;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateSecondEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
