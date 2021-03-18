package com.clusterfactions.clustercore.core.command.impl.admin.crate;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("admin|adminstrator|administration")
@CommandPermission("clustercore.admin")
public class AdminCrateCommand extends BaseCommand{
	
	@Subcommand("crate")
	public class crate extends BaseCommand{
		@Subcommand("create")
		@CommandCompletion("@crate-types")
		public void create(final CommandSender sender, String name){
			name = name.toUpperCase();
			PlayerData player = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
			System.out.println(ClusterCore.getInstance().getCrateManager());
			if(!ClusterCore.getInstance().getCrateManager().isCrate(name)) {
				player.sendMessage(Lang.NO_CRATE_TYPE_OF, name);
				return;
			}
			player.sendMessage(Lang.CRATE_MODE_ENABLED1, name);
			player.sendMessage(Lang.CRATE_MODE_ENABLED2);
			player.setCratePlaceMode(name);
			
		}
	}
}