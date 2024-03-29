package com.clusterfactions.clustercore.util.location;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.fx.spectator.cinematic.util.CinematicFrame;
import com.clusterfactions.clustercore.util.NumberUtil;

public class LocationUtil {

	public static CinematicFrame[] lerp(Location from, Location to, int ticks, Entity toTeleport) {
		double xDiff = to.getX() - from.getX();
		double yDiff = to.getY() - from.getY();
		double zDiff = to.getZ() - from.getZ();
		float yawDiff = to.getYaw() - from.getYaw();
		float pitchDiff = to.getPitch() - from.getPitch();
		Location framePos = from.clone();
		List<CinematicFrame> sequences = new ArrayList<>();
		for(int i = 1; i <= ticks; i++) {
			sequences.add(new CinematicFrame(1, e -> toTeleport.teleport(add(framePos, xDiff/ticks, yDiff/ticks, zDiff/ticks, yawDiff/ticks, pitchDiff/ticks)) ));
		}
		return sequences.toArray(new CinematicFrame[sequences.size()]);
	}
	
	public static CinematicFrame[] lerpBlock(Location from, Location to, int ticks, Material block, Player player) {
		double xDiff = to.getX() - from.getX();
		double yDiff = to.getY() - from.getY();
		double zDiff = to.getZ() - from.getZ();
		float yawDiff = to.getYaw() - from.getYaw();
		float pitchDiff = to.getPitch() - from.getPitch();
		
		Location framePos = from.clone();
		List<CinematicFrame> sequences = new ArrayList<>();
		
		for(int i = 1; i <= ticks; i++) {
			sequences.add(
					new CinematicFrame(1, e -> {
						Location nextLoc = cloneAdd(framePos, xDiff/ticks, yDiff/ticks, zDiff/ticks, yawDiff/ticks, pitchDiff/ticks);
						if(nextLoc.getBlock().getType() == Material.AIR)
							player.sendBlockChange(framePos, Material.AIR.createBlockData());
						
						add(framePos, xDiff/ticks, yDiff/ticks, zDiff/ticks, yawDiff/ticks, pitchDiff/ticks);
						
						if(framePos.getBlock().getType() != block)
							player.sendBlockChange(framePos, block.createBlockData());
					} ));
		}
		return sequences.toArray(new CinematicFrame[sequences.size()]);
	}

	public static String formatString(Location loc) {
		return "("+String.format("%.2f",loc.getX())+","+String.format("%.2f",loc.getY())+","+String.format("%.2f",loc.getZ())+")";
	}
	
	public static Location findSafeLoc(Player player) {
		Location location = player.getLocation().clone();
		int x = NumberUtil.random(-5000, 5000);
		int z = NumberUtil.random(-5000, 5000);
		location.setX(x);
		location.setY(location.getWorld().getHighestBlockYAt(x, z)+1);
		location.setZ(z);
		Block ground = location.getBlock().getRelative(BlockFace.DOWN);
		Block head = location.getBlock().getRelative(BlockFace.UP);
		if(ClusterCore.getInstance().getFactionClaimManager().isChunkClaimed(location))
			return findSafeLoc(player);
		if(location.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.WATER))
			return findSafeLoc(player);		
		if(location.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.LAVA))
			return findSafeLoc(player);
		if (head.getType().isSolid())
			return findSafeLoc(player); 
		if (ground.getType().isAir())
			return findSafeLoc(player); 
	    if (ground.getType().isSolid())
	   		return location; 
	    return location;
		
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

	public static Location add(Location loc, double x, double y, double z, float yaw, float pitch) {
		loc.add(x, y, z);
		loc.setYaw(loc.getYaw() + yaw);
		loc.setPitch(loc.getPitch() + pitch);
		return loc;
	}
	
	public static Location cloneAdd(Location loc, double x, double y, double z, float yaw, float pitch) {
		Location ret = loc.clone();
		ret.add(x, y, z);
		ret.setYaw(ret.getYaw() + yaw);
		ret.setPitch(ret.getPitch() + pitch);
		return ret;
	}
	
	
}







