package com.clusterfactions.clustercore.core.command.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.core.inventory.impl.guide.MainGuideMenu;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;

@CommandAlias("guide|guidebook")
public class GuideCommand extends BaseCommand{
	
	@Default
	public void execute(final CommandSender sender) {
		new MainGuideMenu((Player)sender).openInventory((Player)sender);
	}
	
}
	
	
	
	

















