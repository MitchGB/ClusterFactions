package com.clusterfactions.clustercore.core.command.impl.factions;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.chat.ChatMessageMode;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.FactionsManager;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.factions.util.FactionPerm;
import com.clusterfactions.clustercore.core.factions.util.FactionPlayerRemoveReason;
import com.clusterfactions.clustercore.core.factions.util.FactionRole;
import com.clusterfactions.clustercore.core.inventory.impl.faction.perm.MainPermissionMenu;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.NumberUtil;
import com.clusterfactions.clustercore.util.location.LocationUtil;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

@CommandAlias("f|factions|faction|fact|fs")
public class FactionsCommand extends BaseCommand{
	
	@Default
	public void execute(final CommandSender sender) {
		help(sender, 1);
	}
	
	@HelpCommand
	public void help(final CommandSender sender, @Default("1") int page) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		List<String> commandList = ClusterCore.getInstance().getCommandManager().allCommands(this);
		
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
	
	@Subcommand("alert|weewoo")
	public void alert(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!playerData.isInFaction()){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		if(!faction.hasPerm(playerData.getPlayer(), FactionPerm.ALERT)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		faction.messageAll(Lang.ALERT_RAID);
	}
	
	@Subcommand("create")
	public void create(final CommandSender sender, String tag){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(playerData.isInFaction()) {
			playerData.sendMessage(Lang.ALREADY_IN_FACTION);
			return;
		}
		if(tag.contains(" "))
		{
			playerData.sendMessage(Lang.TAG_CANNOT_CONTAIN_SPACE);
			return;
		}
		if(tag.length() > 10)
		{
			playerData.sendMessage(Lang.FACTION_TAG_TOO_LONG);
			return;
		}
		if(ClusterCore.getInstance().getMongoHook().valueExists("factionLower", tag.toLowerCase(), "factions"))
		{
			playerData.sendMessage(Lang.FACTION_TAG_TAKEN);
			return;
		}
		
		ClusterCore.getInstance().getFactionsManager().createFaction((Player)sender, tag);
	}
	
	@Subcommand("disband")
	public void disband(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		if(!faction.getPlayerRole(playerData.getPlayer()).equals(FactionRole.LEADER)) {
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		FactionsManager facManager = ClusterCore.getInstance().getFactionsManager();
		if(!facManager.canDisband(faction))	
		{
			ClusterCore.getInstance().getFactionsManager().setDisbandTimer(faction);
			playerData.sendMessage(Lang.FACTION_DISBAND_TIMER);
		}else{
			faction.disband();
		}
	}
	
	@Subcommand("leave")
	public void leave(final CommandSender sender){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);		
		if(!playerData.isInFaction()){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
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
			inviterData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(inviterData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.INVITE)){
			inviterData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(faction.getPlayerCount() >= 10)
		{
			inviterData.sendMessage(Lang.FACTION_MAXIMUM_PLAYERS);
			return;
		}
		if(faction.getBannedPlayers() != null && faction.getBannedPlayers().contains(player.getPlayer().getUniqueId()))
		{
			inviterData.sendMessage(Lang.PLAYER_IS_CURRENTLY_BANNED);
			return;
		}
		if(faction.inviteListContains(player.getPlayer())){
			inviterData.sendMessage(Lang.PLAYER_ALREADY_INVITED);
			return;
		}
		if(inviteeData.isInFaction()) {
			inviterData.sendMessage(Lang.PLAYER_ALREADY_IN_FACTION);
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
			inviterData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(inviterData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.INVITE)){
			inviterData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(!faction.inviteListContains(player.getPlayer())){
			inviterData.sendMessage(Lang.PLAYER_NOT_ON_INVITE_LIST);
			return;
		}
		if(inviteeData.isInFaction()) {
			inviterData.sendMessage(Lang.PLAYER_ALREADY_IN_FACTION);
			return;
		}
		inviterData.sendMessage(Lang.UNINVITED_PLAYER);
		faction.uninvitePlayer(player.getPlayer());
	}
	
	@Subcommand("join")
	@CommandCompletion("@all-factions")
	public void join(final CommandSender sender, String tag) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
		if(faction == null) {
			playerData.sendMessage(Lang.NO_FACTION_WITH_TAG, tag);
			return;
		}
		if(playerData.getFaction() != null){
			playerData.sendMessage(Lang.ALREADY_IN_FACTION);
			return;
		}
		if(faction.getPlayerCount() >= 10)
		{
			playerData.sendMessage(Lang.FACTION_MAXIMUM_PLAYERS);
			return;
		}
		if(!faction.inviteListContains (((Player)sender) )){
			playerData.sendMessage(Lang.NO_PENDING_FACTION_INVITE, tag);
			return;
		}
		faction.acceptInvite((Player)sender);
		
	}
	
	@Subcommand("sethome")
	public void sethome(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		if(!faction.hasPerm((Player)sender, FactionPerm.SETHOME)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		playerData.sendMessage(Lang.FACTION_HOME_SET,LocationUtil.formatString(((Player)sender).getLocation()));
		faction.setFactionHome(((Player)sender).getLocation());
		faction.saveData();
	}
	
	@Subcommand("home")
	public void home(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}		
		if(ClusterCore.getInstance().getCombatManager().isTagged(playerData))
		{
			playerData.sendMessage(Lang.PLAYER_COMBAT_TAGGED);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.HOME)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(faction.getFactionHome() == null)
		{
			playerData.sendMessage(Lang.NO_FACTION_HOME_SET);
			return;
		}
		ClusterCore.getInstance().getTeleportQueue().scheduleTeleport(playerData.getPlayer(), 3000L, faction.getFactionHome());
	}
	
	@Subcommand("chat")
	@CommandCompletion("@chat-message-modes")
	public void chat(final CommandSender sender, ChatMessageMode mode) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(playerData.getFaction() == null && (mode == ChatMessageMode.FACTION || mode == ChatMessageMode.ALLY)){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
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
			playerData.sendMessage(Lang.RADIUS_CANNOT_BE_NEGATIVE);
			return;
		}
		if(radius > 5) {
			playerData.sendMessage(Lang.MAXIMUM_CLAIM_RADIUS);
			return;
		}
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		if(factionClaimed != null) {
			playerData.sendMessage(Lang.CHUNK_ALREADY_CLAIMED, ClusterCore.getInstance().getFactionsManager().getFaction(factionClaimed).getFactionTag());
			return;
		}
		if(faction.getSparePower() < (radius == 0 ? 1 : Math.pow(radius, 4))) {
			playerData.sendMessage(Lang.FACTION_NOT_ENOUGH_POWER);
			return;
		}
		if(radius == 0){

			playerData.sendMessage(Lang.SUCCESSFULL_CLAIM, "1");
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
			playerData.sendMessage(Lang.RADIUS_CANNOT_BE_NEGATIVE);
			return;
		}
		if(x > 5 || z > 5) {
			playerData.sendMessage(Lang.MAXIMUM_CLAIM_RADIUS);
			return;
		}
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(faction.getSparePower() < (x*x)*(z*z)) {
			playerData.sendMessage(Lang.FACTION_NOT_ENOUGH_POWER);
			return;
		}
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		if(factionClaimed != null) {
			playerData.sendMessage(Lang.CHUNK_ALREADY_CLAIMED, ClusterCore.getInstance().getFactionsManager().getFaction(factionClaimed).getFactionTag());
			return;
		}
		
		claimManager.claimArea(((Player)sender), x, z);
	}
	
	@Subcommand("unclaim")
	public void unclaim(final CommandSender sender, @Default("0") int radius)
	{
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(radius > 5) {
			playerData.sendMessage(Lang.MAXIMUM_CLAIM_RADIUS);
			return;
		}
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		if(factionClaimed == null) {
			playerData.sendMessage(Lang.CHUNK_NOT_CLAIMED);
			return;
		}
		if(!playerData.getFaction().equals(factionClaimed)){
			playerData.sendMessage(Lang.NOT_YOUR_CLAIM);
			return;
		}
		if(radius == 0){

			playerData.sendMessage(Lang.SUCCESSFULL_UNCLAIM);
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
			playerData.sendMessage(Lang.MAXIMUM_CLAIM_RADIUS);
			return;
		}
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());		
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		if(!playerData.getFaction().equals(factionClaimed)){
			playerData.sendMessage(Lang.NOT_YOUR_CLAIM);
			return;
		}
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(factionClaimed == null) {
			playerData.sendMessage(Lang.CHUNK_NOT_CLAIMED);
			return;
		}

		if(faction.getSparePower() < 1) {
			playerData.sendMessage(Lang.FACTION_NOT_ENOUGH_POWER);
			return;
		}
		claimManager.removeClaimArea(((Player)sender), x, z);
	}
	
	@Subcommand("overclaim")
	public void overclaim(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());		
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		
		Vector2Integer chunkLoc = claimManager.getChunkVector(((Player)sender).getLocation());
		UUID factionClaimed = claimManager.chunkClaimed(chunkLoc);
		Faction claimedFaction = ClusterCore.getInstance().getFactionsManager().getFaction(factionClaimed);
		if(playerData.getFaction().equals(factionClaimed)){
			playerData.sendMessage(Lang.OVERCLAIM_YOUR_OWN_CLAIM);
			return;
		}
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(!(claimedFaction.getSparePower() < 0))
		{
			playerData.sendMessage(Lang.CANNOT_OVERCLAIM_THIS_CLAIM);
			return;
		}
		if(claimManager.getEmptyClaimNeighbours(playerData.getPlayer().getLocation()) < 1)
		{
			playerData.sendMessage(Lang.CANNOT_OVERCLAIM_THIS_CLAIM);
			return;
		}
		playerData.sendMessage(Lang.SUCCESSFUL_OVERCLAIM, claimedFaction.getFactionTag());
		claimManager.removeClaimChunk(chunkLoc, claimedFaction);
		claimManager.claimChunk(chunkLoc, faction);
	}
	
	@Subcommand("isclaimed")
	public void isclaimed(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		UUID factionClaimed = claimManager.chunkClaimed(claimManager.getChunkVector(((Player)sender).getLocation()));
		playerData.sendMessage(factionClaimed == null ? "This chunk is not claimed" : "This chunk is claimed by " + ClusterCore.getInstance().getFactionsManager().getFaction(factionClaimed).getFactionTag());
	}
	
	@Subcommand("showclaims|claims|claimlist")
	public void showclaims(final CommandSender sender) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());		
		if(faction.getClaimedChunks() == null || faction.getClaimedChunks().isEmpty())
		{
			senderData.sendMessage(Lang.CLAIMLIST_EMPTY);
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
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}		
		if(faction.getPlayerRole(playerData.getPlayer()).getWeight() != FactionRole.LEADER.getWeight()){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
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
			senderData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.PROMOTE)){
			senderData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang.PLAYER_NOT_IN_FACTION);
			return;
		}
		if(faction.getPlayerRole(player.getPlayer()).equals(FactionRole.COLEADER))
		{
			senderData.sendMessage(Lang.FACTION_CANNOT_PROMOTE_TO_LEADER);
			return;
		}
		if(faction.getPlayerRole((Player)sender).getWeight() == faction.getPlayerRole(player.getPlayer()).getWeight())
		{
			senderData.sendMessage(Lang.FACTION_CANNOT_PROMOTE_PLAYER);
			return;
		}
		faction.promotePlayer(player.getPlayer().getUniqueId());
		faction.messageAll(Lang.FACTION_PROMOTED_PLAYER, player.getPlayer().getName(), faction.getPlayerRole(player.getPlayer()).toString());
	}
	
	@Subcommand("demote")
	@CommandCompletion("@faction-online-players")
	public void demote(final CommandSender sender, OnlinePlayer player) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.PROMOTE)){
			senderData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang.PLAYER_NOT_IN_FACTION);
			return;
		}
		if(faction.getPlayerRole(player.getPlayer()).getWeight() == 1 || faction.getPlayerRole((Player)sender).getWeight() < faction.getPlayerRole(player.getPlayer()).getWeight())
		{
			senderData.sendMessage(Lang.FACTION_CANNOT_DEMOTE_PLAYER);
			return;
		}
		faction.demotePlayer(player.getPlayer().getUniqueId());
		faction.messageAll(Lang.FACTION_DEMOTED_PLAYER, player.getPlayer().getName(), faction.getPlayerRole(player.getPlayer()).toString());
	}
	
	@Subcommand("kick")
	@CommandCompletion("@faction-online-players")
	public void kick(final CommandSender sender, OnlinePlayer player) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());		
		if(!faction.hasPerm((Player)sender, FactionPerm.KICK)){
			senderData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang.PLAYER_NOT_IN_FACTION);
			return;
		}

		if(faction.getPlayerRole((Player)sender).getWeight() <= faction.getPlayerRole(player.getPlayer()).getWeight())
		{
			senderData.sendMessage(Lang.FACTION_CANNOT_KICK_PLAYER);
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
			senderData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.BAN)){
			senderData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang.PLAYER_NOT_IN_FACTION);
			return;
		}
		if(faction.getPlayerRole((Player)sender).getWeight() <= faction.getPlayerRole(player.getPlayer()).getWeight())
		{
			senderData.sendMessage(Lang.FACTION_CANNOT_BAN_PLAYER);
			return;
		}
		faction.banPlayer(player.getPlayer());
	}
	
	@Subcommand("unban")
	public void unban(final CommandSender sender, OnlinePlayer player) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());
		if(!faction.hasPerm((Player)sender, FactionPerm.BAN)){
			senderData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(faction.getBannedPlayers() != null)
		if(!faction.getBannedPlayers().contains(player.getPlayer().getUniqueId()))
		{
			senderData.sendMessage(Lang.PLAYER_NOT_BANNED);
			return;
		}
		faction.unbanPlayer(player.getPlayer());
		senderData.sendMessage(Lang.FACTION_UNBANNED_PLAYER, player.getPlayer().getName());
	}
	
	@Subcommand("banlist")
	public void banlist(final CommandSender sender) {
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());
		if(faction.getBannedPlayers() == null || faction.getBannedPlayers().isEmpty())
		{
			senderData.sendMessage(Lang.BAN_LIST_EMPTY);
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
	public class allyCommand extends BaseCommand{
		@Subcommand("player")
		@CommandCompletion("@players")
		public void ally(final CommandSender sender, OnlinePlayer target) {
			Player player = target.getPlayer();
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
			Faction targetFaction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
			if(targetFaction == null) {
				playerData.sendMessage(Lang.PLAYER_NOT_IN_FACTION);
				return;
			}
			ally(sender, targetFaction.getFactionTag());
		}
		
		@Default
		@CommandCompletion("@all-factions")
		public void ally(final CommandSender sender, String tag) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
			Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
			Faction target = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
			if(faction == null){
				playerData.sendMessage(Lang.NOT_IN_FACTION);
				return;
			}		
			if(!faction.hasPerm((Player)sender, FactionPerm.ALLY)){
				playerData.sendMessage(Lang.FACTION_NO_PERM);
				return;
			}
			if(target == null) {
				playerData.sendMessage(Lang.NO_FACTION_WITH_TAG, tag);
				return;
			}
			if(faction.isSame(target)){
				playerData.sendMessage(Lang.FACTION_CANNOT_INVITE_SELF_ALLY);
				return;
			}
			if(faction.isAlly(target)){
				playerData.sendMessage(Lang.FACTION_ALREADY_ALLY, target.getFactionTag());
				return;
			}
			if(faction.isInvitedAlly(target)){
				playerData.sendMessage(Lang.FACTION_ALREADY_INVITED_ALLY, target.getFactionTag());
				return;
			}
			faction.setFactionAlly(target);
				
		}
		
	}
	
	@Subcommand("neutral")
	public class neutralCommand extends BaseCommand{
		@Subcommand("player")
		@CommandCompletion("@players")
		public void neutral(final CommandSender sender, OnlinePlayer target) {
			Player player = target.getPlayer();
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
			Faction targetFaction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
			if(targetFaction == null) {
				playerData.sendMessage(Lang.PLAYER_NOT_IN_FACTION);
				return;
			}
			neutral(sender, targetFaction.getFactionTag());
		}
		
		@Default
		@CommandCompletion("@all-factions")
		public void neutral(final CommandSender sender, String tag) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
			Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
			Faction target = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
			if(playerData.getFaction() == null){
				playerData.sendMessage(Lang.NOT_IN_FACTION);
				return;
			}		
			if(!faction.hasPerm((Player)sender, FactionPerm.ALLY)){
				playerData.sendMessage(Lang.FACTION_NO_PERM);
				return;
			}
			if(target == null) {
				playerData.sendMessage(Lang.NO_FACTION_WITH_TAG, tag);
				return;
			}
			if(faction.isSame(target)){
				playerData.sendMessage(Lang.FACTION_CANNOT_INVITE_SELF_NEUTRAL);
				return;
			}
			if(faction.isNeutral(target)){
				playerData.sendMessage(Lang.FACTION_ALREADY_NEUTRAL, target.getFactionTag());
				return;
			}
			if(faction.isInvitedNeutral(target) && !faction.isEnemy(target)){
				playerData.sendMessage(Lang.FACTION_ALREADY_INVITED_NEUTRAL, target.getFactionTag());
				return;
			}
			faction.setFactionNeutral(target);
			
		}
	}
	
	@Subcommand("enemy")
	public class enemyCommand extends BaseCommand{
		@Subcommand("player")
		@CommandCompletion("@players")
		public void neutral(final CommandSender sender, OnlinePlayer target) {
			Player player = target.getPlayer();
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
			Faction targetFaction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
			if(targetFaction == null) {
				playerData.sendMessage(Lang.PLAYER_NOT_IN_FACTION);
				return;
			}
			enemy(sender, targetFaction.getFactionTag());
		}
		
		@Default
		@CommandCompletion("@all-factions")
		public void enemy(final CommandSender sender, String tag) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
			Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
			Faction target = ClusterCore.getInstance().getFactionsManager().getFaction(tag);
			if(playerData.getFaction() == null){
				playerData.sendMessage(Lang.NOT_IN_FACTION);
				return;
			}		
			if(!faction.hasPerm((Player)sender, FactionPerm.ALLY)){
				playerData.sendMessage(Lang.FACTION_NO_PERM);
				return;
			}
			if(target == null) {
				playerData.sendMessage(Lang.NO_FACTION_WITH_TAG, tag);
				return;
			}
			if(faction.isSame(target)){
				playerData.sendMessage(Lang.FACTION_CANNOT_INVITE_SELF_ENEMY);
				return;
			}
			if(faction.isEnemy(target)){
				playerData.sendMessage(Lang.FACTION_ALREADY_ENEMY, target.getFactionTag());
				return;
			}
			faction.setFactionEnemy(target);
		}
	}
	
	@Subcommand("setwarp")
	public void setwarp(final CommandSender sender, String name) {
		name = name.toUpperCase();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}		
		if(!faction.hasPerm((Player)sender, FactionPerm.SETWARP)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(faction.warpExists(name))
		{
			playerData.sendMessage(Lang.FACTION_WARP_NAME_TAKEN, name);
			return;
		}
		playerData.sendMessage(Lang.FACTION_WARP_SET, LocationUtil.formatString(((Player)sender).getLocation()));
		faction.addWarp(name, ((Player)sender).getLocation());
	}
	
	@Subcommand("removewarp|delwarp")
	@CommandCompletion("@faction-warps")
	public void delwarp(final CommandSender sender, String name) {
		name = name.toUpperCase();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}		
		if(!faction.hasPerm((Player)sender, FactionPerm.SETWARP)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(!faction.warpExists(name))
		{
			playerData.sendMessage(Lang.FACTION_WARP_NOT_FOUND, name);
			return;
		}
		playerData.sendMessage(Lang.FACTION_WARP_REMOVED, LocationUtil.formatString(((Player)sender).getLocation()));
		faction.removeWarp(name);
	}
	
	@Subcommand("warp")
	@CommandCompletion("@faction-warps")
	public void warp(final CommandSender sender, String name) {
		name = name.toUpperCase();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}		
		if(!faction.hasPerm((Player)sender, FactionPerm.WARP)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(!faction.warpExists(name))
		{
			playerData.sendMessage(Lang.FACTION_WARP_NOT_FOUND, name);
			return;
		}

		ClusterCore.getInstance().getTeleportQueue().scheduleTeleport(playerData.getPlayer(), 3000L, faction.getWarp(name));
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
	
	@Subcommand("coords|position|coordinates")
	@CommandCompletion("@faction-online-players")
	public void coords(final CommandSender sender, OnlinePlayer player) {
		Player oPlayer = player.getPlayer();
		PlayerData senderData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(senderData.getFaction());	
		if(senderData.getFaction() == null){
			senderData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}	
		if(!faction.hasPerm((Player)sender, FactionPerm.COORDS)){
			senderData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(playerData.getFaction() == null || !playerData.getFaction().toString().equals(faction.getFactionID().toString())) {
			senderData.sendMessage(Lang.PLAYER_NOT_IN_FACTION);
			return;
		}
		senderData.sendMessage(Lang.PLAYER_COORDS, oPlayer.getName(), LocationUtil.formatString(oPlayer.getLocation()), oPlayer.getLocation().getWorld().getName());
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



























