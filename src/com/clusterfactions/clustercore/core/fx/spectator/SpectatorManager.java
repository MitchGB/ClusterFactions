package com.clusterfactions.clustercore.core.fx.spectator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.fx.spectator.util.EntityHider;
import com.clusterfactions.clustercore.core.fx.spectator.util.EntityHider.Policy;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.model.Pair;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;

public class SpectatorManager implements Listener{
	Map<UUID, Entity> entityMap = new HashMap<>();
	Map<UUID, Pair<Location, ItemStack>> lastLoc = new HashMap<>();
	EntityHider entityHider;
	
	public SpectatorManager() {
		ClusterCore.getInstance().registerListener(this);
		entityHider = new EntityHider(ClusterCore.getInstance(), Policy.BLACKLIST);
	}
	
	public Entity getStandEntity(Location loc) {
		Entity armorStand;
		armorStand = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		
		ArmorStand stand = (ArmorStand)armorStand;
		armorStand.setInvulnerable(true);
		stand.setCustomNameVisible(false);
		stand.setVisible(false);
		stand.setGravity(false);
		stand.setArms(false);
		return armorStand;
	}
	
	public Entity getStand(Player player, Location loc) {
		if(entityMap.containsKey(player.getUniqueId())) {
			return entityMap.get(player.getUniqueId());
		}else {
			Entity entity = getStandEntity(loc);
			entityMap.put(player.getUniqueId(), entity);
			return entity;
		}
	}

	public Entity viewLoc(Player player, Location loc) {
		if(entityMap.containsKey(player.getUniqueId())) return null;
		lastLoc.put(player.getUniqueId(), Pair.of(player.getLocation(),player.getInventory().getHelmet()));
		player.setGameMode(GameMode.SPECTATOR);
		Entity stand = getStand(player, loc);
		player.setSpectatorTarget(stand);
		player.getInventory().setHelmet(new ItemBuilder(Material.CARVED_PUMPKIN).coloredName("There was no better way lol").create());
		
		
		return stand;
	}
	
	public void stopSpectatorMode(Player player) {
		player.setGameMode(GameMode.SPECTATOR);
		player.setSpectatorTarget(null);
		player.setGameMode(GameMode.SURVIVAL);

		player.sendTitle(" ", " ", 0, 1, 0);
		player.teleport(lastLoc.get(player.getUniqueId()).getLeft());
		player.getInventory().setHelmet(lastLoc.get(player.getUniqueId()).getRight());
		lastLoc.remove(player.getUniqueId());
		entityMap.get(player.getUniqueId()).remove();
		entityMap.remove(player.getUniqueId());
	}
	
	@EventHandler
	public void playerItemHeldEvent(PlayerItemHeldEvent e) {
		if(e.getPlayer().getGameMode() == GameMode.SPECTATOR)
			e.getPlayer().getInventory().setHeldItemSlot(8);
	}
	
	@EventHandler
	public void playerStopSpectatingEvent(PlayerStopSpectatingEntityEvent e) {
		if(e.getPlayer().getGameMode() != GameMode.SPECTATOR) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void playerQuitEvent(PlayerQuitEvent e) {
		if(entityMap.containsKey(e.getPlayer().getUniqueId())){
			stopSpectatorMode(e.getPlayer());
		}
	}

}



























