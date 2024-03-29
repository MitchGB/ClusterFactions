package com.clusterfactions.clustercore.core.fx.spectator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.AreaEffectCloud;
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
	
	public Entity getStandEntity(Location loc) {/*
		Entity e = loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
		Zombie zomb = (Zombie)e;
		zomb.setGravity(false);
		zomb.setSilent(true);
		zomb.setInvisible(true);
		zomb.setCanPickupItems(false);
		zomb.setCanBreakDoors(false);
		zomb.setInvulnerable(true);
		zomb.setCollidable(false);
		zomb.setAware(false);
		
		return e;*/
		Entity e = loc.getWorld().spawnEntity(loc, EntityType.AREA_EFFECT_CLOUD);
		AreaEffectCloud cloud = (AreaEffectCloud)e;
		cloud.setRadius(0);
		cloud.setGravity(false);
		cloud.setCustomNameVisible(false);
		cloud.setCustomNameVisible(true);
		cloud.setInvulnerable(true);
		cloud.setDuration(2147483646/2);
		cloud.setRadiusPerTick(0);
		cloud.setParticle(Particle.BLOCK_CRACK, Material.AIR.createBlockData());
		cloud.setDurationOnUse(0);
		return e;
		
		/*Entity armorStand;
		armorStand = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		
		ArmorStand stand = (ArmorStand)armorStand;
		armorStand.setInvulnerable(true);
		stand.setCustomNameVisible(false);
		stand.setVisible(false);
		stand.setGravity(false);
		stand.setArms(false);
		return armorStand;*/
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
		Entity stand = getStand(player, loc);
		player.setGameMode(GameMode.SPECTATOR);
		player.setSpectatorTarget(stand);
		player.getInventory().setHelmet(new ItemBuilder(Material.CARVED_PUMPKIN).coloredName("There was no better way lol").create());
		
		return stand;
	}
	
	public void stopSpectatorMode(Player player) {
		player.setGameMode(GameMode.SPECTATOR);
		player.setSpectatorTarget(null);
		player.setGameMode(GameMode.SURVIVAL);

		if(!entityMap.containsKey(player.getUniqueId())) return;
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
	
	public static void changeAllBlocks(Player player, BlockData mat, Location... locs) {
		for(Location loc : locs) {
			player.sendBlockChange(loc, mat);
		}
	}

}



























