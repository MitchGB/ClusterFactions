package com.clusterfactions.clustercore.core.command.impl.admin.claim;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.claim.AdminClaimType;
import com.clusterfactions.clustercore.core.factions.claim.ChunkOwner;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.factions.util.FactionPerm;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("admin|adminstrator|administration")
@CommandPermission("clustercore.admin")
public class AdminClaimCommand extends BaseCommand{
	@Subcommand("claim")
	@CommandCompletion("@admin-claim-types")
	public void claim(final CommandSender sender, AdminClaimType type, @Default("0") int radius){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
	
		if(radius < 0){
			playerData.sendMessage(Lang.RADIUS_CANNOT_BE_NEGATIVE);
			return;
		}
		if(radius > 5) {
			playerData.sendMessage(Lang.MAXIMUM_CLAIM_RADIUS);
			return;
		}
		if(radius == 0){
			playerData.sendMessage(Lang.SUCCESSFULL_CLAIM, "1");
			claimManager.claimChunk(claimManager.getChunkVector(playerData.getPlayer().getLocation()), new ChunkOwner(type.toString(), true));
			return;
		}
		claimManager.claimArea(playerData.getPlayer(), playerData.getPlayer().getLocation(), new ChunkOwner(type.toString(), true), radius, radius);
	}
	
	@Subcommand("claim")
	@CommandCompletion("@admin-claim-types")
	public void claim(final CommandSender sender, AdminClaimType type, int x, int z){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
	
		if(x < 0 || z < 0){
			playerData.sendMessage(Lang.RADIUS_CANNOT_BE_NEGATIVE);
			return;
		}
		if(x > 5 || z > 5) {
			playerData.sendMessage(Lang.MAXIMUM_CLAIM_RADIUS);
			return;
		}

		claimManager.claimArea(playerData.getPlayer(), playerData.getPlayer().getLocation(), new ChunkOwner(type.toString(), true), x, z);
		
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
		if(chunkOwner.isNull()) {
			playerData.sendMessage(Lang.CHUNK_NOT_CLAIMED);
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
}
