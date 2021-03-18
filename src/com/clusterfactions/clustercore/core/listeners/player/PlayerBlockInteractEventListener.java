package com.clusterfactions.clustercore.core.listeners.player;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.crate.CrateManager;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.inventory.impl.block.CraftingTableInventory;
import com.clusterfactions.clustercore.core.inventory.impl.block.FurnaceInventory;
import com.clusterfactions.clustercore.core.listeners.events.player.PlayerBlockInteractEvent;

public class PlayerBlockInteractEventListener implements Listener{

	@EventHandler
	public void CrateBlockInteractEvent(PlayerBlockInteractEvent e) {
		CrateManager manager = ClusterCore.getInstance().getCrateManager();
		if(manager.isCrate(e.getBlock().getLocation())) {
			if(ClusterCore.getInstance().getPlayerManager().getPlayerData(e.getPlayer()).isAdminOverrideMode() && e.getPlayer().isSneaking()) return;
			manager.getCrateHandler(manager.getCrate(e.getBlock().getLocation())).interactHandler().exec(e);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerInteractEventFurnace(PlayerBlockInteractEvent e) {
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		if(!claimManager.canInteractBlock(e.getBlock().getLocation(), e.getPlayer())){
			e.setCancelled(true);
			return;
		}
		
		Block block = e.getBlock();
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(block == null) return;
		if(block.getType() == Material.FURNACE){
			e.setCancelled(true);
			if(ClusterCore.getInstance().getInventoryManager().blockCache.containsKey(block)){
				ClusterCore.getInstance().getInventoryManager().blockCache.get(block).openInventory(e.getPlayer());
			}else
				new FurnaceInventory(e.getPlayer(), block).openInventory(e.getPlayer());
		}
	}	
	
	@EventHandler
	public void PlayerInteractEventCraftingTable(PlayerBlockInteractEvent e) {
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		if(!claimManager.canInteractBlock(e.getBlock().getLocation(), e.getPlayer())){
			e.setCancelled(true);
			return;
		}
		
		Block block = e.getBlock();
		
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(block == null) return;
		if(block.getType() == Material.CRAFTING_TABLE){
			e.setCancelled(true);
			new CraftingTableInventory(e.getPlayer(), block).openInventory(e.getPlayer());
		}
	}	
	
	@EventHandler
	public void PlayerInteractEventNoteblock(PlayerBlockInteractEvent e) {
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		if(!claimManager.canInteractBlock(e.getBlock().getLocation(), e.getPlayer())){
			e.setCancelled(true);
			return;
		}
		
		Block block = e.getBlock();
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(e.getPlayer().isSneaking()) return;
		if(block == null) return;
		if(block.getType() == Material.NOTE_BLOCK){
			e.setCancelled(true);
		}
	}	
	
}
