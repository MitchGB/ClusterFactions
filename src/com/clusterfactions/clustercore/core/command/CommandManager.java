package com.clusterfactions.clustercore.core.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.chat.ChatMessageMode;
import com.clusterfactions.clustercore.core.command.impl.admin.AdminCommand;
import com.clusterfactions.clustercore.core.command.impl.factions.FactionsCommand;
import com.clusterfactions.clustercore.core.command.impl.permission.PermissionCommand;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.permission.PermissionGroup;
import com.clusterfactions.clustercore.core.player.PlayerData;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;

public class CommandManager {

	@Getter public static PaperCommandManager instance;
	
	
	public CommandManager() {
		instance = new PaperCommandManager(ClusterCore.getInstance());
		
		instance.getCommandContexts().registerContext(Faction.class, c -> {return ClusterCore.getInstance().getFactionsManager().getFaction(c.popFirstArg());});
		
		
		instance.getCommandCompletions().registerAsyncCompletion("custom-items", c -> {return CustomItemType.getAllList();});
		instance.getCommandCompletions().registerAsyncCompletion("chat-message-modes", c -> {return ChatMessageMode.getAllList();});
		instance.getCommandCompletions().registerAsyncCompletion("cluster-perm-groups",  c -> {return PermissionGroup.getAllList();});
		instance.getCommandCompletions().registerAsyncCompletion("faction-online-players",  c -> {
			PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(c.getPlayer());
			if(data.getFaction() == null) return new ArrayList<>();
			Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(data.getFaction());
			List<String> list = new ArrayList<>();
			for(UUID uuid : faction.getPlayers())
			{
				list.add(Bukkit.getPlayer(uuid).getName());
			}
			return list;
			
		});
		registerCommand(
				new FactionsCommand(),
				new PermissionCommand(),
				new AdminCommand()

		);
		instance.getCommandCompletions().registerAsyncCompletion("faction-warps", c -> {
			PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(c.getPlayer());
			if(data.getFaction() == null) return new ArrayList<>();
			Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(data.getFaction());
			List<String> list = new ArrayList<>();
			for(String name : faction.getWarps())
			{
				list.add(name);
			}
			return list;
		});
	}
	
	private void registerCommand(BaseCommand... cmds) {
		for(BaseCommand cmd : cmds)
		{
			instance.registerCommand(cmd);
		}
	}

}




















