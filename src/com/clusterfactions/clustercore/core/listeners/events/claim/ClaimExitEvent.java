package com.clusterfactions.clustercore.core.listeners.events.claim;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class ClaimExitEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	

	@Getter private Player player;
	
	public ClaimExitEvent(Player player) {
		this.player = player;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
