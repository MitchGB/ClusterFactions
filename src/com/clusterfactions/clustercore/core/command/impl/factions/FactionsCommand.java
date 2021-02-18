package com.clusterfactions.clustercore.core.command.impl.factions;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
		help(sender);
	}
	
	@HelpCommand
	public void help(final CommandSender sender) {
		sender.sendMessage("TESTSETST");
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
			playerData.sendMessage(Lang_EN_US.PLAYER_COMBAT_TAGGED);
			return;
		}
		Location safeLoc = LocationUtil.findSafeLoc(player);
		ClusterCore.getInstance().getTeleportQueue().scheduleTeleport(player, 3000L, safeLoc);
	    
	}
	
	@Subcommand("alert|weewoo")
	public void alert(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!playerData.isInFaction()){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		if(!faction.hasPerm(playerData.getPlayer(), FactionPerm.ALERT)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		faction.messageAll(Lang_EN_US.ALERT_RAID);
	}
	
	@Subcommand("create")
	public void create(final CommandSender sender, final String name, final String tag){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(!playerData.isInFaction()) {
			playerData.sendMessage(Lang_EN_US.ALREADY_IN_FACTION);
			return;
		}
		if(tag.contains(" "))
		{
			playerData.sendMessage(Lang_EN_US.TAG_CANNOT_CONTAIN_SPACE);
			return;
		}
		if(tag.length() > 4)
		{
			playerData.sendMessage(Lang_EN_US.FACTION_TAG_TOO_LONG);
			return;
		}
		if(ClusterCore.getInstance().getMongoHook().valueExists("factionTag", tag, "factions"))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_TAG_TAKEN);
			return;
		}
		
		ClusterCore.getInstance().getFactionsManager().createFaction((Player)sender, name, tag);
	}
	
	@Subcommand("leave")
	public void leave(final CommandSender sender){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);		
		if(!playerData.isInFaction()){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction()).removePlayer((Player)sender, FactionPlayerRemoveReason.LEFT);
	}
	
	@Subcommand("invite")
	@CommandCompletion("@players")
	public void invite(final CommandSender sender, OnlinePlayer player) {
		PlayerData inviterData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData inviteeData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		if(!inviterData.isInFaction()){
			inviterData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(inviterData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.INVITE)){
			inviterData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(faction.getBannedPlayers() != null && faction.getBannedPlayers().contains(player.getPlayer().getUniqueId()))
		{
			inviterData.sendMessage(Lang_EN_US.PLAYER_IS_CURRENTLY_BANNED);
			return;
		}
		if(faction.inviteListContains(player.getPlayer())){
			inviterData.sendMessage(Lang_EN_US.PLAYER_ALREADY_INVITED);
			return;
		}
		if(inviteeData.isInFaction()) {
			inviterData.sendMessage(Lang_EN_US.PLAYER_ALREADY_IN_FACTION);
			return;
		}
		faction.invitePlayer(player.getPlayer());
	}
	
	@Subcommand("deinvite|uninvite")
	@CommandCompletion("@players")
	public void uninvite(final CommandSender sender, OnlinePlayer player) {
		PlayerData inviterData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData inviteeData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		if(!inviterData.isInFaction()){
			inviterData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(inviterData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.INVITE)){
			inviterData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(!faction.inviteListContains(player.getPlayer())){
			inviterData.sendMessage(Lang_EN_US.PLAYER_NOT_ON_INVITE_LIST);
			return;
		}
		if(inviteeData.isInFaction()) {
			inviterData.sendMessage(Lang_EN_US.PLAYER_ALREADY_IN_FACTION);
			return;
		}
		inviterData.sendMessage(Lang_EN_US.UNINVITED_PLAYER);
		faction.uninvitePlayer(player.getPlayer());
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
		faction.saveData();
	}
	
	@Subcommand("home")
	public void home(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}		
		if(ClusterCore.getInstance().getCombatManager().isTagged(playerData))
		{
			playerData.sendMessage(Lang_EN_US.PLAYER_COMBAT_TAGGED);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.HOME)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(faction.getFactionHome() == null)
		{
			playerData.sendMessage(Lang_EN_US.NO_FACTION_HOME_SET);
			return;
		}
		ClusterCore.getInstance().getTeleportQueue().scheduleTeleport(playerData.getPlayer(), 3000L, faction.getFactionHome());
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
		if(radius < 0)
		{
			playerData.sendMessage(Lang_EN_US.RADIUS_CANNOT_BE_NEGATIVE);
			return;
		}
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
		if(x < 0 || z < 0)
		{
			playerData.sendMessage(Lang_EN_US.RADIUS_CANNOT_BE_NEGATIVE);
			return;
		}
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
		if(!playerData.getFaction().equals(factionClaimed)){
			playerData.sendMessage(Lang_EN_US.NOT_YOUR_CLAIM);
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
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		if(!playerData.getFaction().equals(factionClaimed)){
			playerData.sendMessage(Lang_EN_US.NOT_YOUR_CLAIM);
			return;
		}
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
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
	
	@Subcommand("showclaims|claims|claimlist")
	public void showclaims(final CommandSender sender) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());		
		if(faction.getClaimedChunks() == null || faction.getClaimedChunks().isEmpty())
		{
			senderData.sendMessage(Lang_EN_US.CLAIMLIST_EMPTY);
			return;
		}
		senderData.sendMessage("Faction claims:");
		StringBuilder builder = new StringBuilder();
		for(Vector2Integer claim : faction.getClaimedChunks()) {
			builder.append("&7[&a" + claim.getX() + "&7,&a" + claim.getZ() +"&7],");
		}
		senderData.sendMessage(builder.toString());
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
		if(!faction.hasPerm((Player)sender, FactionPerm.PROMOTE)){
			senderData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang_EN_US.PLAYER_NOT_IN_FACTION);
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
		if(!faction.hasPerm((Player)sender, FactionPerm.PROMOTE)){
			senderData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang_EN_US.PLAYER_NOT_IN_FACTION);
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
	
	@Subcommand("kick")
	@CommandCompletion("@faction-online-players")
	public void kick(final CommandSender sender, OnlinePlayer player) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());		
		if(!faction.hasPerm((Player)sender, FactionPerm.KICK)){
			senderData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang_EN_US.PLAYER_NOT_IN_FACTION);
			return;
		}

		if(faction.getPlayerRole((Player)sender).getWeight() <= faction.getPlayerRole(player.getPlayer()).getWeight())
		{
			senderData.sendMessage(Lang_EN_US.FACTION_CANNOT_KICK_PLAYER);
			return;
		}
		faction.banPlayer(player.getPlayer());
	}
	
	@Subcommand("ban")
	@CommandCompletion("@faction-online-players")
	public void ban(final CommandSender sender, OnlinePlayer player) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.BAN)){
			senderData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang_EN_US.PLAYER_NOT_IN_FACTION);
			return;
		}
		if(faction.getPlayerRole((Player)sender).getWeight() <= faction.getPlayerRole(player.getPlayer()).getWeight())
		{
			senderData.sendMessage(Lang_EN_US.FACTION_CANNOT_BAN_PLAYER);
			return;
		}
		faction.banPlayer(player.getPlayer());
	}
	
	@Subcommand("unban")
	public void unban(final CommandSender sender, OnlinePlayer player) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.BAN)){
			senderData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(faction.getBannedPlayers() != null)
		if(!faction.getBannedPlayers().contains(player.getPlayer().getUniqueId()))
		{
			senderData.sendMessage(Lang_EN_US.PLAYER_NOT_BANNED);
			return;
		}
		faction.unbanPlayer(player.getPlayer());
		senderData.sendMessage(Lang_EN_US.FACTION_UNBANNED_PLAYER, player.getPlayer().getName());
	}
	
	@Subcommand("banlist")
	public void banlist(final CommandSender sender) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());
		if(faction.getBannedPlayers() == null || faction.getBannedPlayers().isEmpty())
		{
			senderData.sendMessage(Lang_EN_US.BAN_LIST_EMPTY);
			return;
		}
		
		senderData.sendMessage("Banned players:");
		StringBuilder builder = new StringBuilder();
		for(UUID uuid : faction.getBannedPlayers())
		{
			builder.append(Bukkit.getPlayer(uuid).getName() + ",");
		}
		senderData.sendMessage(builder.toString());
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
	
	@Subcommand("setwarp")
	public void setwarp(final CommandSender sender, String name) {
		name = name.toUpperCase();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}		
		if(!faction.hasPerm((Player)sender, FactionPerm.SETWARP)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(faction.warpExists(name))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_WARP_NAME_TAKEN, name);
			return;
		}
		playerData.sendMessage(Lang_EN_US.FACTION_WARP_SET, LocationUtil.formatString(((Player)sender).getLocation()));
		faction.addWarp(name, ((Player)sender).getLocation());
	}
	
	@Subcommand("removewarp|delwarp")
	@CommandCompletion("@faction-warps")
	public void delwarp(final CommandSender sender, String name) {
		name = name.toUpperCase();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}		
		if(!faction.hasPerm((Player)sender, FactionPerm.SETWARP)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(!faction.warpExists(name))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_WARP_NOT_FOUND, name);
			return;
		}
		playerData.sendMessage(Lang_EN_US.FACTION_WARP_REMOVED, LocationUtil.formatString(((Player)sender).getLocation()));
		faction.removeWarp(name);
	}
	
	@Subcommand("warp")
	@CommandCompletion("@faction-warps")
	public void warp(final CommandSender sender, String name) {
		name = name.toUpperCase();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang_EN_US.NOT_IN_FACTION);
			return;
		}		
		if(!faction.hasPerm((Player)sender, FactionPerm.WARP)){
			playerData.sendMessage(Lang_EN_US.FACTION_NO_PERM);
			return;
		}
		if(!faction.warpExists(name))
		{
			playerData.sendMessage(Lang_EN_US.FACTION_WARP_NOT_FOUND, name);
			return;
		}

		ClusterCore.getInstance().getTeleportQueue().scheduleTeleport(playerData.getPlayer(), 3000L, faction.getWarp(name));
	}
	
	@Subcommand("who")
	public void who(final CommandSender sender, String tag) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
		if(faction == null) {
			playerData.sendMessage(Lang_EN_US.NO_FACTION_WITH_TAG, tag);
			return;
		}
		playerData.sendMessage("");
		playerData.sendMessage("");
		playerData.sendMessage("");
		playerData.sendMessage("");
		playerData.sendMessage("");
		playerData.sendMessage("");
	}
	
}



























