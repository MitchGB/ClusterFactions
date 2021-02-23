package com.clusterfactions.clustercore.core.factions.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.location.Vector2Integer;
import com.clusterfactions.clustercore.util.model.Pair;

public class Renderer extends MapRenderer{
	
	public Renderer() {
		super(true);
	}
	
	FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
	
	int xMapCenter = -Integer.MAX_VALUE;
	int zMapCenter = -Integer.MAX_VALUE;
	
	boolean canRender = true;
	
	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if(canRender != true) return;
		canRender = false;
		
		//BUFFER TO RENDER (USUALLY EVERY TICK)
		Bukkit.getScheduler().runTaskLaterAsynchronously(ClusterCore.getInstance(), new Runnable() {
	        @Override
	        public void run() {
	        	canRender = true;
	        }
		}, 10);
		
		try {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
			int radius = 5;
			
			Vector2Integer centerChunk = claimManager.getChunkVector(map.getCenterX(), map.getCenterZ());
			Vector2Integer[][] claimMap = new Vector2Integer[radius*2][radius*2];
			
			int pX = centerChunk.getX();
			int pZ = centerChunk.getZ();

			int uX = pX + radius;
			int uZ = pZ + radius ;
			
			int lX = pX - radius;
			int lZ = pZ - radius;
			int xIndex = 0;
			int zIndex = 0;
			
			for(int z = lZ; z < uZ; z++)
			{
				xIndex = 0;
				for(int x = lX; x <uX; x++)
				{
					claimMap[xIndex][zIndex] = new Vector2Integer(x,z);
					xIndex++;
				}
				zIndex++;
			}
			
			for (int x = 0; x < radius*2; ++x) {
	            for (int z = 0; z < radius*2; ++z) {
	            	Vector2Integer chunkLoc = claimMap[x][z];
	            	List<Pair<Integer,Integer>> pixelMap = new ArrayList<>();
	            	
	            	for(int px = 0; px < 16; px++)
	            	{
	            		for(int pz = 0; pz < 16; pz++)
	            		{
	            			if(pz == 0 || px == 0 || px == 15 || pz == 15)
	            				pixelMap.add(new Pair<Integer,Integer>((x*16) - 32 + px, (z*16) - 32 + pz));
	            			
	            		}
	            	}
	            	
	            	Faction chunkFaction = ClusterCore.getInstance().getFactionsManager().getFaction(claimManager.chunkClaimedCache(chunkLoc));
	            	Faction playerFaction = ClusterCore.getInstance().getFactionsManager().getFaction(ClusterCore.getInstance().getPlayerManager().getPlayerData(player).getFaction());
	            	if(chunkFaction == null) {
	            		if(playerData.getMapEmptyColour() == MapColour.TRANSPARENT) continue;
	            		setPixel(canvas, pixelMap, playerData.getMapEmptyColour());
	            	}else if(playerFaction != null && playerFaction.isSame(chunkFaction)){
	            		if(playerData.getMapFactionColour() == MapColour.TRANSPARENT) continue;
	            		setPixel(canvas, pixelMap, playerData.getMapFactionColour());
	            	}else if(playerFaction != null && playerFaction.isAlly(chunkFaction)){
	            		if(playerData.getMapAllyColour() == MapColour.TRANSPARENT) continue;
	            		setPixel(canvas, pixelMap, playerData.getMapAllyColour());
	            	}else if(playerFaction != null && playerFaction.isEnemy(chunkFaction)) {
	            		if(playerData.getMapEnemyColour() == MapColour.TRANSPARENT) continue;
	            		setPixel(canvas, pixelMap, playerData.getMapEnemyColour());
	            	}else {
	            		if(playerData.getMapNeutralColour() == MapColour.TRANSPARENT) continue;
	            		setPixel(canvas, pixelMap, playerData.getMapNeutralColour());
	            	}
	            }
	        }
			//colorMap[xCenter][zCenter] = mapColor;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setPixel(MapCanvas canvas, List<Pair<Integer, Integer>> pixels, MapColour colour) {
		setPixel(canvas, pixels, colour.getValue());
	}
	
	private void setPixel(MapCanvas canvas, List<Pair<Integer, Integer>> pixels, byte colour) {
		for(Pair<Integer,Integer> pixel : pixels) {
			canvas.setPixel(pixel.getLeft().intValue(), pixel.getRight().intValue(), colour);
		}
	}
}























