package com.clusterfactions.clustercore.core.command.impl.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.core.inventory.impl.player.settings.MainSettingsMenu;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;

@CommandAlias("settings|settingsmenu")
public class SettingsCommand extends BaseCommand{
	
	@Default
	public void execute(final CommandSender sender) {
		new MainSettingsMenu((Player)sender).openInventory((Player)sender);
	}
	
}
	
	
	
	

















