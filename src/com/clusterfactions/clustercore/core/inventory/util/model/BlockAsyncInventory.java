package com.clusterfactions.clustercore.core.inventory.util.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.persistence.serialization.ItemStackSerializer;
import com.clusterfactions.clustercore.util.Colors;

public abstract class BlockAsyncInventory extends InventoryBase{

	private List<UUID> handlers = new ArrayList<>();
	protected Block block;
	
	public BlockAsyncInventory(Player player, String ID, String displayName, int inventorySize, Block block) {
		super(player, ID, displayName, inventorySize);
		if(!(block.getState() instanceof TileState)) {
			System.err.println("INVALID BLOCK TYPE");
			return;
		}
		ClusterCore.getInstance().getInventoryManager().blockCache.put(block, this);
		TileState tile = (TileState) block.getState();
		
		if(tile.getPersistentDataContainer().get(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING) == null)
			tile.getPersistentDataContainer().set(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING, "");
		
		String content = tile.getPersistentDataContainer().get(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING);

		invInstance = Bukkit.createInventory(player, inventorySize, Colors.parseColors(displayName));
		this.invInstance.setContents(new ItemStackSerializer().deserialize(content));
		
		tile.update();
		this.block = block;
	}
	
	public List<Player> getHandlers() {
		List<Player> l = new ArrayList<>();
		for(UUID uuid : handlers) {
			l.add(Bukkit.getPlayer(uuid));
		}
		return l;
	}
	
	public void removeHandler(Player player) {
		handlers.remove(player.getUniqueId());
	}
	
	@Override
	public void openInventory(Player player) {
		player.closeInventory();
		player.openInventory(this.getInvInstance());
		handlers.add(player.getUniqueId());
	}
	
	@Override
	public Inventory getInventory(Player player) {
		return invInstance;
	}
	
	@Override
	public void playerInventoryClickEvent(InventoryClickEvent e) {
		TileState tile = (TileState) block.getState();
		tile.getPersistentDataContainer().set(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING, new ItemStackSerializer().serialize(invInstance.getContents()));
		tile.update();
	}
	
	@Override
	public void closeInventory(InventoryCloseEvent e) {
		handlers.remove(e.getPlayer().getUniqueId());
		TileState tile = (TileState) block.getState();
		tile.getPersistentDataContainer().set(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING, new ItemStackSerializer().serialize(invInstance.getContents()));
		tile.update();
	}
	
	public abstract void update();
}
















