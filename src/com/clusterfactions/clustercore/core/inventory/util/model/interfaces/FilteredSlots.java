package com.clusterfactions.clustercore.core.inventory.util.model.interfaces;

import java.util.List;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;

public interface FilteredSlots {
	
	public List<Integer> filteredSlots();
	
	public boolean satisfiesFilter(int slot, ItemStack item);
	
	public default List<Integer> getSlotBelonging(ItemStack item) {
		return null;
	}
	
	public default boolean filterSatisfiesEvent(InventoryDragEvent e) {
		if(e.getInventorySlots().size() > 1) return false;
		for(Integer i : e.getInventorySlots()){
			if(!filteredSlots().contains(i)) return false;
		}
		return true;
	}
	
	public default boolean filterSatisfiesSlot(int slot) {
		return filteredSlots().contains(slot);
	}
	
	public default void clickFromBottomInventoryFiltered(InventoryClickEvent e) {
		if(e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT) return;
		
		FilteredSlots filter = (FilteredSlots) ClusterCore.getInstance().getInventoryManager().getHandler(e.getInventory());
		InventoryBase base = ClusterCore.getInstance().getInventoryManager().getHandler(e.getInventory());

		e.setCancelled(true);
		if(filter.getSlotBelonging(e.getCurrentItem()) == null) return;
		if(e.getCurrentItem() == null) return;
		e.setCancelled(true);
		ItemStack item = e.getCurrentItem();

		item.setAmount(base.addItemInto(item, getSlotBelonging(item).toArray(new Integer[getSlotBelonging(item).size()]) ) );
		
	}
}






























