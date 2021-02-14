package com.clusterfactions.clustercore.core.command;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.command.impl.TestMenuCommand;
import com.clusterfactions.clustercore.core.command.impl.factions.FactionsCommand;
import com.clusterfactions.clustercore.core.command.impl.permission.PermissionCommand;
import com.clusterfactions.clustercore.core.permission.PermissionGroup;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;

public class CommandManager {

	@Getter public static PaperCommandManager instance;
	
	
	public CommandManager() {
		instance = new PaperCommandManager(ClusterCore.getInstance());

		instance.getCommandCompletions().registerAsyncCompletion("cluster-perm-groups",  c -> {return PermissionGroup.getAllList();});
		registerCommand(
				new TestMenuCommand(),
				new FactionsCommand(),
				new PermissionCommand()

		);
	}
	
	private void registerCommand(BaseCommand... cmds) {
		for(BaseCommand cmd : cmds)
		{
			instance.registerCommand(cmd);
		}
	}

}




















