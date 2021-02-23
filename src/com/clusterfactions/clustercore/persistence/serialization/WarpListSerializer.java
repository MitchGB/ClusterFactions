package com.clusterfactions.clustercore.persistence.serialization;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

//Base64 Encoder
public class WarpListSerializer extends VariableSerializer<HashMap<String, Location>>{

	@Override
	public String serialize(HashMap<String, Location> obj) {
		StringBuilder builder = new StringBuilder();
		if(obj == null) return "";
		for(Entry<String,Location> entrySet : obj.entrySet())
		{
			builder.append(entrySet.getKey());
			builder.append(":");
			Location loc = entrySet.getValue();
			builder.append(loc.getWorld().getName() + "/" + loc.getBlockX() + "/" +loc.getBlockY() + "/" + loc.getBlockZ());
			builder.append(",");
		}
		return builder.toString();
	}

	@Override
	public HashMap<String, Location> deserialize(String str) {		
		HashMap<String, Location> ret = new HashMap<>();
		if(str == null || str.isEmpty()) return ret;
		for(String a : str.split(","))
		{
			String[] m = a.split(":");
			Location loc = null;
			String name = m[0];
		    String f[] = m[1].split("/");
			if (f.length == 4) {
		        final World w = Bukkit.getServer().getWorld(f[0]);
		        final int x = Integer.parseInt(f[1]);
		        final int y = Integer.parseInt(f[2]);
		        final int z = Integer.parseInt(f[3]);
		        loc = new Location(w, x, y, z);
		    }
		    
		    ret.put(name,loc);
		}
		return ret;
	}
}

