package com.clusterfactions.clustercore.core.inventory.util;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.model.BlockAsyncInventory;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.FilteredSlots;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.Interactable;
import com.clusterfactions.clustercore.listeners.events.updates.UpdateTickEvent;
import com.clusterfactions.clustercore.persistence.serialization.ItemStackSerializer;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;

public class InventoryManager implements Listener{
	@Getter
	public HashMap<String, InventoryBase> inventoryCache = new HashMap<>();
	public HashMap<Block, InventoryBase> blockCache = new HashMap<>();
	
	public boolean applicableInventory(Inventory inv) {
		for(InventoryBase b : inventoryCache.values())
		{
			if(b.getInvInstance() != null && inv != null)
			if(b.getInvInstance().equals(inv))
				return true;
		}
		return false;
	}
	
	public InventoryBase getHandler(Inventory inv) {
		for(InventoryBase b : inventoryCache.values())
		{
			if(b.getInvInstance() == null) continue;
			if(b.getInvInstance().equals(inv))
				return b;
		}
		return null;
	}
	
	public InventoryManager() {
		ClusterCore.getInstance().registerListener(this);
	}
	
	public void registerInstance(InventoryBase base, String id) {
		inventoryCache.put(id, base);
	}
	
	public void unregisterInstance(String id)
	{
		if(inventoryCache.containsKey(id))
			inventoryCache.remove(id);
	}

	@EventHandler
	public void dragHandler(InventoryDragEvent e) {
		if(e.getInventory().getType() == InventoryType.PLAYER) return;
		if(!applicableInventory(e.getInventory())) return;

		e.setCancelled(true);
		
		if(getHandler(e.getInventory()) instanceof Interactable &&  ((Interactable)getHandler(e.getInventory())).isExcluded(e.getInventorySlots())) 
			e.setCancelled(false);
		
		if(getHandler(e.getInventory()) instanceof FilteredSlots && ((FilteredSlots)getHandler(e.getInventory())).isApplicableEvent(e) )
			e.setCancelled(true);
		
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void clickFromBottomInv(InventoryClickEvent e) {
		if(e.getClickedInventory() == null) return;
		if(e.getClickedInventory().getType() != InventoryType.PLAYER) return;
		if(e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT) return;
		if(!(getHandler(e.getInventory()) instanceof FilteredSlots)) return;
		FilteredSlots filter = (FilteredSlots) getHandler(e.getInventory());
		if(filter.getSlotBelonging(e.getCurrentItem()) == -1) return;
		if(e.getCurrentItem() == null) return;
		e.setCancelled(true);
		Inventory topInventory = e.getInventory();
		ItemStack item = e.getCurrentItem();

		int slot = filter.getSlotBelonging(item);
		if(topInventory.getItem(slot) == null || topInventory.getItem(slot).getType() == Material.AIR) {
			topInventory.setItem(slot, item.clone());
			item.setAmount(0);
			return;
		}
		if(!item.isSimilar(topInventory.getItem(slot)))
			return;
		if(topInventory.getItem(slot).getAmount() == item.getMaxStackSize())
			return;

		if(topInventory.getItem(slot).getAmount() + item.getAmount() > item.getMaxStackSize()){
			int amount = item.getMaxStackSize()-topInventory.getItem(slot).getAmount();
			topInventory.getItem(slot).add(amount);
			item.add(-amount);
			return;
		}
		if(topInventory.getItem(slot).getAmount() + item.getAmount() <= item.getMaxStackSize()){
			topInventory.getItem(slot).add(item.getAmount());
			item.setAmount(0);
			return;
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void clickHandler(InventoryClickEvent e) {
		if(!applicableInventory(e.getInventory())) return;
		if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT)
		{
			if(getHandler(e.getClickedInventory()) instanceof Interactable) {
				Interactable interactable = (Interactable) getHandler(e.getClickedInventory());
				if(interactable.isExcluded(e.getSlot()))
					return;
			}
			e.setCancelled(true);
		}
		
				
		
		if(e.getCursor() != null && e.getCursor().getType() != Material.AIR)
		if(getHandler(e.getClickedInventory()) instanceof FilteredSlots && ((FilteredSlots)getHandler(e.getClickedInventory())).isApplicableEvent(e.getSlot())) e.setCancelled( ((FilteredSlots)getHandler(e.getClickedInventory())).isRestricted(e.getSlot(), e.getCursor()) ); 
		if(getHandler(e.getClickedInventory()) instanceof Interactable && ((Interactable)getHandler(e.getClickedInventory())).isExcluded(e.getSlot())) return;
		
		if(e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER) return;
		e.setCancelled(true);

		if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		NBTItem item = new NBTItem(e.getCurrentItem());
		String uuid = item.getString(InventoryBase.RANDOM_UUID);
		if(uuid.isEmpty()) return;
		if(!inventoryCache.containsKey(uuid)) return;
		inventoryCache.get(uuid).playerInventoryClickEvent(e);
	}
	
	@EventHandler
	public void closeInventoryEvent(InventoryCloseEvent e) {
		if(!applicableInventory(e.getInventory())) return;
		if(getHandler(e.getInventory()) instanceof BlockAsyncInventory){
			((BlockAsyncInventory)getHandler(e.getInventory())).removeHandler((Player)e.getPlayer());
			return;
		}
		getHandler(e.getInventory()).closeInventory(e);
		getHandler(e.getInventory()).setPlayer(null);
		inventoryCache.remove(getHandler(e.getInventory()).getUuid().toString() ); //Let GC pickup inventory
	}

	/*
	 * BLOCKASYNCINVENTORY
	 */
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void blockBreakEvent(BlockBreakEvent e) {
		if(!(e.getBlock().getState() instanceof TileState)) return;
		TileState tile = (TileState) e.getBlock().getState();
		String contents = tile.getPersistentDataContainer().get(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING);
		if(contents == null || contents.isEmpty()) return;
		ItemStack[] items = new ItemStackSerializer().deserialize(contents);
		for(ItemStack item : items) {
			if(item == null) continue;
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
		}
		if(blockCache.containsKey(e.getBlock()))
			blockCache.remove(e.getBlock());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void inventoryMoveItemEvent(InventoryMoveItemEvent e) {
		if(e.getSource().getType() != InventoryType.HOPPER) return;
		if(e.getDestination().getType() != InventoryType.FURNACE) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void tickUpdateEvent(UpdateTickEvent e) {
		//Delegate runnable to event rather than runnable for each inventory
		for(InventoryBase inv : blockCache.values()){
			if(!(inv instanceof BlockAsyncInventory)) return;
			((BlockAsyncInventory)inv).update();
		}
	}
	
	/*
	 * END 
	 */
	
}


















