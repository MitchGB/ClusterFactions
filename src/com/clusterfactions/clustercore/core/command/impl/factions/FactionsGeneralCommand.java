package com.clusterfactions.clustercore.core.command.impl.factions;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.command.impl.factions.claim.FactionsClaimCommand;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.NumberUtil;
import com.clusterfactions.clustercore.util.location.LocationUtil;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("f|factions|faction|fact|fs")
public class FactionsGeneralCommand extends BaseCommand{
	
	@SuppressWarnings("unchecked")
	@HelpCommand
	public void help(final CommandSender sender, @Default("1") int page) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		List<String> commandList = ClusterCore.getInstance().getCommandManager().allCommands(FactionsClaimCommand.class, FactionsInternalCommand.class, FactionsGeneralCommand.class);
		
		page = NumberUtil.clamp(page, 0, (int)(Math.ceil((commandList.size()-1)/10)+1) );
		
		playerData.sendMessage("Faction Help [" + page + "/" + (int)(Math.ceil((commandList.size()-1)/10)+1) +"]");
		for(int i = 0 + (10*page-10); i < 10*page; i++) {
			if(commandList.size() <= i) break;
			String str = commandList.get(i);
			if(str == null) continue;
			playerData.sendMessage("&7-&b"+str.replace("(", "&7(&b").replace(")", "&7)&b").replace("|", "&7|&b"));
		}
	}
	
    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender).sendMessage("&7Unkown command. Type &b/f help &7for a list of commands.");
    }
    
	@Subcommand("map")
	public void map(final CommandSender sender) {
		ClusterCore.getInstance().getFactionMapGeneratorManager().openMapView((Player)sender);
	}
	
	@Subcommand("randomtp|rtp|wild|wilderness")
	public void rtp(final CommandSender sender) {
		Player player = (Player)sender;
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		if(playerData.isCombatTagged())
		{
			playerData.sendMessage(Lang.PLAYER_COMBAT_TAGGED);
			return;
		}
		Location safeLoc = LocationUtil.findSafeLoc(player);
		ClusterCore.getInstance().getTeleportQueue().scheduleTeleport(player, 3000L, safeLoc);
	    
	}
	
	@Subcommand("who")
	@CommandCompletion("@all-factions")
	public void who(final CommandSender sender, String tag) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
		if(faction == null) {
			playerData.sendMessage(Lang.NO_FACTION_WITH_TAG, tag);
			return;
		}
		playerData.sendMessage("");
		playerData.sendMessage("Land / Power / MaxPower: " + faction.getClaimCount() + " / " + faction.getFactionPower() + " / " + (faction.getPlayerCount() * PlayerData.maxPower));
		playerData.sendMessage("");
		playerData.sendMessage("");
		playerData.sendMessage("");
		playerData.sendMessage("");
	}
	
	@Subcommand("list")
	public void list(final CommandSender sender, @Default("1") int page)
	{
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		page = NumberUtil.clampMin(page, 0);
		List<String> factionList = ClusterCore.getInstance().getMongoHook().getAllList("factionTag", "factions");
		playerData.sendMessage("Faction List [" + page + "/" + (int)(Math.ceil(factionList.size()/10)+1) +"]");
		for(int i = 0 + (10*page-10); i < 10*page; i++) {
			if(factionList.size() <= i) break;
			String str = factionList.get(i);
			if(str == null) continue;
			playerData.sendMessage(str);
		}
		playerData.sendMessage("------------------");
	}
	
	@Subcommand("power")
	@CommandCompletion("@faction-online-players")
	public void power(final CommandSender sender, Player player) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());	
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang.PLAYER_NOT_IN_FACTION);
			return;
		}
		senderData.sendMessage(Lang.PLAYER_POWER, player.getName(), playerData.getPower());
	
	}
	
	@Subcommand("power")
	public void power(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		playerData.sendMessage(Lang.PLAYER_POWER, ((Player)sender).getName(), playerData.getPower());
	}
	
}



























