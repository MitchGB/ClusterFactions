package com.clusterfactions.clustercore.core.inventory.util.model;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.persistence.PersistentDataType;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.persistence.serialization.ItemStackSerializer;

public abstract class BlockAsyncInventoryBase extends BlockInventoryBase{

	
	public BlockAsyncInventoryBase(Player player, String ID, String displayName, int inventorySize, Block block) {
		super(player, ID, displayName, inventorySize, block);
		if(!(block.getState() instanceof TileState)) {
			System.err.println("INVALID BLOCK TYPE");
			return;
		}
		
		TileState tile = (TileState) block.getState();
		String content = tile.getPersistentDataContainer().get(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING);
		this.invInstance.setContents(new ItemStackSerializer().deserialize(content));
	}
	
	@Override
	public void closeInventoryEvent(InventoryCloseEvent e) {
		handlers.remove(e.getPlayer().getUniqueId());
		TileState tile = (TileState) block.getState();
		tile.getPersistentDataContainer().set(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING, new ItemStackSerializer().serialize(invInstance.getContents()));
		tile.update();
	}
	
	public void blockBreakEvent(BlockBreakEvent e) {
		TileState tile = (TileState) block.getState();
		tile.getPersistentDataContainer().set(new NamespacedKey(ClusterCore.getInstance(), "contents"), PersistentDataType.STRING, new ItemStackSerializer().serialize(invInstance.getContents()));
		tile.update();
		ClusterCore.getInstance().getInventoryManager().blockCache.remove(block);
		for(UUID p : handlers){
			Bukkit.getPlayer(p).closeInventory();
		}
	}
}






