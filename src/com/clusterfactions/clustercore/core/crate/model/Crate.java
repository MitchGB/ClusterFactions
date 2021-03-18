package com.clusterfactions.clustercore.core.crate.model;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.core.listeners.events.player.PlayerBlockInteractEvent;
import com.clusterfactions.clustercore.util.ActionHandler;
import com.clusterfactions.clustercore.util.ProbabilityCollection;

import lombok.Getter;

public abstract class Crate {
	
	@Getter private String name;
	@Getter private String[] displayName;
	
	public Crate(String name, String... displayName) {
		this.name = name;
		this.displayName = displayName;
	}
	
	public abstract HashMap<Prize, Integer> drawSet();
	public abstract ActionHandler<PlayerBlockInteractEvent> interactHandler();
	
	public Prize draw(Player player) {
		ProbabilityCollection<Prize> prob = new ProbabilityCollection<>();	
		for(Entry<Prize, Integer> set : drawSet().entrySet()) {
			prob.add(set.getKey(), set.getValue());
		}
		Prize p = prob.get();
		p.output(player);
		return p;
	}
}
