package com.clusterfactions.clustercore.core.command.impl.factions;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.chat.ChatMessageMode;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.factions.util.FactionPlayerRemoveReason;
import com.clusterfactions.clustercore.core.inventory.impl.TestMenu;
import com.clusterfactions.clustercore.core.lang.Lang_EN_US;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.location.LocationUtil;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

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
	
	@Subcommand("claim")
	public void claim(final CommandSender sender, @Default("0") int radius)
	{
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(radius > 5) {
			playerData.sendMessage(Lang_EN_US.MAXIMUM_CLAIM_RADIUS);
			return;
		}
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
		if(!faction.isModerator((Player)sender) && !faction.isCoLeader((Player)sender) && !faction.isLeader((Player)sender))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		if(factionClaimed != null) {
			
			playerData.sendMessage(String.format(Lang_EN_US.CHUNK_ALREADY_CLAIMED, ClusterCore.getInstance().getFactionsManager().getFaction(factionClaimed).getFactionName()));
			return;
		}
		
		if(radius == 0)
		{
			claimManager.claimChunk(claimManager.getChunkVector(((Player)sender).getLocation()), faction);
			return;
		}
		
		Vector2Integer playerChunk = claimManager.getChunkVector(((Player)sender).getLocation());
		
		Vector2Integer[][] claimMap = new Vector2Integer[radius*2][radius*2];
		
		int pX = playerChunk.getX();
		int pZ = playerChunk.getZ();
		
		int rad = radius;
		
		int uX = pX + rad;
		int uZ = pZ + rad ;
		
		int lX = pX - rad;
		int lZ = pZ - rad;
		int xIndex = 0;
		int zIndex = 0;

		int overlapping = 0;
		
		for(int z = lZ; z < uZ; z++)
		{
			xIndex = 0;
			for(int x = lX; x <uX; x++)
			{
				claimMap[xIndex][zIndex] = new Vector2Integer(x,z);
				if(claimManager.chunkClaimed(claimMap[xIndex][zIndex]) != null)
				{
					if(claimManager.chunkClaimed(claimMap[xIndex][zIndex]).equals(faction.getFactionID()))
					{
						claimMap[xIndex][zIndex] = null;
						overlapping++;
						xIndex++;

						continue;
					}
					playerData.sendMessage(Lang_EN_US.CLAIM_RADIUS_OVERLAPPING);
					return;
				}

				xIndex++;
			}
			zIndex++;
		}
		
		for(Vector2Integer[] vA : claimMap) {
			for(Vector2Integer v : vA)
			{
				if(v == null) continue;
				claimManager.claimChunk(v, faction);
			}
		}
		
		playerData.sendMessage(String.format(Lang_EN_US.SUCCESSFULL_CLAIM, (radius*2)*(radius*2) - overlapping, overlapping));
		
	}
	
	@Subcommand("isclaimed")
	public void isclaimed(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		playerData.sendMessage(factionClaimed == null ? "This chunk is not claimed" : "This chunk is claimed by " + ClusterCore.getInstance().getFactionsManager().getFaction(factionClaimed).getFactionName());
	}
}



























