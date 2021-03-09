package com.clusterfactions.clustercore.core.listeners.events.claim;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.clusterfactions.clustercore.core.factions.claim.ChunkOwner;

import lombok.Getter;

public class ClaimEnterEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	

	@Getter private Player player;
	@Getter private ChunkOwner owner;
	
	public ClaimEnterEvent(Player player, ChunkOwner owner) {
		this.player = player;
		this.owner = owner;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
