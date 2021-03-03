package com.clusterfactions.clustercore.core.fx.spectator.cinematic;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.fx.spectator.cinematic.util.CinematicFrame;

public class CinematicSequence {
	public List<CinematicFrame> frames;
	public boolean exitFinish;
	
	public CinematicSequence(boolean exitFinish, CinematicFrame... frames) {
		this.frames = Arrays.asList(frames);
		this.exitFinish = exitFinish;
	}
	
	public CinematicSequence(CinematicFrame... frames) {
		this.frames = Arrays.asList(frames);
		this.exitFinish = false;
	}
	
	public void execute(Player player) {
		Deque<CinematicFrame> iterator = new ArrayDeque<>();
		iterator.addAll(frames);		
		Bukkit.getScheduler().runTaskLater(ClusterCore.getInstance(), new Runnable() {
	        @Override
	        public void run() {
	        	nextFrame(player, iterator);
	        }
		}, iterator.peek().delay);
	}
	
	public void nextFrame(Player player, Deque<CinematicFrame> iterator) {
		CinematicFrame frame = iterator.pop();
		frame.getFunction().exec(player);
		if(!iterator.isEmpty()) {
			Bukkit.getScheduler().runTaskLater(ClusterCore.getInstance(), new Runnable() {
				@Override
				public void run() {
	        		nextFrame(player, iterator);
	        	}
			}, iterator.peek().delay);
		}else if(exitFinish){
			ClusterCore.getInstance().getSpectatorManager().stopSpectatorMode(player);
		}
	}

	public static CinematicFrame[] mergeArrays(CinematicFrame[] ...arrays)
	{
		return Stream.of(arrays)
						.flatMap(Stream::of)		
						.toArray(CinematicFrame[]::new);
	}

}























