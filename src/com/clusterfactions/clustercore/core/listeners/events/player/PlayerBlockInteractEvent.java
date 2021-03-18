package com.clusterfactions.clustercore.core.listeners.events.player;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import lombok.Getter;

public class PlayerBlockInteractEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	

	@Getter private Player player;
	@Getter private Block block;
	@Getter private Action action;
	private PlayerInteractEvent event;
	
	public PlayerBlockInteractEvent(Player player, Block block, Action action, PlayerInteractEvent event)  {
		this.player = player;
		this.block = block;
		this.action = action;
		this.event = event;
	}
	
	public void setCancelled(boolean state) {
		event.setCancelled(state);
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
