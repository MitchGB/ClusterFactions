package com.clusterfactions.clustercore.util.location;

import org.bukkit.Location;

import lombok.Getter;

public class Vector2 {

	@Getter private double x;
	@Getter private double z;
	
	public Vector2(double x, double z)
	{
		this.x = x;
		this.z = z;
	}
	
	public Vector2(Location loc) {
		this.x = loc.getX();
		this.z = loc.getZ();
	}
	
	@Override
	public String toString() {
		return String.format("%.2f", x) + ":" + String.format("%.2f", z);
	}
}
