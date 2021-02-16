package com.clusterfactions.clustercore.core.command.impl.factions;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.chat.ChatMessageMode;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.factions.util.FactionPerm;
import com.clusterfactions.clustercore.core.factions.util.FactionPlayerRemoveReason;
import com.clusterfactions.clustercore.core.factions.util.FactionRole;
import com.clusterfactions.clustercore.core.inventory.impl.faction.perm.MainPermissionMenu;
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
		help(sender);
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
		ClusterCore.getInstance().getFactionsManager().getFaction(data.getFaction()).removePlayer((Player)sender, FactionPlayerRemoveReason.LEFT);
	}
	
	@Subcommand("invite")
	@CommandCompletion("@players")
	public void invite(final CommandSender sender, OnlinePlayer player) {
		PlayerData inviterData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData inviteeData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		if(inviterData.getFaction() == null){
			inviterData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(inviterData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.INVITE)){
			inviterData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(faction.inviteListContains(player.getPlayer())){
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
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
		if(faction == null) {
			playerData.sendMessage(Lang_EN_US.NO_FACTION_WITH_TAG, tag);
			return;
		}
		if(playerData.getFaction() != null){
			playerData.sendMessage(Lang_EN_US.ALREADY_IN_FACTION);
			return;
		}
		if(!faction.inviteListContains (((Player)sender) )){
			playerData.sendMessage(Lang_EN_US.NO_PENDING_FACTION_INVITE, tag);
			return;
		}
		faction.acceptInvite((Player)sender);
		
	}
	
	@Subcommand("sethome")
	public void sethome(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		if(!faction.hasPerm((Player)sender, FactionPerm.SETHOME)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		playerData.sendMessage(Lang_EN_US.FACTION_HOME_SET,LocationUtil.formatString(((Player)sender).getLocation()));
		faction.setFactionHome(((Player)sender).getLocation());
	}
	
	@Subcommand("home")
	public void home(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		//IMPLEMENT TIMER 
		((Player)sender).teleport(faction.getFactionHome());
	}
	
	@Subcommand("chat")
	@CommandCompletion("@chat-message-modes")
	public void chat(final CommandSender sender, ChatMessageMode mode) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(playerData.getFaction() == null && (mode == ChatMessageMode.FACTION || mode == ChatMessageMode.ALLY)){
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
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		if(factionClaimed != null) {
			playerData.sendMessage(Lang_EN_US.CHUNK_ALREADY_CLAIMED, ClusterCore.getInstance().getFactionsManager().getFaction(factionClaimed).getFactionName());
			return;
		}
		if(radius == 0){

			playerData.sendMessage(Lang_EN_US.SUCCESSFULL_CLAIM, "1");
			claimManager.claimChunk(claimManager.getChunkVector(((Player)sender).getLocation()), faction);
			return;
		}
		
		claimManager.claimArea(((Player)sender), radius, radius);
	}
	
	@Subcommand("claim")
	public void claim(final CommandSender sender, int x, int z)
	{
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(x > 5 || z > 5) {
			playerData.sendMessage(Lang_EN_US.MAXIMUM_CLAIM_RADIUS);
			return;
		}
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		if(factionClaimed != null) {
			playerData.sendMessage(Lang_EN_US.CHUNK_ALREADY_CLAIMED, ClusterCore.getInstance().getFactionsManager().getFaction(factionClaimed).getFactionName());
			return;
		}
		
		claimManager.claimArea(((Player)sender), x, z);
	}
	
	@Subcommand("unclaim")
	public void unclaim(final CommandSender sender, @Default("0") int radius)
	{
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(radius > 5) {
			playerData.sendMessage(Lang_EN_US.MAXIMUM_CLAIM_RADIUS);
			return;
		}
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		if(factionClaimed == null) {
			playerData.sendMessage(Lang_EN_US.CHUNK_NOT_CLAIMED);
			return;
		}
		if(radius == 0){

			playerData.sendMessage(Lang_EN_US.SUCCESSFULL_UNCLAIM);
			claimManager.removeClaimChunk(claimManager.getChunkVector(((Player)sender).getLocation()), faction);
			return;
		}
		
		claimManager.removeClaimArea(((Player)sender), radius, radius);
	}
	
	@Subcommand("unclaim")
	public void unclaim(final CommandSender sender, int x, int z)
	{
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(x > 5 || z > 5) {
			playerData.sendMessage(Lang_EN_US.MAXIMUM_CLAIM_RADIUS);
			return;
		}
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		if(factionClaimed == null) {
			playerData.sendMessage(Lang_EN_US.CHUNK_NOT_CLAIMED);
			return;
		}
		
		claimManager.removeClaimArea(((Player)sender), x, z);
	}
	
	@Subcommand("isclaimed")
	public void isclaimed(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		playerData.sendMessage(factionClaimed == null ? "This chunk is not claimed" : "This chunk is claimed by " + ClusterCore.getInstance().getFactionsManager().getFaction(factionClaimed).getFactionName());
	}
	
	@Subcommand("perms|perm|permission|permissions")
	public void perm(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());		
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}		
		if(faction.getPlayerRole(playerData.getPlayer()).getWeight() != FactionRole.LEADER.getWeight()){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		new MainPermissionMenu((Player)sender).openInventory((Player)sender);
	}
	
	@Subcommand("promote")
	@CommandCompletion("@faction-online-players")
	public void promote(final CommandSender sender, OnlinePlayer player) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang_EN_US.PLAYER_NOT_IN_FACTION);
			return;
		}
		if(!faction.hasPerm((Player)sender, FactionPerm.PROMOTE)){
			senderData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(faction.getPlayerRole(player.getPlayer()).equals(FactionRole.COLEADER))
		{
			senderData.sendMessage(Lang_EN_US.FACTION_CANNOT_PROMOTE_TO_LEADER);
			return;
		}
		if(faction.getPlayerRole((Player)sender).getWeight() == faction.getPlayerRole(player.getPlayer()).getWeight())
		{
			senderData.sendMessage(Lang_EN_US.FACTION_CANNOT_PROMOTE_PLAYER);
			return;
		}
		faction.promotePlayer(player.getPlayer().getUniqueId());
		faction.messageAll(String.format(Lang_EN_US.FACTION_PROMOTED_PLAYER, player.getPlayer().getName(), faction.getPlayerRole(player.getPlayer()).toString()));
	}
	
	@Subcommand("demote")
	@CommandCompletion("@faction-online-players")
	public void demote(final CommandSender sender, OnlinePlayer player) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang_EN_US.PLAYER_NOT_IN_FACTION);
			return;
		}
		if(!faction.hasPerm((Player)sender, FactionPerm.PROMOTE)){
			senderData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(faction.getPlayerRole(player.getPlayer()).getWeight() == 1 || faction.getPlayerRole((Player)sender).getWeight() < faction.getPlayerRole(player.getPlayer()).getWeight())
		{
			senderData.sendMessage(Lang_EN_US.FACTION_CANNOT_DEMOTE_PLAYER);
			return;
		}
		faction.demotePlayer(player.getPlayer().getUniqueId());
		faction.messageAll(String.format(Lang_EN_US.FACTION_DEMOTED_PLAYER, player.getPlayer().getName(), faction.getPlayerRole(player.getPlayer()).toString()));
	}
	
	@Subcommand("ally")
	public void ally(final CommandSender sender, String tag) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		Faction ally = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}		
		if(!faction.hasPerm((Player)sender, FactionPerm.ALLY)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(ally == null) {
			playerData.sendMessage(Lang_EN_US.NO_FACTION_WITH_TAG, tag);
			return;
		}
		if(faction.allyInviteListContains(ally))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_ALREADY_INVITED);
			return;
		}
		if(faction.isAllied(ally))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_ALREADY_ALLIED);
			return;
		}
		if(faction.getFactionID().toString().equals(ally.getFactionID().toString()))
		{
			playerData.sendMessage(Lang_EN_US.CANNOT_ALLY_OWN_FACTION);
			return;
		}
		if(ally.allyInviteListContains(faction))
		{
			ally.allyFaction(faction);
			faction.allyFaction(ally);
			ally.messageAll(String.format(Lang_EN_US.FACTIONS_ARE_NOW_ALLIED, faction.getFactionName()));
			faction.messageAll(String.format(Lang_EN_US.FACTIONS_ARE_NOW_ALLIED, ally.getFactionName()));
			return;
		}
		faction.inviteAllyFaction(ally);
	}
	
	@Subcommand("unally")
	public void unally(final CommandSender sender, String tag) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		Faction ally = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}		
		if(!faction.hasPerm((Player)sender, FactionPerm.ALLY)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(ally == null) {
			playerData.sendMessage(Lang_EN_US.NO_FACTION_WITH_TAG, tag);
			return;
		}
		if(!faction.isAllied(ally))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_NOT_ALLIED);
			return;
		}
		if(faction.getFactionID().toString().equals(ally.getFactionID().toString()))
		{
			playerData.sendMessage(Lang_EN_US.CANNOT_ALLY_OWN_FACTION);
			return;
		}
		faction.unally(ally);
	}
	@Subcommand("unenemy")
	public void unenemy(final CommandSender sender, String tag) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		Faction enemy = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}		
		if(!faction.hasPerm((Player)sender, FactionPerm.ALLY)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(enemy == null) {
			playerData.sendMessage(Lang_EN_US.NO_FACTION_WITH_TAG, tag);
			return;
		}
		if(!faction.isEnemy(enemy))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_NOT_ENEMIES);
			return;
		}
		if(faction.getFactionID().toString().equals(enemy.getFactionID().toString()))
		{
			playerData.sendMessage(Lang_EN_US.CANNOT_ENEMY_OWN_FACTION);
			return;
		}
		faction.unenemy(enemy);
	}
	
	@Subcommand("enemy")
	public void enemy(final CommandSender sender, String tag) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		Faction enemy = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}		
		if(!faction.hasPerm((Player)sender, FactionPerm.ALLY)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(enemy == null) {
			playerData.sendMessage(Lang_EN_US.NO_FACTION_WITH_TAG, tag);
			return;
		}
		if(faction.isEnemy(enemy))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_ALREADY_ENEMIES);
			return;
		}
		if(faction.getFactionID().toString().equals(enemy.getFactionID().toString()))
		{
			playerData.sendMessage(Lang_EN_US.CANNOT_ENEMY_OWN_FACTION);
			return;
		}
		faction.enemy(enemy);
	}
}



























