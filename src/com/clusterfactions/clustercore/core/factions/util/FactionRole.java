package com.clusterfactions.clustercore.core.factions.util;

import lombok.Getter;

public enum FactionRole {
	LEADER(5),
	COLEADER(4),
	MODERATOR(3),
	MEMBER(2),
	RECRUIT(1);
	
	@Getter private int weight;
	
	public static int getHighestWeight() {
		return 5;
	}
	
	public static FactionRole getRoleByWeight(int weight) {
		for(FactionRole role : FactionRole.values())
			if(role.weight == weight)
				return role;
		return null;
	}
	
	FactionRole(int weight) {
		this.weight = weight;
	}
}
