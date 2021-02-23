package com.clusterfactions.clustercore.listeners.block;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.factions.util.FactionPerm;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

public class BlockPlaceEventListener implements Listener{

	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		Faction faction = playerData.getFaction() == null ? null : ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		
		Vector2Integer chunk = claimManager.getChunkVector(e.getPlayer().getLocation());
		
		UUID chunkOwner = claimManager.chunkClaimed(chunk);
		if(chunkOwner == null) return;
		if(faction == null || !chunkOwner.toString().equals(faction.getFactionID().toString()))
		{
			playerData.sendMessage(Lang.BUILDING_NOT_PERMITTED, ClusterCore.getInstance().getFactionsManager().getFaction(chunkOwner).getFactionTag());
			e.setCancelled(true);
			return;
		}
		if(chunkOwner.toString().equals(faction.getFactionID().toString()) && !faction.hasPerm(player, FactionPerm.BUILD))
		{
			playerData.sendMessage(Lang.BUILDING_NOT_PERMITTED, ClusterCore.getInstance().getFactionsManager().getFaction(chunkOwner).getFactionTag());
			e.setCancelled(true);
			return;
		}
		
	}
}