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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.InventoryClickContext;
import com.clusterfactions.clustercore.core.inventory.util.InventoryClickHandler;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.FilteredSlots;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.InteractableSlots;
import com.clusterfactions.clustercore.core.listeners.events.updates.UpdateTickEvent;
import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.model.Pair;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.Containers;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenWindow;

@SuppressWarnings("deprecation")
public abstract class InventoryBase {
	public final static String INVENTORY_TAG = "INVENTORY_KEY";
	public final static String INVENTORY_UUID = "INVENTORY_UUID";
	public final static String ITEM_ID_TAG = "ITEM_UUID";
	
	@Getter protected HashMap<String, Pair<InventoryClickHandler, InventoryClickHandler>> clickHandlers = new HashMap<>();
	
	@Getter protected String inventoryUUID;
	@Getter protected String inventoryTag;
	
	@Getter protected String displayName;
	@Getter protected int inventorySize;
	@Getter protected Map<Integer, ItemStack> itemList = new HashMap<>();
	@Getter protected List<ItemStack> itemAddList = new ArrayList<>();
	@Getter protected List<UUID> handlers = new ArrayList<>();
	@Getter protected Inventory invInstance;
	@Getter protected InventoryClickHandler defaultClickHandler;
	
	public InventoryBase(Player player, String tag, String displayName, int inventorySize) {
		this.inventoryTag = tag;
		this.displayName = Colors.parseColors(displayName);
		this.inventorySize = inventorySize;
		this.inventoryUUID = UUID.randomUUID().toString();
		this.handlers.add(player.getUniqueId());

		this.invInstance = Bukkit.createInventory(null, this.inventorySize, this.displayName);
		
		ClusterCore.getInstance().getInventoryManager().registerInstance(this, this.inventoryUUID);
	}
	
	public void inventoryClickEvent(InventoryClickEvent e) {}
	public void inventoryDragEvent(InventoryDragEvent e) {}
	public void updateTickEvent(UpdateTickEvent e) {}
	
	public void inventoryItemClickHandler(InventoryClickEvent e) {
		ItemStack cursorItem = e.getCursor();
		ItemStack itemStack = e.getCurrentItem();
		
		if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
			if(ClusterCore.getInstance().getInventoryManager().getHandler(e.getClickedInventory()) instanceof InteractableSlots && ((InteractableSlots)ClusterCore.getInstance().getInventoryManager().getHandler(e.getClickedInventory())).isInteractable(e.getSlot())) return;
			e.setCancelled(true);
		}
		
		if(ClusterCore.getInstance().getInventoryManager().getHandler(e.getClickedInventory()) instanceof FilteredSlots && ((FilteredSlots)ClusterCore.getInstance().getInventoryManager().getHandler(e.getClickedInventory())).filterSatisfiesSlot(e.getSlot())) e.setCancelled( ((FilteredSlots)this).satisfiesFilter(e.getSlot(), cursorItem));

		if(ClusterCore.getInstance().getInventoryManager().getHandler(e.getClickedInventory()) instanceof InteractableSlots && ((InteractableSlots)ClusterCore.getInstance().getInventoryManager().getHandler(e.getClickedInventory())).isInteractable(e.getSlot())) return;
		if(e.getClickedInventory() == null || e.getClickedInventory().getType() == InventoryType.PLAYER) return;

		e.setCancelled(true);
		e.setResult(Result.DENY);
		if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

		NBTItem item = new NBTItem(itemStack);
		String uuid = item.getString(InventoryBase.INVENTORY_UUID);
		String id = item.getString(ITEM_ID_TAG);
		if(uuid.isEmpty()) return;
		if(!isApplicableItem(e.getCurrentItem())) return;

		
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
	
	public void clickFromBottomInventory(InventoryClickEvent e) {	
		if(this instanceof FilteredSlots) 
			((FilteredSlots)this).clickFromBottomInventoryFiltered(e);	
		if(this instanceof InteractableSlots) 
			((InteractableSlots)this).clickFromBottomInventoryInteractable(e);
	}
	
	public void playerDropItemEvent(PlayerDropItemEvent e){
		if(!isApplicableItem(e.getItemDrop().getItemStack())) return;
		
		e.setCancelled(true);
		e.getPlayer().updateInventory();
	}
	
	public void closeInventoryEvent(InventoryCloseEvent e) {
		handlers.remove(e.getPlayer().getUniqueId());
		if(handlers.size() == 0)
			ClusterCore.getInstance().getInventoryManager().inventoryCache.remove(this.getInventoryUUID());
	}
	
	public void openInventory(Player player) {		
		itemAddList.forEach(item -> invInstance.addItem(item));
		for(Entry<Integer, ItemStack> e : itemList.entrySet())
			invInstance.setItem(e.getKey(), e.getValue());
		player.closeInventory();
		player.openInventory(invInstance);
		if(!handlers.contains(player.getUniqueId()))
		handlers.add(player.getUniqueId());
	}
	
	public void updateAllHandlers() {
		for(Player player : getPlayerHandlers())
			player.updateInventory();
	}
	
	public void removeHandler(Player player) {
		handlers.remove(player.getUniqueId());
	}
	
	public List<Player> getPlayerHandlers() {
		List<Player> playerList = new ArrayList<>();
		for(UUID uuid : handlers) {
			playerList.add(Bukkit.getPlayer(uuid));
		}
		return playerList;
	}
	
	protected void registerItemHandler(String uuid, InventoryClickHandler handlerL, InventoryClickHandler handlerR){
		clickHandlers.put(uuid, new Pair<InventoryClickHandler, InventoryClickHandler>(handlerL, handlerR));
	}
	
	public void addItem(ItemStack item) {
		addItem(item, null, null);
	}
	
	public void addItem(ItemStack item, InventoryClickHandler handlerL){
		addItem(item, handlerL, null);
	}
	
	public void addItem(ItemStack item, InventoryClickHandler handlerL, InventoryClickHandler handlerR)	{
		String randomUUID = UUID.randomUUID().toString();
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setString(ITEM_ID_TAG, randomUUID);
		nbtItem.setString(INVENTORY_TAG, this.inventoryTag);
		nbtItem.setString(INVENTORY_UUID, this.inventoryUUID);
		itemAddList.add(nbtItem.getItem());
		registerItemHandler(randomUUID, handlerL, handlerR);
	}
	
	public void setItem(ItemStack item, int... index) {
		setItem(item, null, null, index);
	}
	
	public void setItem(ItemStack item, InventoryClickHandler handlerL, int... index){
		setItem(item, handlerL, null, index);
	}
	
	public void setItem(ItemStack item, InventoryClickHandler handlerL, InventoryClickHandler handlerR, int... index) {
		String randomUUID = UUID.randomUUID().toString();
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setString(ITEM_ID_TAG, randomUUID);
		nbtItem.setString(INVENTORY_TAG, this.inventoryTag);
		nbtItem.setString(INVENTORY_UUID, this.inventoryUUID);
		for(int i = 0; i < index.length; i++)
			itemList.put(index[i], nbtItem.getItem());
		registerItemHandler(randomUUID, handlerL, handlerR);
	}
	
	public boolean isApplicableItem(ItemStack item) {
		NBTItem nbtItem = new NBTItem(item);
		if(!nbtItem.hasKey(INVENTORY_TAG)) return false;
		if(!nbtItem.hasKey(INVENTORY_UUID)) return false;
		if(!nbtItem.getString(INVENTORY_TAG).equals(this.getInventoryTag())) return false;
		if(!nbtItem.getString(INVENTORY_UUID).equals(this.getInventoryUUID())) return false;
		if(!ClusterCore.getInstance().getInventoryManager().inventoryCache.containsKey(this.getInventoryUUID())) return false;
		return true;
	}
	
	public void renameWindow(Inventory inv, String title, Containers<?> cont) {
		for(UUID uuid : handlers) {
			Player p = Bukkit.getPlayer(uuid);
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
	}
	
	protected ItemStack setKey(ItemStack item, String key, String value) {
		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setString(key, value);
		return nbtItem.getItem();
	}
	
	protected ItemStack setApplicable(ItemStack item) {
		item = setKey(item, INVENTORY_TAG, this.inventoryTag);
		item = setKey(item, INVENTORY_UUID, this.inventoryUUID);
		return item;
	}

	public Inventory getInventory(Player player) {
		return this.invInstance;
	}
	
	public boolean canFitItem(ItemStack item, Integer... slots) {
		return getNextSlot(item, slots) == null ? false : true;
	}
	
	public List<Integer> getNextSlot(ItemStack item, Integer... slots) {
		List<Integer> openSlots = new ArrayList<>();
		for(int i : slots) {
			ItemStack is = invInstance.getItem(i);
			if(is == null) openSlots.add(i);
			if(item != null && is != null)
				if(is.isSimilar(item) && is.getAmount() < is.getMaxStackSize())
					openSlots.add(i);
		}
		return openSlots.size() == 0 ? null : openSlots;
	}
	
	public int addItemInto(ItemStack item, Integer... slots) {
		List<Integer> openSlots = getNextSlot(item, slots);
		int remaining = item.getAmount();
		if(!canFitItem(item, slots)) return remaining;
		for(Integer slot : openSlots) {
			if(remaining == 0) return 0;
			if(invInstance.getItem(slot) == null || invInstance.getItem(slot).getType() == Material.AIR) {
				invInstance.setItem(slot, item.clone());
				item.setAmount(0);
				remaining = 0;
				continue;
			}
			if(invInstance.getItem(slot).getAmount() + remaining > item.getMaxStackSize()){
				int amount = item.getMaxStackSize()-invInstance.getItem(slot).getAmount();
				invInstance.getItem(slot).add(amount);
				remaining -= amount;
				item.setAmount(remaining);
				continue;
			}
			if(invInstance.getItem(slot).getAmount() + remaining <= item.getMaxStackSize()){
				invInstance.getItem(slot).add(remaining);
				item.setAmount(0);				
				remaining = 0;
				continue;
			}
		}
		return remaining;
	}
	
	public ItemStack getNextItem(Integer... slots) {
		for(int i : slots) {
			ItemStack is = invInstance.getItem(i);
			if(is == null || is.getType() == Material.AIR) return is;
		}
		return null;
	}
}






























