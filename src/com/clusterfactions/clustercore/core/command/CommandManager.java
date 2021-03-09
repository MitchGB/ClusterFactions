package com.clusterfactions.clustercore.core.command;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.chat.ChatMessageMode;
import com.clusterfactions.clustercore.core.command.impl.ClusterFactionsCommand;
import com.clusterfactions.clustercore.core.command.impl.GuideCommand;
import com.clusterfactions.clustercore.core.command.impl.admin.AdminCommand;
import com.clusterfactions.clustercore.core.command.impl.admin.claim.AdminClaimCommand;
import com.clusterfactions.clustercore.core.command.impl.factions.FactionsGeneralCommand;
import com.clusterfactions.clustercore.core.command.impl.factions.FactionsInternalCommand;
import com.clusterfactions.clustercore.core.command.impl.factions.SettingsCommand;
import com.clusterfactions.clustercore.core.command.impl.factions.claim.FactionsClaimCommand;
import com.clusterfactions.clustercore.core.command.impl.permission.PermissionCommand;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.claim.AdminClaimType;
import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.permission.PermissionGroup;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.EnumUtil;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;

public class CommandManager {

	@Getter public static PaperCommandManager instance;
	
	
	public CommandManager() {
		instance = new PaperCommandManager(ClusterCore.getInstance());
		
		instance.getCommandContexts().registerContext(Faction.class, c -> {return ClusterCore.getInstance().getFactionsManager().getFaction(c.popFirstArg());});

		instance.getCommandCompletions().registerAsyncCompletion("admin-claim-types", c -> {return EnumUtil.getAllList(AdminClaimType.class);});
		instance.getCommandCompletions().registerAsyncCompletion("langs", c -> {return EnumUtil.getAllList(Lang.class);});
		instance.getCommandCompletions().registerAsyncCompletion("custom-items", c -> {return EnumUtil.getAllList(CustomItemType.class);});
		instance.getCommandCompletions().registerAsyncCompletion("chat-message-modes", c -> {return EnumUtil.getAllList(ChatMessageMode.class);});
		instance.getCommandCompletions().registerAsyncCompletion("cluster-perm-groups",  c -> {return EnumUtil.getAllList(PermissionGroup.class);});
		instance.getCommandCompletions().registerAsyncCompletion("all-factions", c -> {return ClusterCore.getInstance().getMongoHook().getAllList("factionTag", "factions");});
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
				new FactionsInternalCommand(),
				new FactionsGeneralCommand(),
				new FactionsClaimCommand(),
				
				new AdminCommand(),
				new AdminClaimCommand(),
				
				new PermissionCommand(),
				new SettingsCommand(),
				new ClusterFactionsCommand(),
				new GuideCommand()

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
	
	public List<String> allCommands(@SuppressWarnings("unchecked") Class<? extends BaseCommand>... commandClasses){
        try {
        	List<String> ret = new ArrayList<>();
        	for(Class<? extends BaseCommand> commandClass : commandClasses) {
        		String mainCommand = commandClass.getAnnotation(CommandAlias.class).value().split("|")[0];
        	
        		for(Method method : commandClass.getDeclaredMethods()) {
        			if(method.getAnnotation(Subcommand.class) == null) continue;
        		
        			if(!ret.contains(mainCommand + " " + getCommand(method)))
        				ret.add(mainCommand + " " + getCommand(method));		
        		}
            
            
            	for(Class<?> nestedClazz : commandClass.getDeclaredClasses()) {
        			if(nestedClazz.getAnnotation(Subcommand.class) == null) continue;
        			String classCommand = nestedClazz.getAnnotation(Subcommand.class).value();
        		
        			for(Method method : nestedClazz.getDeclaredMethods()) {
                		if(method.getAnnotation(Default.class) != null)
                			ret.add(mainCommand + " " + classCommand + getParamNames(method));
  	
                		if(method.getAnnotation(Subcommand.class) != null)
            				ret.add(mainCommand + " " + classCommand + " " + getCommand(method));		
                	}
            	}
        	}

        	return ret;
        } catch (Throwable e) {
            e.printStackTrace();
        }
		return null;
	}
	
	private String getParamNames(Method method){
		String paramString = "";
		Parameter[] params = method.getParameters();	
		Parameter[] paramList = params.clone();		

		if(params == null || params.length == 1) return "";
		
		paramList[0] = null;
		for(Parameter param : paramList) 
			if(param != null)
				paramString += " (" + param.getName() + ")";
		return paramString;
	}
	
	private String getCommand(Method method) {
		String paramString = getParamNames(method);
		Subcommand annotation = method.getAnnotation(Subcommand.class);
		return annotation.value() + paramString;
	}
	
}




















