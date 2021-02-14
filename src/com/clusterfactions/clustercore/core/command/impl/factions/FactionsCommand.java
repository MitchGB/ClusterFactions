package com.clusterfactions.clustercore.core.command.impl.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.impl.TestMenu;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("factions|faction|fact|f|fs")
public class FactionsCommand extends BaseCommand{
	
	@Default
	public void execute(final CommandSender sender) {
		new TestMenu((Player)sender).openInventory((Player)sender);
	}
	
	@Subcommand("create")
	public void create(final CommandSender sender, final String name, final String tag)
	{
		ClusterCore.getInstance().getFactionsManager().createFaction((Player)sender, name, tag);
	}
	
	@Subcommand("leave")
	public void leave(final CommandSender sender)
	{

		ClusterCore.getInstance().getFactionsManager().leave((Player)sender);
	}
}
