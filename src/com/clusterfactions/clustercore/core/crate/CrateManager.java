package com.clusterfactions.clustercore.core.crate;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.crate.impl.VoteCrate;
import com.clusterfactions.clustercore.core.crate.model.Crate;
import com.clusterfactions.clustercore.persistence.database.MongoHook;
import com.clusterfactions.clustercore.persistence.serialization.LocationSerializer;
import com.clusterfactions.clustercore.util.Hologram;

public class CrateManager {
	
	private HashMap<String, Crate> crateRegistry = new HashMap<>();
	public HashMap<Location, List<Entity>> standList = new HashMap<>();
	
	public CrateManager() {
		try {
			init();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		MongoHook mongo = ClusterCore.getInstance().getMongoHook();
		
		registerCrates(
				new VoteCrate()
			);
		System.out.println("[ClusterFactions] Registered " + crateRegistry.size() + " crate(s)");
		Bukkit.getScheduler().runTaskLater(ClusterCore.getInstance(), new Runnable() {
	        @Override
	        public void run() {
	    		int registered = 0;
	    		for(String str : mongo.getAllList("_id", "crates")) {
	    			Location loc = new LocationSerializer().deserialize(str);
	    			String type = getCrate(loc);
	    			Crate crate = getCrateHandler(type);
	    			standList.put(loc, Hologram.spawnHologram(loc, true, crate.getDisplayName()) );

	    			registered ++;
	    		}
	    		System.out.println("[ClusterFactions] Found " + registered + " crate(s) in world");
	        }
		}, 100);
	}
	
	public Set<String> crateTypes(){
		return crateRegistry.keySet();
	}
	
	private void registerCrates(Crate... crates) {
		for(Crate crate : crates) {
			crateRegistry.put(crate.getName(), crate);
		}
	}
	
	public boolean isCrate(Location loc) {
		return getCrate(loc) != null;
	}
	
	public boolean isCrate(String name) {
		return crateRegistry.containsKey(name.toUpperCase());
	}
	
	public Crate getCrateHandler(String name) {
		return crateRegistry.get(name.toString());
	}
	
	public String getCrate(Location loc) {
		MongoHook mongo = ClusterCore.getInstance().getMongoHook();
		return mongo.getValue(new LocationSerializer().serialize(loc), "type", String.class, "crates");
	}
	
	public void setCrate(Location loc, String type) {
		MongoHook mongo = ClusterCore.getInstance().getMongoHook();
		mongo.saveValue(new LocationSerializer().serialize(loc), "type", type, "crates");
		standList.put(loc, Hologram.spawnHologram(loc, true, getCrateHandler(type).getDisplayName()) );
	}
	
	public void removeCrate(Location loc) {
		MongoHook mongo = ClusterCore.getInstance().getMongoHook();
		mongo.deleteData(new LocationSerializer().serialize(loc), "crates");
		
		for(Entity stand : standList.get(loc)) {
			stand.remove();
		}
	}
}




















