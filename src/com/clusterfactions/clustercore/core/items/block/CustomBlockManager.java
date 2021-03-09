package com.clusterfactions.clustercore.core.items.block;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.items.ItemManager;
import com.clusterfactions.clustercore.core.items.block.breakhandler.BlockBreakHandler;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.PlaceableItem;
import com.clusterfactions.clustercore.util.ItemBuilder;

public class CustomBlockManager implements Listener{
	
	private HashMap<UUID, Long> timerMap = new HashMap<>();
	
	public BlockBreakHandler itemBreakHandler = new BlockBreakHandler();
	
	public CustomBlockManager() {
		ClusterCore.getInstance().registerListener(this);
	}
	
	public void placeBlock(PlayerInteractEvent e, ItemStack item) {
		if(e.isCancelled()) return;
		ItemManager itemManager = ClusterCore.getInstance().getItemManager();
		if(item == null || item.getType() == Material.AIR) return;
		CustomItem customItem = itemManager.getCustomItemHandler(itemManager.getCustomItemType(item));
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		
		BlockFace bf = e.getBlockFace();
		bf.getDirection();
		Location loc = e.getClickedBlock().getLocation().add(bf.getDirection());
		if(!claimManager.canManipulateBlock(loc, e.getPlayer())) {
			e.setCancelled(true);
			return;
		}
		
		if(e.isBlockInHand()){
			if(e.getClickedBlock().getType() == Material.NOTE_BLOCK) {
				e.setCancelled(true);
				placeBlockAt(e, e.getMaterial());
			}
			return;
		}
		

		if(e.getClickedBlock().getType() == Material.NOTE_BLOCK) e.setCancelled(true);
		
		if(customItem == null) return;
		if(!(customItem instanceof PlaceableItem)) return;
		
		placeBlock(e, (PlaceableItem)customItem);
	}
	
	public void placeBlock(PlayerInteractEvent e, PlaceableItem item) {
		if(e.getClickedBlock() == null) return;

		e.setCancelled(true);
		Location loc = placeBlockAt(e, Material.NOTE_BLOCK, item.blockType().blockSound);
		
		NoteBlock nb = (NoteBlock) loc.getBlock().getBlockData();
		nb.setInstrument(item.blockType().instrument);
		
		nb.setNote(new Note(item.blockType().note));
		loc.getBlock().setBlockData(nb);
	}

	private Location placeBlockAt(PlayerInteractEvent e, Material mat, Sound sound) {
		e.getClickedBlock().getLocation().getWorld().playSound(e.getClickedBlock().getLocation(), sound, 10, 10);
		return placeBlockAt(e, mat);
	}
	private Location placeBlockAt(PlayerInteractEvent e, Material mat) {
		
		BlockFace bf = e.getBlockFace();
		bf.getDirection();
		Location loc = e.getClickedBlock().getLocation().add(bf.getDirection());

		
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE)
			e.getPlayer().getInventory().setItemInMainHand(new ItemBuilder(e.getPlayer().getInventory().getItemInMainHand()).amount(e.getPlayer().getInventory().getItemInMainHand().getAmount()-1).create());
		e.getPlayer().swingMainHand();
	    loc.getBlock().setType(mat);
	    if(loc.getBlock() instanceof Directional)
	    	((Directional)loc.getBlock()).setFacing(bf);
	    
		return loc;
	}
	
	@EventHandler
	public void notePlayEvent(NotePlayEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void blockPlaceEvent(BlockPlaceEvent e) {
		if(e.isCancelled()) return;
		if(e.getBlockPlaced().getType() == Material.NOTE_BLOCK)
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockBreakEvent(BlockBreakEvent e) {
		if(e.getBlock().getType() != Material.NOTE_BLOCK) return;
		e.setCancelled(true);

		NoteBlock nb = (NoteBlock) e.getBlock().getBlockData();
		CustomBlockType blockType = CustomBlockType.getType(nb.getInstrument(), Byte.toUnsignedInt(nb.getNote().getId()));
		if(blockType == null) {e.setCancelled(false); return;}
		
		e.getBlock().setType(Material.AIR);
		
		if(timerMap.containsKey(e.getPlayer().getUniqueId()))
			timerMap.remove(e.getPlayer().getUniqueId());
		
		if(blockType.consumer != null && e.getPlayer().getGameMode() != GameMode.CREATIVE && blockType.consumer.exec(e.getPlayer()) != null)	
			e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), blockType.consumer.exec(e.getPlayer()));
	}
	
	private CustomBlockType blockType;
	private NoteBlock nb;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void physics(BlockPhysicsEvent e) {
		if(e.getBlock().getType() == Material.NOTE_BLOCK) {
			if(!e.getSourceBlock().getRelative(BlockFace.UP).equals(e.getBlock())) return;
			
			nb = (NoteBlock) e.getBlock().getBlockData();			
			if(CustomBlockType.getType(nb.getInstrument(), Byte.toUnsignedInt(nb.getNote().getId())) != null)
				blockType = CustomBlockType.getType(nb.getInstrument(), Byte.toUnsignedInt(nb.getNote().getId()));
			
			Bukkit.getScheduler().runTaskLater(ClusterCore.getInstance(), new Runnable() {
		        @Override
		        public void run() {	
					nb.setInstrument(blockType.instrument);
					nb.setNote(new Note(blockType.note));
					e.getBlock().setBlockData(nb);
		        }
			}, 1);
		}
	}
}















