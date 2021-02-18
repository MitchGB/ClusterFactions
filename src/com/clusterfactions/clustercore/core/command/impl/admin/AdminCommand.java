package com.clusterfactions.clustercore.core.command.impl.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.ItemManager;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

@CommandAlias("admin|adminstrator|administration")
@CommandPermission("clustercore.admin")
public class AdminCommand extends BaseCommand{

	@HelpCommand
	public void help(final CommandSender sender) {
		
	}
	
	@Subcommand("item")
	public class item extends BaseCommand{
		
		private void giveItem(Player player, CustomItemType type, int amount){
			ItemManager itemManager = ClusterCore.getInstance().getItemManager();
			player.getInventory().addItem(itemManager.getCustomItemHandler(type).getNewStack(amount));
		}
		
		@Subcommand("give")
		@CommandCompletion("@players @custom-items")
		public void give(final CommandSender sender, OnlinePlayer player, CustomItemType type, @Default("1") int amount) {
			giveItem(player.getPlayer(), type, amount);
		}
		
		@Subcommand("give")
		@CommandCompletion("@cluster-perm-groups")
		public void give(final CommandSender sender, CustomItemType type, @Default("1") int amount) {
			giveItem((Player)sender, type, amount);
		}
	}
	

}
	
	
	
	