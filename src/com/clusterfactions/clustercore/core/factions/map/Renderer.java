package com.clusterfactions.clustercore.core.factions.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.util.location.Vector2Integer;
import com.clusterfactions.clustercore.util.model.Pair;

public class Renderer extends MapRenderer{
	
	public Renderer() {
		super(true);
	}
	
	FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
	
	int xMapCenter = -Integer.MAX_VALUE;
	int zMapCenter = -Integer.MAX_VALUE;
	
	//static byte[][][][] colorMap = new byte[128][128][128][128]; //mapX, mapZ, x, z
	
	boolean canRender = true;
	
	@SuppressWarnings("deprecation")
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
			/*WorldMap worldMap = null;
			Field worldField = CraftMapView.class.getDeclaredField("worldMap");
			worldField.setAccessible(true);
			worldMap = (WorldMap)worldField.get((CraftMapView)map);
			
			int xCenter = map.getCenterX()/128+64;
			int zCenter = map.getCenterZ()/128+64;
			
			
			byte[][] mapColor = new byte[128][128];
			
			if(colorMap[xCenter] == null)
				colorMap[xCenter] = new byte[128][128][128];
				
			if(colorMap[xCenter][zCenter] == null)
				colorMap[xCenter][zCenter] = new byte[128][128];
				
			if(colorMap[xCenter] != null && colorMap[xCenter][zCenter] != null)
				mapColor = colorMap[xCenter][zCenter];

			for(int x = 0; x < 128; x++) {
				for(int z = 0; z < 128; z++) {
					if(mapColor[x][z] == Byte.valueOf("0"))
					{
						canvas.setPixel(x, z, worldMap.colors[z * 128 + x]);
						mapColor[x][z] = worldMap.colors[z * 128 + x];
					}
					else
					{
						canvas.setPixel(x, z, mapColor[x][z]);
					}
				}
			}
			*/
			
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
	            	if(chunkFaction == null) 
	            		continue;
	            	else if(playerFaction.isAllied(chunkFaction))
	            		setPixel(canvas, pixelMap, MapPalette.matchColor(232, 53, 129));
	            	else
	            		setPixel(canvas, pixelMap, MapPalette.RED);
	            }
	        }
			//colorMap[xCenter][zCenter] = mapColor;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setPixel(MapCanvas canvas, List<Pair<Integer, Integer>> pixels, byte colour) {
		for(Pair<Integer,Integer> pixel : pixels) {
			canvas.setPixel(pixel.getLeft().intValue(), pixel.getRight().intValue(), colour);
		}
	}
}























