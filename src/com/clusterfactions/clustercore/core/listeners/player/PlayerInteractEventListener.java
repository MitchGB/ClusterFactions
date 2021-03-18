package com.clusterfactions.clustercore.core.listeners.player;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.claim.ChunkOwner;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.factions.util.FactionPerm;
import com.clusterfactions.clustercore.core.fx.spectator.cinematic.CinematicSequence;
import com.clusterfactions.clustercore.core.inventory.impl.block.CraftingTableInventory;
import com.clusterfactions.clustercore.core.inventory.impl.block.FurnaceInventory;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.location.LocationUtil;
import com.clusterfactions.clustercore.util.location.Vector2Integer;
import com.google.common.collect.ImmutableSet;

public class PlayerInteractEventListener implements Listener{
	public final ImmutableSet<Material> restrictedChests = ImmutableSet.<Material>builder()
			.add(Material.CHEST)
			.add(Material.TRAPPED_CHEST)
			.add(Material.BLACK_SHULKER_BOX)
			.add(Material.BLUE_SHULKER_BOX)
			.add(Material.BROWN_SHULKER_BOX)
			.add(Material.CYAN_SHULKER_BOX)
			.add(Material.GRAY_SHULKER_BOX)
			.add(Material.GREEN_SHULKER_BOX)
			.add(Material.LIGHT_BLUE_SHULKER_BOX)
			.add(Material.LIGHT_GRAY_SHULKER_BOX)
			.add(Material.LIME_SHULKER_BOX)
			.add(Material.MAGENTA_SHULKER_BOX)
			.add(Material.ORANGE_SHULKER_BOX)
			.add(Material.PINK_SHULKER_BOX)
			.add(Material.PURPLE_SHULKER_BOX)
			.add(Material.PURPLE_SHULKER_BOX)
			.add(Material.RED_SHULKER_BOX)
			.add(Material.WHITE_SHULKER_BOX)
			.add(Material.YELLOW_SHULKER_BOX)
			.add(Material.SHULKER_BOX)
			.add(Material.BARREL)
			.build();
	public final ImmutableSet<Material> restrictedContainers = ImmutableSet.<Material>builder()
			.add(Material.DISPENSER)
			.add(Material.DROPPER)
			.add(Material.HOPPER)
			.add(Material.ENCHANTING_TABLE)
			.add(Material.FURNACE)
			.add(Material.BLAST_FURNACE)
			.add(Material.SMOKER)
			.add(Material.COMPOSTER)
			.add(Material.LOOM)
			.add(Material.CARTOGRAPHY_TABLE)
			.add(Material.FLETCHING_TABLE)
			.build();
	public final ImmutableSet<Material> restrictedInteractables = ImmutableSet.<Material>builder()
			.add(Material.ACACIA_DOOR)
			.add(Material.BIRCH_DOOR)
			.add(Material.CRIMSON_DOOR)
			.add(Material.DARK_OAK_DOOR)
			.add(Material.IRON_DOOR)
			.add(Material.JUNGLE_DOOR)
			.add(Material.OAK_DOOR)
			.add(Material.SPRUCE_DOOR)
			.add(Material.WARPED_DOOR)		
			
			.add(Material.ACACIA_TRAPDOOR)
			.add(Material.BIRCH_TRAPDOOR)
			.add(Material.CRIMSON_TRAPDOOR)
			.add(Material.DARK_OAK_TRAPDOOR)
			.add(Material.IRON_TRAPDOOR)
			.add(Material.JUNGLE_TRAPDOOR)
			.add(Material.OAK_TRAPDOOR)
			.add(Material.SPRUCE_TRAPDOOR)
			.add(Material.WARPED_TRAPDOOR)
			
			.add(Material.ACACIA_BUTTON)
			.add(Material.BIRCH_BUTTON)
			.add(Material.CRIMSON_BUTTON)
			.add(Material.DARK_OAK_BUTTON)
			.add(Material.JUNGLE_BUTTON)
			.add(Material.OAK_BUTTON)
			.add(Material.POLISHED_BLACKSTONE_BUTTON)
			.add(Material.SPRUCE_BUTTON)
			.add(Material.STONE_BUTTON)
			.add(Material.WARPED_BUTTON)
			
			.add(Material.LEVER)
			
			.add(Material.ACACIA_PRESSURE_PLATE)
			.add(Material.BIRCH_PRESSURE_PLATE)
			.add(Material.CRIMSON_PRESSURE_PLATE)
			.add(Material.DARK_OAK_PRESSURE_PLATE)
			.add(Material.JUNGLE_PRESSURE_PLATE)
			.add(Material.OAK_PRESSURE_PLATE)
			.add(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE)
			.add(Material.SPRUCE_PRESSURE_PLATE)
			.add(Material.STONE_PRESSURE_PLATE)
			.add(Material.WARPED_PRESSURE_PLATE)
			.build();
	
	@EventHandler(priority = EventPriority.HIGH)
	public void PlayerInteractEvent(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		Faction faction = playerData.getFaction() == null ? null : ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		
		Vector2Integer chunk = claimManager.getChunkVector(e.getPlayer().getLocation());
		
		ChunkOwner chunkOwner = claimManager.getChunkOwner(chunk);
		
		if(chunkOwner == null) return;
		if(playerData.isAdminOverrideMode()) return;
		
		Block block = e.getClickedBlock();
		
		FactionPerm permNeeded = null;
		
		if(block == null) return;
		if(restrictedChests.contains(block.getType()))
			permNeeded = FactionPerm.CHEST;
		
		if(restrictedContainers.contains(block.getType()))
			permNeeded = FactionPerm.CONTAINER;
		
		if(restrictedInteractables.contains(block.getType()))
			permNeeded = FactionPerm.INTERACT;
		
		
		if(permNeeded == null)
			return;
		if(chunkOwner.isNull())
			return;
		
		
		if(faction == null || !chunkOwner.toString().equals(faction.getFactionID().toString()))
		{
			e.setCancelled(true);
			return;
		}
		if(chunkOwner.toString().equals(faction.getFactionID().toString()) && !faction.hasPerm(player, permNeeded))
		{
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerInteractEventFurnace(PlayerInteractEvent e) {
		Block block = e.getClickedBlock();
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(e.isCancelled()) return;
		if(block == null) return;
		if(block.getType() == Material.FURNACE){
			e.setCancelled(true);
			if(ClusterCore.getInstance().getInventoryManager().blockCache.containsKey(block)){
				ClusterCore.getInstance().getInventoryManager().blockCache.get(block).openInventory(e.getPlayer());
			}else
				new FurnaceInventory(e.getPlayer(), block).openInventory(e.getPlayer());
		}
	}	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerInteractEventCraftingTable(PlayerInteractEvent e) {
		Block block = e.getClickedBlock();
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(e.isCancelled()) return;
		if(block == null) return;
		if(block.getType() == Material.CRAFTING_TABLE){
			e.setCancelled(true);
			new CraftingTableInventory(e.getPlayer(), block).openInventory(e.getPlayer());
		}
	}	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void PlayerInteractEventNoteblock(PlayerInteractEvent e) {
		Block block = e.getClickedBlock();
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(e.getPlayer().isSneaking()) return;
		if(block == null) return;
		if(block.getType() == Material.NOTE_BLOCK){
			e.setCancelled(true);
		}
	}	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void PlayerInteractEventCoalBlock(PlayerInteractEvent e) {
		Block block = e.getClickedBlock();
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(block == null) return;
		if(block.getType() != Material.COAL_BLOCK) return;
		Player player = e.getPlayer();
		Entity stand = ClusterCore.getInstance().getSpectatorManager().viewLoc(e.getPlayer(), block.getLocation().clone().add(.5, -1, -10));
		Location loc = block.getLocation();
		

		new CinematicSequence(true, LocationUtil.lerp(stand.getLocation(), loc.clone().add(.5, -1, -2), 100, stand) ).execute(player);



	}
}



























