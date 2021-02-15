package com.clusterfactions.clustercore.util.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LocationUtil {

	public static String formatString(Location loc) {
		return "("+String.format("%.2f",loc.getX())+","+String.format("%.2f",loc.getY())+","+String.format("%.2f",loc.getZ())+")";
	}
	
	public static boolean withinBounds(Location obj, Location loc1, Location loc2) {
		double minX, maxX;
		double minY, maxY;
		double minZ, maxZ;
		minX = Math.min(loc1.getX(), loc2.getX());
		maxX = Math.max(loc1.getX(), loc2.getX());
		
		minY = Math.min(loc1.getY(), loc2.getY());
		maxY = Math.max(loc1.getY(), loc2.getY());
		
		minZ = Math.min(loc1.getZ(), loc2.getZ());
		maxZ = Math.max(loc1.getZ(), loc2.getZ());
		
		if(obj.getX() > maxX || obj.getX() < minX)
			return false;
		if(obj.getY() > maxY || obj.getY() < minY)
			return false;
		if(obj.getZ() > maxZ || obj.getZ() < minZ)
			return false;
	
		return true;		
	}
	
	//disregards z axis
	public static boolean withinBounds(Location obj, Vector2Integer loc1, Vector2Integer loc2) {
		World w = obj.getWorld();
		return withinBounds(obj, new Location(w, loc1.getX(), 256, loc1.getZ()), new Location(w, loc2.getX(), -256, loc2.getZ()));
	}
	

	//disregards z axis
	public static boolean withinBounds(World w, Vector2Integer obj, Vector2Integer loc1, Vector2Integer loc2)
	{
		return withinBounds(new Location(w, obj.getX(), 0, obj.getZ()), loc1, loc2);
		
	}
	
	public static String getDirection(Player player) {
		double rotation = (player.getLocation().getYaw() - 90.0F) % 360.0F;
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }
        if ((0.0D <= rotation) && (rotation < 22.5D)) {
            return "W";
        }
        if ((22.5D <= rotation) && (rotation < 67.5D)) {
            return "NW";
        }
        if ((67.5D <= rotation) && (rotation < 112.5D)) {
            return "N";
        }
        if ((112.5D <= rotation) && (rotation < 157.5D)) {
            return "NE";
        }
        if ((157.5D <= rotation) && (rotation < 202.5D)) {
            return "E";
        }
        if ((202.5D <= rotation) && (rotation < 247.5D)) {
            return "SE";
        }
        if ((247.5D <= rotation) && (rotation < 292.5D)) {
            return "S";
        }
        if ((292.5D <= rotation) && (rotation < 337.5D)) {
            return "SW";
        }
        if ((337.5D <= rotation) && (rotation < 360.0D)) {
            return "W";
        }
        return "";
	}

	
	
	
}







