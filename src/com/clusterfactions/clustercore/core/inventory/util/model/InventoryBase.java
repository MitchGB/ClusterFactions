package com.clusterfactions.clustercore.core.inventory.util.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
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
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.Containers;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenWindow;

public abstract class InventoryBase{

	public static String INVENTORY_TAG = "INVENTORY_KEY";
	public static String ITEM_ID_TAG = "ITM_UUID";
	public static String RANDOM_UUID = "RANDOM_UUID"; //Listeners will stack if not assigned
	
	protected HashMap<String, Pair<InventoryClickHandler, InventoryClickHandler>> clickHandlers = new HashMap<>();
	
	protected InventoryClickHandler defaultClickHandler;
	
	@Getter UUID uuid;
	
	String inventoryID;
	String displayName;
	int inventorySize;
	@Setter protected Player player;
	@Setter Map<Integer, ItemStack> itemList = new HashMap<>();
	@Setter List<ItemStack> itemAddList = new ArrayList<>();
	@Getter protected Inventory invInstance;
	
	public InventoryBase(Player player, String ID, String displayName, int inventorySize) {
		this.uuid = UUID.randomUUID();
		this.inventoryID = ID;
		this.displayName = Colors.parseColors(displayName);
		this.inventorySize = inventorySize;
		this.player = player;
		ClusterCore.getInstance().getInventoryManager().registerInstance(this, this.uuid.toString());
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

			if(e.isRightClick() && this.clickHandlers.get(id).getRight() != null)
				this.clickHandlers.get(id).getRight().exec(context);
			if(e.isLeftClick() && this.clickHandlers.get(id).getLeft() != null)
				this.clickHandlers.get(id).getLeft().exec(context);		
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
	
	protected void renameWindow(Player p, Inventory inv, String title, Containers<?> cont) {

		try {
			EntityPlayer ep = (EntityPlayer) ((CraftPlayer) p).getHandle();
			
			PacketPlayOutOpenWindow windowPacket = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, cont, new ChatMessage(title));
		    ep.playerConnection.sendPacket(windowPacket);
			
			PacketContainer windowItemPacket = new PacketContainer(PacketType.Play.Server.WINDOW_ITEMS);
			windowItemPacket.getIntegers().write(0, ep.activeContainer.windowId);
			List<ItemStack> items = new ArrayList<>();
			for(ItemStack i : inv.getContents())
				items.add(i == null ? new ItemStack(Material.AIR) : i);
			
			StructureModifier<List<ItemStack>> structMod = windowItemPacket.getItemListModifier();
			structMod.write(0, items);
			ClusterCore.getInstance().getProtocolManager().sendServerPacket(p, windowItemPacket);
			
			ItemStack cursor = p.getItemOnCursor();
			PacketContainer setSlotPacket = new PacketContainer(PacketType.Play.Server.SET_SLOT);
			setSlotPacket.getIntegers().write(0, -1);
			setSlotPacket.getIntegers().write(1, -1);
			StructureModifier<ItemStack> structMod2 = setSlotPacket.getItemModifier();
			structMod2.write(0, cursor);
			ClusterCore.getInstance().getProtocolManager().sendServerPacket(p, setSlotPacket);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeInventory(InventoryCloseEvent e) {}

}
