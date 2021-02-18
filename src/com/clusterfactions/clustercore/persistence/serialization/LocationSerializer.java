package com.clusterfactions.clustercore.persistence.serialization;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

//Base64 Encoder
public class LocationSerializer extends VariableSerializer<Location>{
	
	@Override
	public String serialize(Location obj) {
	    if (obj == null) {
	        return "";
	    }
	    return obj.getWorld().getName() + ":" + obj.getBlockX() + ":" + obj.getBlockY() + ":" + obj.getBlockZ();
	}

	@Override
	public Location deserialize(String str) {
	    if (str == null || str.trim() == "") {
	        return null;
	    }
	    final String[] parts = str.split(":");
	    if (parts.length == 4) {
	        final World w = Bukkit.getServer().getWorld(parts[0]);
	        final int x = Integer.parseInt(parts[1]);
	        final int y = Integer.parseInt(parts[2]);
	        final int z = Integer.parseInt(parts[3]);
	        return new Location(w, x, y, z);
	    }
	    return null;
	}
    

}