package com.clusterfactions.clustercore.core.command.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.core.inventory.impl.TestMenu;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;

@CommandAlias("testmenu")
public class TestMenuCommand extends BaseCommand{
	
	@Default
	public void execute(final CommandSender sender) {
		new TestMenu((Player)sender).openInventory((Player)sender);
	}
}
