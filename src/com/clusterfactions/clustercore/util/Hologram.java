package com.clusterfactions.clustercore.util;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;

import net.md_5.bungee.api.ChatColor;

public class Hologram {
	
	public static ArrayList<Entity> spawnHologram(Location loc, boolean offset, String... lines) {
		int i = 1;
		ArrayList<Entity> stands = new ArrayList<>();
		for(String layer : lines)
		{
			
			AreaEffectCloud as;
			if(offset)
				as = loc.getWorld().spawn(loc.clone().add(+0.5D, .5+i*.4D, +0.5D), AreaEffectCloud.class);
			else
				as = loc.getWorld().spawn(loc.clone().add(0, .5+i*.4D, 0), AreaEffectCloud.class);
			as.setRadius(0);
			as.setGravity(false);
			as.setCustomName(ChatColor.translateAlternateColorCodes('&', layer));
			as.setCustomNameVisible(true);
			as.setInvulnerable(true);
			as.setDuration(2147483646/2);
			as.setRadiusPerTick(0);
			as.setParticle(Particle.BLOCK_CRACK, Material.AIR.createBlockData());
			as.setDurationOnUse(0);
			
			
			/*
			ArmorStand as;
			if(offset)
				as = loc.getWorld().spawn(loc.clone().add(+0.5D, .5+i*.4D, +0.5D), ArmorStand.class);
			else
				as = loc.getWorld().spawn(loc.clone().add(0, .5+i*.4D, 0), ArmorStand.class);
			as.setGravity(false);
			as.setCustomName(ChatColor.translateAlternateColorCodes('&', layer));
			as.setCustomNameVisible(true);
			as.setInvulnerable(true);
			as.setInvisible(true);
			as.setMarker(true);
			as.setCollidable(false);
			 */
			i++;
			stands.add(as);
		}
		return stands;
		
	}
}
