package com.clusterfactions.clustercore.core.inventory.util;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.Interactable;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;

public class InventoryManager implements Listener{
	@Getter
	public HashMap<String, InventoryBase> inventoryCache = new HashMap<>();
	
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
		if(getHandler(e.getInventory()).getClass().isAnnotationPresent(Interactable.class)) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void clickHandler(InventoryClickEvent e) {
		if(!applicableInventory(e.getInventory())) return;
		if(e.getInventory() != null && getHandler(e.getInventory()) != null)
		if(getHandler(e.getInventory()).getClass().isAnnotationPresent(Interactable.class)) return;
		
		if(e.getClickedInventory() != null && getHandler(e.getClickedInventory()) != null)
		if(getHandler(e.getClickedInventory()).getClass().getAnnotation(Interactable.class) != null) return;

		if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) e.setCancelled(true);
		if(e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER) return;
		e.setCancelled(true);

		if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		NBTItem item = new NBTItem(e.getCurrentItem());
		String uuid = item.getString(InventoryBase.RANDOM_UUID);
		if(uuid.isEmpty()) return;
		if(!inventoryCache.containsKey(uuid)) return;
		inventoryCache.get(uuid).playerInventoryClickEvent(e);
	}

	
}


















