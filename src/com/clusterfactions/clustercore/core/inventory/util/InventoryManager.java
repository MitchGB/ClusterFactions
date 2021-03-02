package com.clusterfactions.clustercore.core.inventory.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.model.BlockInventoryBase;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.FilteredSlots;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.InteractableSlots;
import com.clusterfactions.clustercore.listeners.events.updates.UpdateTickEvent;
import com.clusterfactions.clustercore.persistence.serialization.ItemStackSerializer;

public class InventoryManager implements Listener{
	public HashMap<String, InventoryBase> inventoryCache = new HashMap<>();
	public HashMap<Block, BlockInventoryBase> blockCache = new HashMap<>();
	
	public InventoryManager() {
		ClusterCore.getInstance().registerListener(this);
	}
	
	public boolean applicableInventory(Inventory inv) {
		if(inv == null) return false;
		for(InventoryBase b : inventoryCache.values()){
			if(b.getInvInstance() != null){
				if(b.getInvInstance().equals(inv)) {
					return true;	
				}
			}
		}
		return false;
	}
	
	public InventoryBase getHandler(Inventory inv) {
		for(InventoryBase b : inventoryCache.values()){
			if(b.getInvInstance() == null) continue;
			if(b.getInvInstance().equals(inv))
				return b;
		}
		return null;
	}

	public void registerInstance(InventoryBase inventoryBase, String inventoryUUID) {
		this.inventoryCache.put(inventoryUUID, inventoryBase);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void inventoryMoveItemEvent(InventoryMoveItemEvent e) {
		if(e.getSource().getType() != InventoryType.HOPPER) return;
		if(e.getDestination().getType() != InventoryType.FURNACE) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void inventoryDragEvent(InventoryDragEvent e) {
		if(e.getInventory().getType() == InventoryType.PLAYER) return;
		if(!applicableInventory(e.getInventory())) return;

		e.setCancelled(true);
		
		if(getHandler(e.getInventory()) instanceof InteractableSlots &&  ((InteractableSlots)getHandler(e.getInventory())).isInteractable(e.getInventorySlots())) 
			e.setCancelled(false);
		
		if(getHandler(e.getInventory()) instanceof FilteredSlots && ((FilteredSlots)getHandler(e.getInventory())).filterSatisfiesEvent(e) )
			e.setCancelled(true);
		
	}
	
	/*
	 * Passthrough event
	 */
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void inventoryItemClickHandlerPass(InventoryClickEvent e) {
		Inventory inventory = e.getInventory();		
		if(e.getInventory() == null) return;
		if(!applicableInventory(inventory)) return;
		getHandler(inventory).inventoryItemClickHandler(e);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void clickFromBottomInventoryPass(InventoryClickEvent e) {		
		Inventory inventory = e.getInventory();
		if(e.getClickedInventory() == null) return;
		if(e.getClickedInventory().getType() != InventoryType.PLAYER) return;	
		if(!applicableInventory(inventory)) return;
		getHandler(inventory).clickFromBottomInventory(e);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void blockBreakEventPass(BlockBreakEvent e) {
		if(e.getBlock().getState() instanceof TileState){
			TileState tile = (TileState) e.getBlock().getState();
			String contents = tile.getPersistentDataContainer().get(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING);
			tile.getPersistentDataContainer().set(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING, "");
			tile.update();
			for(ItemStack item : new ItemStackSerializer().deserialize(contents)){
				if(item == null || item.getType() == Material.AIR) continue;
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
			}
		}
		if(!blockCache.containsKey(e.getBlock())) return;
		blockCache.get(e.getBlock()).blockBreakEvent(e);
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void inventoryClickEventPass(InventoryClickEvent e) {
		if(applicableInventory(e.getClickedInventory()))
			getHandler(e.getClickedInventory()).inventoryClickEvent(e);
		if(applicableInventory(e.getInventory()))
			getHandler(e.getInventory()).inventoryClickEvent(e);
	}

	@EventHandler(priority=EventPriority.LOW)
	public void inventoryDragEventPass(InventoryDragEvent e) {
		if(applicableInventory(e.getInventory()))
			getHandler(e.getInventory()).inventoryDragEvent(e);
	}
	
	@EventHandler
	public void inventoryCloseEventPass(InventoryCloseEvent e) {
		Inventory inventory = e.getInventory();		
		if(!applicableInventory(inventory)) return;
		getHandler(inventory).closeInventoryEvent(e);
	}
	
	@EventHandler
	public void updateTickEventPass(UpdateTickEvent e) {
		for(InventoryBase base : inventoryCache.values()) {
			base.updateTickEvent(e);
		}
	}
	
	public static boolean canFitItem(Inventory inv, ItemStack item, Integer... slots) {
		return getNextSlot(inv, item, slots) == null ? false : true;
	}
	
	public static List<Integer> getNextSlot(Inventory inv, ItemStack item, Integer... slots) {
		List<Integer> openSlots = new ArrayList<>();
		for(int i : slots) {
			ItemStack is = inv.getItem(i);
			if(is == null) openSlots.add(i);
			if(item != null && is != null)
				if(is.isSimilar(item) && is.getAmount() < is.getMaxStackSize())
					openSlots.add(i);
		}
		return openSlots.size() == 0 ? null : openSlots;
	}
	
	public static int addItemInto(Inventory inv, ItemStack item, Integer... slots) {
		List<Integer> openSlots = getNextSlot(inv, item, slots);
		if(item == null || item.getType() == Material.AIR) return 0;
		int remaining = item.getAmount();
		if(!canFitItem(inv, item, slots)) return remaining;
		for(Integer slot : openSlots) {
			if(remaining == 0) return 0;
			if(inv.getItem(slot) == null || inv.getItem(slot).getType() == Material.AIR) {
				inv.setItem(slot, item.clone());
				item.setAmount(0);
				remaining = 0;
				continue;
			}
			if(inv.getItem(slot).getAmount() + remaining > item.getMaxStackSize()){
				int amount = item.getMaxStackSize()-inv.getItem(slot).getAmount();
				inv.getItem(slot).add(amount);
				remaining -= amount;
				item.setAmount(remaining);
				continue;
			}
			if(inv.getItem(slot).getAmount() + remaining <= item.getMaxStackSize()){
				inv.getItem(slot).add(remaining);
				item.setAmount(0);				
				remaining = 0;
				continue;
			}
		}
		return remaining;
	}
	

	public static ItemStack getNextItem(Inventory inv, Integer... slots) {
		for(int i : slots) {
			ItemStack is = inv.getItem(i);
			if(is != null && is.getType() != Material.AIR) return is;
		}
		return null;
	}
}



















