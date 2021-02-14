package com.clusterfactions.clustercore.core.factions.map;

import java.awt.Color;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Renderer extends MapRenderer{

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
	    for (int x = 0; x < 128; ++x) {
	        for (int y = 0; y < 128; ++y) {
	            canvas.setPixel((int)player.getLocation().getX(), (int)player.getLocation().getY(), MapPalette.RED);
	        }
	    }
	}
		

}
