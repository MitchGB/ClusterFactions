package com.clusterfactions.clustercore.core.listeners.block;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.claim.ChunkOwner;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.location.LocationUtil;
import com.clusterfactions.clustercore.util.location.Vector2Integer;

public class BlockPlaceEventListener implements Listener{

	@EventHandler(priority = EventPriority.HIGHEST)
	public void BlockPlaceEvent(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		//Crate
		if(playerData.getCratePlaceMode() != null) {
			ClusterCore.getInstance().getCrateManager().setCrate(e.getBlock().getLocation(), playerData.getCratePlaceMode());
			playerData.sendMessage(Lang.PLACED_CRATE, playerData.getCratePlaceMode(), LocationUtil.formatString(e.getBlock().getLocation()));
			playerData.setCratePlaceMode(null);
			return;
		}
		
		//
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		
		Vector2Integer chunk = claimManager.getChunkVector(e.getPlayer().getLocation());
		
		ChunkOwner chunkOwner = claimManager.getChunkOwnerCache(chunk);
		
		if(!claimManager.canManipulateBlock(e.getBlock().getLocation(), player)){
			e.setCancelled(true);
			if(chunkOwner.isAdmin())
				return;
			
			playerData.sendMessage(Lang.BUILDING_NOT_PERMITTED, chunkOwner.getFaction().getFactionTag());
			return;
		}
		
	}
}