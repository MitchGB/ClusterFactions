package com.clusterfactions.clustercore.core.factions.map;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.location.Vector2Integer;
import com.clusterfactions.clustercore.util.model.Pair;

public class FactionMapGeneratorManager implements Listener {

	HashMap<UUID, Pair<ItemStack, Integer>> itemCache = new HashMap<>();
	HashMap<UUID, Pair<Integer,Integer>> playerCache = new HashMap<>();
	UUID tempCache = null;
	
	public FactionMapGeneratorManager() {
		ClusterCore.getInstance().registerListener(this);
	}
	
	public void openMapView(Player player) {
		player.getInventory().setHeldItemSlot(0);
		ItemStack mapItem = new ItemBuilder(Material.FILLED_MAP).create();
		tempCache = player.getUniqueId();
		playerCache.put(player.getUniqueId(), new Pair<Integer,Integer>((int)Math.round(player.getLocation().getX()/128)*128, (int)Math.round(player.getLocation().getZ()/128)*128));
		
		itemCache.put(player.getUniqueId(), new Pair<ItemStack,Integer>(player.getInventory().getItem(0), Integer.valueOf(player.getInventory().getHeldItemSlot())));
		player.getInventory().setItem(0, mapItem);
	}
	
	
	@EventHandler
	public void onMapInitialize(MapInitializeEvent e) {	;
		MapView mapView = e.getMap();
		mapView.setScale(Scale.CLOSEST);
		mapView.setTrackingPosition(true);
		mapView.setUnlimitedTracking(true);
		mapView.addRenderer(new Renderer());
		
		if(tempCache != null) {
			Player player = Bukkit.getPlayer(tempCache);
    		mapView.setCenterX((int)Math.round(player.getLocation().getX()/128)*128);
    		mapView.setCenterZ((int)Math.round(player.getLocation().getZ()/128)*128);
    		tempCache = null;
		}
		
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
	
	public void removeMap(Player player) {
		if(itemCache.containsKey(player.getUniqueId()))
		{
			player.getInventory().setItem(0,itemCache.get(player.getUniqueId()).getLeft());
			player.getInventory().setHeldItemSlot(itemCache.get(player.getUniqueId()).getRight());
			itemCache.remove(player.getUniqueId());

			if(playerCache.containsKey(player.getUniqueId()))
				playerCache.remove(player.getUniqueId());	
		}
	}
	
	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent e) {
		if(playerCache.containsKey(e.getPlayer().getUniqueId()))
		{
			int x = playerCache.get(e.getPlayer().getUniqueId()).getLeft();
			int xPos = (int)Math.round(e.getPlayer().getLocation().getX()/128)*128;
			if(x != xPos)
			{
				openMapView(e.getPlayer());
				return;
			}
			int z = playerCache.get(e.getPlayer().getUniqueId()).getRight();
			int zPos = (int)Math.round(e.getPlayer().getLocation().getZ()/128)*128;			
			if(z != zPos)
			{
				openMapView(e.getPlayer());
				return;
			}
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
			removeMap(e.getPlayer());
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
			

			if(playerCache.containsKey(e.getPlayer().getUniqueId()))
				removeMap(e.getPlayer());
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
			

			if(playerCache.containsKey(e.getPlayer().getUniqueId()))
				removeMap(e.getPlayer());
		}
	}
}


















