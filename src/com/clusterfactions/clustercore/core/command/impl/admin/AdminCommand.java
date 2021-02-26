package com.clusterfactions.clustercore.core.command.impl.admin;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.impl.block.FurnaceInventory;
import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.ItemManager;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;

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
	
	@Subcommand("save|savedata")
	public void save(final CommandSender sender, OnlinePlayer player) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		playerData.saveData();
	}
	
	@Subcommand("langtest")
	@CommandCompletion("@langs")
	public void langtest(final CommandSender sender, Lang lang){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		playerData.sendMessage(ClusterCore.getInstance().getLanguageManager().getString(Locale.ENGLISH, lang));
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
	
	@Subcommand("power")
	public class power extends BaseCommand{
		
		@Subcommand("give")
		@CommandCompletion("@players")
		public void give(final CommandSender sender, OnlinePlayer player, int amount) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
			playerData.setPower(playerData.getPower() + amount);
			playerData.saveData("power");
		}
		
		@Subcommand("remove")
		@CommandCompletion("@players")
		public void remove(final CommandSender sender, OnlinePlayer player, int amount) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
			playerData.setPower(playerData.getPower() - amount);
			playerData.saveData("power");
		}
		
		@Subcommand("set")
		@CommandCompletion("@players")
		public void set(final CommandSender sender, OnlinePlayer player, int amount) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
			playerData.setPower(amount);
			playerData.saveData("power");
		}
		
		@Subcommand("reset")
		@CommandCompletion("@players")
		public void reset(final CommandSender sender, OnlinePlayer player) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
			playerData.setPower(0);
			playerData.saveData("power");
		}
		
	}
}
	
	
	
	

















