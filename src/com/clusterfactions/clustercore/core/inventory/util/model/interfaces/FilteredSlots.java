package com.clusterfactions.clustercore.core.inventory.util.model.interfaces;

import java.util.List;

import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public interface FilteredSlots {
	
	public List<Integer> restrictedSlots();
	
	public boolean isRestricted(int slot, ItemStack item);
	
	public default int getSlotBelonging(ItemStack item) {
		return -1;
	}
	
	public default boolean isApplicableEvent(InventoryDragEvent e) {
		if(e.getInventorySlots().size() > 1) return false;
		for(Integer i : e.getInventorySlots()){
			if(!restrictedSlots().contains(i)) return false;
		}
		return true;
	}
	
	public default boolean isApplicableEvent(int slot) {
		return restrictedSlots().contains(slot);
	}
}
