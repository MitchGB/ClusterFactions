package com.clusterfactions.clustercore.core.factions.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.claim.Chunk;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.util.NumberUtil;
import com.clusterfactions.clustercore.util.location.Vector2Integer;
import com.clusterfactions.clustercore.util.model.Pair;

public class Renderer extends MapRenderer{
	
	public Renderer() {
		super(true);
	}
	
	byte[][] colorMap = new byte[128][128];
	
	FactionClaimManager claimManager = ClusterCore.getInstance().getFactionClaimManager();
	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		final int radius = 5;
		map.setCenterX((int) player.getLocation().getX());
		map.setCenterZ((int) player.getLocation().getZ());
		Vector2Integer playerChunk = claimManager.getChunkVector(player.getLocation());
		
		Vector2Integer[][] claimMap = new Vector2Integer[radius*2][radius*2];
		
		int pX = playerChunk.getX();
		int pZ = playerChunk.getZ();

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
		
		int xoffset = (int)player.getLocation().getX() - (NumberUtil.roundDown(player.getLocation().getX() / 16, 1) * 16);
		int zoffset = (int)player.getLocation().getZ() - (NumberUtil.roundDown(player.getLocation().getZ() / 16, 1) * 16);
		
		
        for (int x = 0; x < 10; ++x) {
            for (int z = 0; z < 10; ++z) {
            	Vector2Integer chunkLoc = claimMap[x][z];
            	List<Pair<Integer,Integer>> pixelMap = new ArrayList<>();
            	List<Pair<Integer,Integer>> resetPixelMap = new ArrayList<>();
            	
            	//color map made per chunk
//            	Chunk chunk = claimManager.chunkClaimedCache(chunkLoc);
 //   			byte[][] colorMap = chunk.getColorMap() == null ? new byte[16][16] : chunk.getColorMap();
    			
            	for(int px = 0; px < 16; px++)
            	{
            		for(int pz = 0; pz < 16; pz++)
            		{
            			if(pz == 0 || px == 0 || px == 15 || pz == 15)
            				pixelMap.add(new Pair<Integer,Integer>(x * 16 - 16 - xoffset + px, z * 16 - 16 -zoffset + pz));
            			else
            				resetPixelMap.add(new Pair<Integer,Integer>(x * 16 - 16 - xoffset + px, z * 16 - 16 -zoffset + pz));
            			/*{
                			byte color = canvas.getPixel(x * 16 - 16 - xoffset + px, z * 16 - 16 -zoffset + pz);
                			if(color != Byte.valueOf("0"))
                				colorMap[px][pz] = color;
            				canvas.setPixel(x * 16 - 16 - xoffset + px, z * 16 - 16 -zoffset + pz, color);
            			}*/
            			
            		}
            	}
            	/*
            	for(int i = 0; i < 16; i++)
            	{
            		for(int j = 0; j < 16; j++)
            		{
            			if(chunk.getColorMap() == null)
            			{
            				chunk.setColorMap(new byte[16][16]);
            			}
            			if(chunk.getColorMap()[i] == null)
            			{
            				chunk.getColorMap()[i]=new byte[16];
            			}
            			
            			if(chunk.getColorMap()[i][j] == Byte.valueOf("0"))
            			{
            				chunk.getColorMap()[i][j] = colorMap[i][j];
            			}
            		}
            	}
            	claimManager.replaceChunk(chunk.getLocation(), chunk);
            	*/
            	
            	if(claimManager.chunkClaimedCache(chunkLoc) != null)
            		setPixel(canvas, pixelMap, MapPalette.RED);
            	else
            		setPixel(canvas, pixelMap, MapPalette.DARK_GREEN);
            	resetPixel(canvas, resetPixelMap);
            }
        }
        MapCursorCollection cursors = canvas.getCursors();
        while (cursors.size() > 0) {
            cursors.removeCursor(cursors.getCursor(0));
        }
        
	}
	
	private void setPixel(MapCanvas canvas, List<Pair<Integer, Integer>> pixels, byte colour) {
		for(Pair<Integer,Integer> pixel : pixels) {
			canvas.setPixel(pixel.getLeft().intValue(), pixel.getRight().intValue(), colour);
		}
	}
	
	@SuppressWarnings("unused")
	private void resetPixel(MapCanvas canvas, List<Pair<Integer, Integer>> pixels) {
		for(Pair<Integer,Integer> pixel : pixels) {
			canvas.setPixel(pixel.getLeft().intValue(), pixel.getRight().intValue(), canvas.getBasePixel(pixel.getLeft().intValue(), pixel.getRight().intValue()));
		}
	}
}























