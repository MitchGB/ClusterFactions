package com.clusterfactions.clustercore.core.command.impl.factions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.chat.ChatMessageMode;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.util.FactionPlayerRemoveReason;
import com.clusterfactions.clustercore.core.inventory.impl.TestMenu;
import com.clusterfactions.clustercore.core.lang.Lang_EN_US;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.location.LocationUtil;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

@CommandAlias("factions|faction|fact|f|fs")
public class FactionsCommand extends BaseCommand{
	
	@Default
	public void execute(final CommandSender sender) {
		new TestMenu((Player)sender).openInventory((Player)sender);
	}
	
	@HelpCommand
	public void help(final CommandSender sender) {
		sender.sendMessage("TESTSETST");
	}
	
	@Subcommand("create")
	public void create(final CommandSender sender, final String name, final String tag){
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(data.getFaction() != null) {
			data.sendMessage(Lang_EN_US.ALREADY_IN_FACTION);
			return;
		}
		if(tag.contains(" "))
		{
			data.sendMessage(Lang_EN_US.TAG_CANNOT_CONTAIN_SPACE);
			return;
		}
		if(ClusterCore.getInstance().getMongoHook().valueExists("factionTag", tag, "factions"))
		{
			data.sendMessage(Lang_EN_US.FACTION_TAG_TAKEN);
			return;
		}
		
		ClusterCore.getInstance().getFactionsManager().createFaction((Player)sender, name, tag);
	}
	
	@Subcommand("map")
	public void map(final CommandSender sender) {
		ClusterCore.getInstance().getFactionMapGeneratorManager().openMapView((Player)sender);
	}
	
	@Subcommand("leave")
	public void leave(final CommandSender sender){
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		data.getFaction().removePlayer((Player)sender, FactionPlayerRemoveReason.LEFT);
	}
	
	@Subcommand("invite")
	@CommandCompletion("@players")
	public void invite(final CommandSender sender, OnlinePlayer player) {
		PlayerData inviterData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData inviteeData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		if(inviterData.getFaction() == null)
		{
			inviterData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(inviterData.getFaction().getFactionID());
		if(faction == null)
		{
			inviterData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		if(!faction.isModerator((Player)sender) && !faction.isCoLeader((Player)sender) && !faction.isLeader((Player)sender))
		{
			inviterData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(faction.inviteListContains(player.getPlayer()))
		{
			inviterData.sendMessage(Lang_EN_US.PLAYER_ALREADY_INVITED);
			return;
		}
		if(inviteeData.getFaction() != null) {
			inviterData.sendMessage(Lang_EN_US.PLAYER_ALREADY_IN_FACTION);
			return;
		}
		//CHECK IF SENDER HAS PERMS
		
		faction.invitePlayer((Player)sender, player.getPlayer());
	}
	
	@Subcommand("join")
	public void join(final CommandSender sender, String tag) {
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
		if(faction == null) {
			data.sendMessage(String.format(Lang_EN_US.NO_FACTION_WITH_TAG, tag));
			return;
		}
		if(!faction.inviteListContains (((Player)sender) )){
			data.sendMessage(String.format(Lang_EN_US.NO_PENDING_FACTION_INVITE, tag));
			return;
		}
		faction.acceptInvite((Player)sender);
		
	}
	
	@Subcommand("sethome")
	public void sethome(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction().getFactionID());
		if(faction == null)
		{
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		if(!faction.isModerator((Player)sender) && !faction.isCoLeader((Player)sender) && !faction.isLeader((Player)sender))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		playerData.sendMessage(String.format(Lang_EN_US.FACTION_HOME_SET,LocationUtil.formatString(((Player)sender).getLocation())));
		faction.setFactionHome(((Player)sender).getLocation());
	}
	
	@Subcommand("home")
	public void home(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(playerData.getFaction() == null)
		{
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction().getFactionID());
		if(faction == null)
		{
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		//IMPLEMENT TIMER 
		((Player)sender).teleport(faction.getFactionHome());
	}
	
	@Subcommand("chat")
	@CommandCompletion("@chat-message-modes")
	public void chat(final CommandSender sender, ChatMessageMode mode) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(playerData.getFaction() == null && mode == ChatMessageMode.FACTION)
		{
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction().getFactionID());
		if(faction == null  && mode == ChatMessageMode.FACTION)
		{
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		playerData.setChatMode(mode);
	}
}



























