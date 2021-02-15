package com.clusterfactions.clustercore.core.factions.map;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.model.Pair;

public class FactionMapGeneratorManager implements Listener {

	HashMap<UUID, Pair<ItemStack, Integer>> itemCache = new HashMap<>();
	
	public FactionMapGeneratorManager() {
		ClusterCore.getInstance().registerListener(this);
	}
	
	public void openMapView(Player player) {
		MapView mapView;
		Location loc = player.getLocation();
		
		mapView = Bukkit.getServer().createMap(loc.getWorld());
		mapView.setScale(Scale.FARTHEST);
		mapView.setUnlimitedTracking(true);
		mapView.getRenderers().clear();
		mapView.addRenderer(new Renderer());
		mapView.setCenterX((int)player.getLocation().getX());
		mapView.setCenterZ((int)player.getLocation().getZ());
		mapView.setTrackingPosition(true);
		itemCache.put(player.getUniqueId(), new Pair<ItemStack,Integer>(player.getInventory().getItem(0), Integer.valueOf(player.getInventory().getHeldItemSlot())));

		player.getInventory().setHeldItemSlot(0);
		player.getInventory().setItem(0, new ItemBuilder(Material.FILLED_MAP).create());
		player.sendMap(mapView);


	}
	
	@EventHandler
	public void PlayerItemHeldEvent(PlayerItemHeldEvent e) {
		
		if(itemCache.containsKey(e.getPlayer().getUniqueId()) && e.getPreviousSlot() == 0 && e.getNewSlot() != 0)
		{
			e.getPlayer().getInventory().setItem(0,itemCache.get(e.getPlayer().getUniqueId()).getLeft());
			e.getPlayer().getInventory().setHeldItemSlot(itemCache.get(e.getPlayer().getUniqueId()).getRight());
			itemCache.remove(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void PlayerDropItemEvent(PlayerDropItemEvent e) {
		if(itemCache.containsKey(e.getPlayer().getUniqueId()))
		{
			e.getPlayer().getInventory().setItem(0,itemCache.get(e.getPlayer().getUniqueId()).getLeft());
			e.getPlayer().getInventory().setHeldItemSlot(itemCache.get(e.getPlayer().getUniqueId()).getRight());
			itemCache.remove(e.getPlayer().getUniqueId());
			e.getItemDrop().remove();
		}
	}
	
	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent e) {
		if(itemCache.containsKey(e.getPlayer().getUniqueId()))
		{
			e.getPlayer().getInventory().setItem(0,itemCache.get(e.getPlayer().getUniqueId()).getLeft());
			e.getPlayer().getInventory().setHeldItemSlot(itemCache.get(e.getPlayer().getUniqueId()).getRight());
			itemCache.remove(e.getPlayer().getUniqueId());
			e.setCancelled(true);
		}
	}
}


















