package com.clusterfactions.clustercore.util.location;

import org.bukkit.Location;

import lombok.Getter;

public class Vector2Integer {

	@Getter private int x;
	@Getter private int z;
	
	public Vector2Integer(int x, int z)
	{
		this.x = x;
		this.z = z;
	}
	
	public Vector2Integer(Location loc) {
		this.x = (int)loc.getX();
		this.z = (int)loc.getZ();
	}
	
	@Override
	public String toString() {
		return x + ":" + z;
	}
}
