package com.clusterfactions.clustercore.core.command.impl.permission;

import org.bukkit.command.CommandSender;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.permission.PermissionGroup;
import com.clusterfactions.clustercore.util.Colors;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

@CommandAlias("permission|perm")
@CommandPermission("clustercore.admin")
public class PermissionCommand extends BaseCommand{

	@HelpCommand
	public void help(final CommandSender sender) {
		
	}
	
	@Subcommand("group")
	public class group extends BaseCommand{
		
		@Subcommand("set")
		@CommandCompletion("@players @cluster-perm-groups")
		public void set(final CommandSender sender, OnlinePlayer target, PermissionGroup group)
		{
			ClusterCore.getInstance().getPlayerPermissionManager().setPlayerGroup(target.player, group);
			sender.sendMessage(Colors.parseColors(target.player.getDisplayName() + "&7's group set to " + ClusterCore.getInstance().getPlayerManager().getPlayerData(target.player).getGroup().getGroupID()));
			
		}
	}
	

}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	