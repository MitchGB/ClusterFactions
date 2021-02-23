package com.clusterfactions.clustercore.core.command.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.player.PlayerData;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;

@CommandAlias("clusterfactions|cf|clustercore")
public class ClusterFactionsCommand extends BaseCommand{
	
	@Default
	public void execute(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		playerData.sendMessage("&b&lClusterCore [&7" + ClusterCore.getInstance().getDescription().getVersion() + "&b]");
		playerData.sendMessage("&7Developed by: &bBuby");
		playerData.sendMessage("&7Contributors:");
		playerData.sendMessage("&7http://clusterfactions.com");
	}
	
}
	
	
	
	

















