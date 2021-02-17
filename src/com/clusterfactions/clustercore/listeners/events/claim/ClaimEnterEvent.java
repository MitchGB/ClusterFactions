package com.clusterfactions.clustercore.listeners.events.claim;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class ClaimEnterEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	

	@Getter private Player player;
	@Getter private UUID faction;
	
	public ClaimEnterEvent(Player player, UUID faction) {
		this.player = player;
		this.faction = faction;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
