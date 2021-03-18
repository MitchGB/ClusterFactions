package com.clusterfactions.clustercore.core.crate.model;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.util.ActionHandler;

public class Prize_Command extends Prize {

	ActionHandler<Player> command;
	ItemStack displayItem;
	
	public Prize_Command(String displayName, ActionHandler<Player> command, ItemStack displayItem, String... desc) {
		super(displayName, desc);
		this.command = command;
	}

	@Override
	public void output(Player player) {
		command.exec(player);
	}

	@Override
	public ItemStack displayItem() {
		return displayItem;
	}

}