package com.clusterfactions.clustercore.core.inventory.util.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.InventoryClickContext;
import com.clusterfactions.clustercore.core.inventory.util.InventoryClickHandler;
import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.model.Pair;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;

public abstract class InventoryBase implements Listener{

	public static String INVENTORY_TAG = "INVENTORY_KEY";
	public static String ITEM_ID_TAG = "ITM_UUID";
	public static String RANDOM_UUID = "RANDOM_UUID"; //Listeners will stack if not assigned
	
	protected HashMap<String, Pair<InventoryClickHandler, InventoryClickHandler>> clickHandlers = new HashMap<>();
	
	protected InventoryClickHandler defaultClickHandler;
	
	UUID uuid;
	
	String inventoryID;
	String displayName;
	int inventorySize;
	Player player;
	@Setter Map<Integer, ItemStack> itemList = new HashMap<>();
	@Setter List<ItemStack> itemAddList = new ArrayList<>();
	@Getter Inventory invInstance;
	
	public InventoryBase(Player player, String ID, String displayName, int inventorySize) {
		this.uuid = UUID.randomUUID();
		this.inventoryID = ID;
		this.displayName = Colors.parseColors(displayName);
		this.inventorySize = inventorySize;
		this.player = player;
		ClusterCore.getInstance().getInventoryManager().registerInstance(this, this.uuid.toString());
		ClusterCore.getInstance().registerListener(this);
	}
	
	protected void registerHandler(String uuid, InventoryClickHandler handlerL, InventoryClickHandler handlerR)
	{
		clickHandlers.put(uuid, new Pair<InventoryClickHandler, InventoryClickHandler>(handlerL, handlerR));
	}
	
	public void addItem(ItemStack item) {
		addItem(item, null, null);
	}
	
	public void addItem(ItemStack item, InventoryClickHandler handlerL)
	{
		addItem(item, handlerL, null);
	}
	
	public void addItem(ItemStack item, InventoryClickHandler handlerL, InventoryClickHandler handlerR)	{

		String rid = UUID.randomUUID().toString();
		registerHandler(rid, handlerL, handlerR);
		
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setString(ITEM_ID_TAG, rid);
		nbtItem.setString(INVENTORY_TAG, inventoryID);
		nbtItem.setString(RANDOM_UUID, uuid.toString());
		itemAddList.add(nbtItem.getItem());
	}
	
	public void setItem(ItemStack item, int... index) {
		setItem(item, null, null, index);
	}
	
	public void setItem(ItemStack item, InventoryClickHandler handlerL, int... index)
	{
		setItem(item, handlerL, null, index);
	}
	
	public void setItem(ItemStack item, InventoryClickHandler handlerL, InventoryClickHandler handlerR, int... index) {
		String rid = UUID.randomUUID().toString();
		registerHandler(rid, handlerL, handlerR);
		
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setString(ITEM_ID_TAG, rid);
		nbtItem.setString(INVENTORY_TAG, inventoryID);
		nbtItem.setString(RANDOM_UUID, uuid.toString());
		for(int i = 0; i < index.length; i++)
			itemList.put(index[i], nbtItem.getItem());
	}
	
	public void playerDropItemEvent(PlayerDropItemEvent e)
	{
		if(!isApplicable(e.getItemDrop().getItemStack())) return;
		
		e.setCancelled(true);
		e.getPlayer().updateInventory();
	}
	
	@EventHandler
	public void inventoryCloseEvent(InventoryCloseEvent e) {
		try {
			if(!player.getUniqueId().equals(e.getPlayer().getUniqueId())) return;
			//HytheCraft.getInstance().getInventoryManager().unregisterInstance(this.uuid.toString());
		}catch(Exception z) {}
	}
	
	public void playerInventoryClickEvent(InventoryClickEvent e) {
		if(e.getInventory().getType() == InventoryType.CHEST) e.setResult(Result.DENY);
		if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		if(!isApplicable(e.getCurrentItem())) return;
		e.setResult(Result.DENY);
		NBTItem nbtItem = new NBTItem(e.getCurrentItem());
		String id = nbtItem.getString(ITEM_ID_TAG);
		
		InventoryClickContext context = new InventoryClickContext((Player)e.getWhoClicked(), e.getCurrentItem(), e.isRightClick(), e);
		if(!id.isEmpty()) {
			if(!this.clickHandlers.containsKey(id)) return;
			String uid = uuid.toString();

			if(this.clickHandlers.get(uid) == null) return;
			if(e.isRightClick() && this.clickHandlers.get(uid).getRight() != null)
				this.clickHandlers.get(uid).getRight().exec(context);
			if(e.isLeftClick() && this.clickHandlers.get(uid).getLeft() != null)
				this.clickHandlers.get(uid).getLeft().exec(context);		
		}
		else {
			if(this.defaultClickHandler != null)
				this.defaultClickHandler.exec(context);
		}
		
	}
	
	public void openInventory(Player player) {
		player.closeInventory();
		invInstance = getInventory(player);
		player.openInventory(invInstance);
	}
	
	public Inventory getInventory(Player player) {
		Inventory inv;
		inv = Bukkit.createInventory(player, inventorySize, displayName);
			
		itemAddList.forEach(item -> inv.addItem(item));
		
		for(Entry<Integer, ItemStack> e : itemList.entrySet())
			inv.setItem(e.getKey(), e.getValue());
		return inv;
	}
	
	protected boolean isApplicable(ItemStack item) {
		NBTItem nbtItem = new NBTItem(item);
		if(!nbtItem.hasKey(INVENTORY_TAG)) return false;
		if(!nbtItem.hasKey(RANDOM_UUID)) return false;
		if(!nbtItem.getString(INVENTORY_TAG).equals(inventoryID)) return false;
		if(!nbtItem.getString(RANDOM_UUID).equals(uuid.toString())) return false;
		if(!ClusterCore.getInstance().getInventoryManager().inventoryCache.containsKey(this.uuid.toString())) return false;
		return true;
	}
	
	protected ItemStack setKey(ItemStack item, String key, String value) {
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setString(key, value);
		return nbtItem.getItem();
	}
	
	protected ItemStack setApplicable(ItemStack item) {
		item = setKey(item, INVENTORY_TAG, inventoryID);
		item = setKey(item, RANDOM_UUID, uuid.toString());
		return item;
	}

}
