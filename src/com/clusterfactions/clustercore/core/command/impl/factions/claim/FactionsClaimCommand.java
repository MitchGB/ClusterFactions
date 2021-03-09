package com.clusterfactions.clustercore.core.command.impl.factions.claim;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.claim.ChunkOwner;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.factions.util.FactionPerm;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("f|factions|faction|fact|fs")
public class FactionsClaimCommand extends BaseCommand{
	
	@Subcommand("claim")
	public void claim(final CommandSender sender, @Default("0") int radius){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		ChunkOwner chunkOwner = claimManager.getChunkOwner( claimManager.getChunkVector(playerData.getPlayer().getLocation() ));
		if(radius < 0){
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
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(chunkOwner.isAdmin()) {
			playerData.sendMessage(Lang.CANNOT_CLAIM_THIS_AREA);	
			return;
		}
		if(!chunkOwner.isNull()) {
			
			if(chunkOwner.getFaction().equals(faction)){
				playerData.sendMessage(Lang.OVERCLAIM_YOUR_OWN_CLAIM);
				return;
			}
			if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
				playerData.sendMessage(Lang.FACTION_NO_PERM);
				return;
			}
			if(!(faction.getSparePower() < 0)){
				playerData.sendMessage(Lang.CANNOT_OVERCLAIM_THIS_CLAIM);
				return;
			}
			if(claimManager.getEmptyClaimNeighbours(playerData.getPlayer().getLocation()) < 1) {
				playerData.sendMessage(Lang.CANNOT_OVERCLAIM_THIS_CLAIM);
				return;
			}
			
			playerData.sendMessage(Lang.SUCCESSFUL_OVERCLAIM, chunkOwner.getFaction().getFactionTag());
			Vector2Integer chunkLoc = claimManager.getChunkVector(playerData.getPlayer().getLocation());
			claimManager.unclaimChunk(chunkLoc, chunkOwner.getFaction());
			claimManager.claimChunk(chunkLoc, faction);
			return;
			
		}
		if(faction.getSparePower() < (radius == 0 ? 1 : Math.pow(radius, 4))) {
			playerData.sendMessage(Lang.FACTION_NOT_ENOUGH_POWER);
			return;
		}
		if(radius == 0){
			playerData.sendMessage(Lang.SUCCESSFULL_CLAIM, "1");
			claimManager.claimChunk(claimManager.getChunkVector(playerData.getPlayer().getLocation()), faction);
			return;
		}
		claimManager.claimArea(playerData.getPlayer(), playerData.getPlayer().getLocation(), new ChunkOwner(faction.getFactionID().toString()), radius, radius);
	}
	
	@Subcommand("claim")
	public void claim(final CommandSender sender, int x, int z){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		ChunkOwner chunkOwner = claimManager.getChunkOwner( claimManager.getChunkVector(playerData.getPlayer().getLocation() ));
		if(x < 0 || z < 0){
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
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(chunkOwner.isAdmin()) {
			playerData.sendMessage(Lang.CANNOT_CLAIM_THIS_AREA);	
			return;
		}
		if(!chunkOwner.isNull()) {
			playerData.sendMessage(Lang.CHUNK_ALREADY_CLAIMED, chunkOwner.getFaction().getFactionTag());
			return;
		}
		if(faction.getSparePower() < (x*x)*(z*z)) {
			playerData.sendMessage(Lang.FACTION_NOT_ENOUGH_POWER);
			return;
		}
		claimManager.claimArea(playerData.getPlayer(), playerData.getPlayer().getLocation(), new ChunkOwner(faction.getFactionID().toString()), x, z);
		
	}
	
	@Subcommand("unclaim")
	public void unclaim(final CommandSender sender, @Default("0") int radius){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		ChunkOwner chunkOwner = claimManager.getChunkOwner( claimManager.getChunkVector(playerData.getPlayer().getLocation() ));
		if(radius > 5) {
			playerData.sendMessage(Lang.MAXIMUM_CLAIM_RADIUS);
			return;
		}
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(chunkOwner.isNull()) {
			playerData.sendMessage(Lang.CHUNK_NOT_CLAIMED);
			return;
		}
		if(chunkOwner.isAdmin() || !playerData.getFaction().equals(chunkOwner.getFaction().getFactionID())){
			playerData.sendMessage(Lang.NOT_YOUR_CLAIM);
			return;
		}
		if(radius == 0){
			playerData.sendMessage(Lang.SUCCESSFULL_UNCLAIM);
			claimManager.unclaimChunk(claimManager.getChunkVector(playerData.getPlayer().getLocation()), faction);
			return;
		}
		
		claimManager.removeClaimArea(((Player)sender), radius, radius);
	}
	
	@Subcommand("unclaim")
	public void unclaim(final CommandSender sender, int x, int z){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());	
		ChunkOwner chunkOwner = claimManager.getChunkOwner( claimManager.getChunkVector(playerData.getPlayer().getLocation() ));
		if(x > 5 || z > 5) {
			playerData.sendMessage(Lang.MAXIMUM_CLAIM_RADIUS);
			return;
		}
		if(playerData.getFaction() == null){
			playerData.sendMessage(Lang.NOT_IN_FACTION);
			return;
		}
		if(chunkOwner.isAdmin() || !playerData.getFaction().equals(chunkOwner.getFaction().getFactionID())){
			playerData.sendMessage(Lang.NOT_YOUR_CLAIM);
			return;
		}
		if(!faction.hasPerm((Player)sender, FactionPerm.CLAIM)){
			playerData.sendMessage(Lang.FACTION_NO_PERM);
			return;
		}
		if(chunkOwner.isNull()) {
			playerData.sendMessage(Lang.CHUNK_NOT_CLAIMED);
			return;
		}
		if(faction.getSparePower() < 1) {
			playerData.sendMessage(Lang.FACTION_NOT_ENOUGH_POWER);
			return;
		}
		claimManager.removeClaimArea(((Player)sender), x, z);
	}
	
	@Subcommand("isclaimed")
	public void isclaimed(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		
		ChunkOwner chunkOwner = claimManager.getChunkOwnerCache( ((Player)sender).getLocation() );
		playerData.sendMessage(chunkOwner.isNull() ? "This chunk is not claimed" : "This chunk is claimed by " + chunkOwner.getFaction().getFactionTag());
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
	
}



























