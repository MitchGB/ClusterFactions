package com.clusterfactions.clustercore.core.factions.map;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.util.location.Vector2Integer;
import com.clusterfactions.clustercore.util.model.Pair;

public class FactionMapGeneratorManager implements Listener {

	HashMap<UUID, Pair<ItemStack, Integer>> itemCache = new HashMap<>();
	
	public FactionMapGeneratorManager() {
		ClusterCore.getInstance().registerListener(this);
	}
	
	public void openMapView(Player player) {
		/*
		CraftMapView mapView;
		Location loc = player.getLocation();
		player.getInventory().setHeldItemSlot(0);
		ItemStack mapItem = new ItemBuilder(Material.FILLED_MAP).create();
		net.minecraft.server.v1_16_R3.ItemStack NMSmapItem = CraftItemStack.asNMSCopy(mapItem);
		mapView = ((CraftServer)Bukkit.getServer()).createMap(loc.getWorld());
		Bukkit.getServer().createMap(loc.getWorld()).
		mapView.setScale(Scale.FARTHEST);
		mapView.setUnlimitedTracking(true);
		mapView.setTrackingPosition(true);

		//mapView.setCenterX((int)player.getLocation().getX());
		//mapView.setCenterZ((int)player.getLocation().getZ());
		WorldMap wMap = ItemWorldMap.getSavedMap(NMSmapItem, ((CraftWorld) loc.getWorld()).getHandle());
		
		
		mapView.addRenderer(new Renderer(wMap));
		player.getInventory().setItem(0, mapItem);
		itemCache.put(player.getUniqueId(), new Pair<ItemStack,Integer>(player.getInventory().getItem(0), Integer.valueOf(player.getInventory().getHeldItemSlot())));
		player.sendMap(mapView);
*/

	}
	
	@EventHandler
	public void onMapInitialize(MapInitializeEvent e) {
		MapView mapView = e.getMap();
		
		mapView.setScale(Scale.FARTHEST);
		mapView.setUnlimitedTracking(true);
		mapView.getRenderers().clear();
		mapView.addRenderer(new Renderer());
	}
	
	private void preCacheChunk(Player player)
	{
		FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
		final int radius = 5;		
		Vector2Integer playerChunk = claimManager.getChunkVector(player.getLocation());
		
		Vector2Integer[][] claimMap = new Vector2Integer[radius*2][radius*2];
		
		int pX = playerChunk.getX();
		int pZ = playerChunk.getZ();

		int uX = pX + radius;
		int uZ = pZ + radius ;
		
		int lX = pX - radius;
		int lZ = pZ - radius;
		int xIndex = 0;
		int zIndex = 0;
		
		for(int z = lZ; z < uZ; z++)
		{
			xIndex = 0;
			for(int x = lX; x <uX; x++)
			{
				claimMap[xIndex][zIndex] = new Vector2Integer(x,z);
				xIndex++;
			}
			zIndex++;
		}
	}
	
	@EventHandler
	public void PlayerItemHeldEvent(PlayerItemHeldEvent e) {
		if(e.getPlayer().getInventory().getItem(e.getNewSlot()) != null)
		if(e.getPlayer().getInventory().getItem(e.getNewSlot()).getType() == Material.MAP) {
			preCacheChunk(e.getPlayer());
		}
		
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


















