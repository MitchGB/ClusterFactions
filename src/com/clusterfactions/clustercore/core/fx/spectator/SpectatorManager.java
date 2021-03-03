package com.clusterfactions.clustercore.core.fx.spectator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import com.clusterfactions.clustercore.core.fx.spectator.cinematic.CinematicSequence;
import com.clusterfactions.clustercore.core.fx.spectator.cinematic.util.CinematicFrame;
import com.clusterfactions.clustercore.core.fx.spectator.util.EntityHider;
import com.clusterfactions.clustercore.core.fx.spectator.util.EntityHider.Policy;
import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.unicode.CharRepo;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;

public class SpectatorManager implements Listener{
	Map<UUID, Entity> entityMap = new HashMap<>();
	Map<UUID, Location> lastLoc = new HashMap<>();
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

	public void viewLoc(Player player, Location loc) {
		lastLoc.put(player.getUniqueId(), player.getLocation());
		player.setGameMode(GameMode.SPECTATOR);
		Entity stand = getStand(player, loc);
		player.setSpectatorTarget(stand);
		new CinematicSequence(
				new CinematicFrame(5, e -> {player.playSound(loc, Sound.UI_TOAST_IN, 10, 10);}),
				new CinematicFrame(5, e -> {player.playSound(loc, Sound.UI_TOAST_IN, 10, 10);}),
				new CinematicFrame(5, e -> {player.playSound(loc, Sound.UI_TOAST_IN, 10, 10);}),
				new CinematicFrame(5, e -> {player.playSound(loc, Sound.UI_TOAST_IN, 10, 10);}),
				new CinematicFrame(5, e -> {player.playSound(loc, Sound.UI_TOAST_IN, 10, 10);}),
				new CinematicFrame(5, e -> {player.playSound(loc, Sound.UI_TOAST_IN, 10, 10);}),
				new CinematicFrame(5, e -> {player.playSound(loc, Sound.UI_TOAST_IN, 10, 10);}),
				new CinematicFrame(5, e -> {player.playSound(loc, Sound.UI_TOAST_IN, 10, 10);}),
				new CinematicFrame(5, e -> {player.playSound(loc, Sound.UI_TOAST_IN, 10, 10);}),
				new CinematicFrame(5, e -> {player.playSound(loc, Sound.UI_TOAST_IN, 10, 10);}),
				new CinematicFrame(30, e -> {player.getWorld().createExplosion(loc, 10, false, false); }),
				new CinematicFrame(1, e -> {player.getWorld().dropItem(new Location(player.getWorld(), -136.5, 68.5, -13.5), new ItemStack(Material.NETHERITE_SWORD));}),
				new CinematicFrame(40, e -> {stopSpectatorMode(player);})
				).execute(player);
		player.sendTitle("", Colors.parseColors("&f" + CharRepo.BLACK_BORDER), 0, Integer.MAX_VALUE, 0);
		
	}
	
	public void stopSpectatorMode(Player player) {
		player.setGameMode(GameMode.SPECTATOR);
		player.setSpectatorTarget(null);
		player.setGameMode(GameMode.SURVIVAL);

		player.sendTitle(" ", " ", 0, 1, 0);
		player.teleport(lastLoc.get(player.getUniqueId()));
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



























