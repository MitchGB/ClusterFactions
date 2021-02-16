package com.clusterfactions.clustercore.persistence.serialization;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

//Base64 Encoder
public class LocationSerializer extends VariableSerializer<Location>{
    private String getStringLocation(final Location l) {
    if (l == null) {
        return "";
    }
    return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }
 
	
    private Location getLocationString(final String s) {
    if (s == null || s.trim() == "") {
        return null;
    }
    final String[] parts = s.split(":");
    if (parts.length == 4) {
        final World w = Bukkit.getServer().getWorld(parts[0]);
        final int x = Integer.parseInt(parts[1]);
        final int y = Integer.parseInt(parts[2]);
        final int z = Integer.parseInt(parts[3]);
        return new Location(w, x, y, z);
    }
    return null;
    }
	
	@Override
	public String serialize(Location obj) {
		if(!(obj instanceof Location)) return null;
		return getStringLocation((Location)obj);
	}

	@Override
	public Location deserialize(String str) {
		return getLocationString(str);
	}
    

}