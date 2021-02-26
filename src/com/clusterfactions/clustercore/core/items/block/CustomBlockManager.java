package com.clusterfactions.clustercore.core.items.block;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.ItemManager;
import com.clusterfactions.clustercore.core.items.block.breakhandler.BlockBreakHandler;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.PlaceableItem;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.google.common.base.Preconditions;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation;

public class CustomBlockManager implements Listener{
	
	private HashMap<UUID, Long> timerMap = new HashMap<>();
	
	public CustomBlockRegistry itemRegistry = new CustomBlockRegistry();
	public BlockBreakHandler itemBreakHandler = new BlockBreakHandler();
	
	public CustomBlockManager() {
		ClusterCore.getInstance().registerListener(this);
	}
	
	public void placeBlock(PlayerInteractEvent e, ItemStack item) {
		if(e.isCancelled()) return;
		ItemManager itemManager = ClusterCore.getInstance().getItemManager();
		CustomItem customItem = itemManager.getCustomItemHandler(itemManager.getCustomItemType(item));
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(e.isBlockInHand())
		{
			if(e.getClickedBlock().getType() == Material.NOTE_BLOCK) 
				{
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
		
		Sound blockSound = blockType.getBlockSound();
		e.getBlock().getLocation().getWorld().playSound(e.getBlock().getLocation(), blockSound, 10, 10);
		e.getBlock().setType(Material.AIR);
		
		if(timerMap.containsKey(e.getPlayer().getUniqueId()))
			timerMap.remove(e.getPlayer().getUniqueId());
		
		if(blockType.consumer != null && e.getPlayer().getGameMode() != GameMode.CREATIVE)	
			e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), blockType.consumer.exec(e.getPlayer()));
	}
	
	public void timerHandler(Player player, Block block) {		
		if(block.getType() != Material.NOTE_BLOCK) return;

		NoteBlock nb = (NoteBlock) block.getBlockData();
		CustomBlockType blockType = CustomBlockType.getType(nb.getInstrument(), Byte.toUnsignedInt(nb.getNote().getId()));
		if(blockType == null) return;
		
		int breakDuration = blockType.breakDuration;
		if(!timerMap.containsKey(player.getUniqueId())) timerMap.put(player.getUniqueId(), System.currentTimeMillis() + breakDuration);
		if(timerMap.get(player.getUniqueId()) < 0) timerMap.put(player.getUniqueId(), System.currentTimeMillis() + breakDuration);
		Bukkit.broadcastMessage(((double)System.currentTimeMillis() - (double)timerMap.get(player.getUniqueId())) / (double)breakDuration * 1D + "");
		sendBlockDamage(player, block.getLocation(), ((float)System.currentTimeMillis() - (float)timerMap.get(player.getUniqueId())) / (float)breakDuration * 1F);
	}

    public static void sendBlockDamage(Player player, Location loc, float progress) {
        Preconditions.checkArgument(player != null, "player must not be null");
        Preconditions.checkArgument(loc != null, "loc must not be null");
        Preconditions.checkArgument(progress >= 0.0 && progress <= 1.0, "progress must be between 0.0 and 1.0 (inclusive)");

        int stage = (int) (9 * progress); // There are 0 - 9 damage states
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(((CraftPlayer) player).getHandle().getId(), new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), stage);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}















