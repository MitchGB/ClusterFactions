package com.clusterfactions.clustercore.core.inventory.util.model.interfaces;

import java.util.List;
import java.util.Set;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;

public interface InteractableSlots {
	List<Integer> interactableSlots();
	
	public default boolean isInteractable(Set<Integer> slots) {
		for(Integer i : slots) {
			if(!interactableSlots().contains(i))
				return false;
		}
		return true;
	}
	
	public default boolean isInteractable(int slot) {
		return interactableSlots().contains(slot);
	}
	
	public default void clickFromBottomInventoryInteractable(InventoryClickEvent e) {
		if(e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT) return;
		if(e.getCurrentItem() == null) return;
		InventoryBase base = ClusterCore.getInstance().getInventoryManager().getHandler(e.getInventory());
		if(base instanceof FilteredSlots && e.isCancelled()) return;
		e.setCancelled(true);
		
		ItemStack item = e.getCurrentItem();
		if(!base.canFitItem(item, interactableSlots().toArray(new Integer[interactableSlots().size()]))) return;
		
		item.setAmount(base.addItemInto(item, interactableSlots().toArray(new Integer[interactableSlots().size()])));
	}
}
