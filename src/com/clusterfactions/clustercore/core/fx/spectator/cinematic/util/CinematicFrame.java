package com.clusterfactions.clustercore.core.fx.spectator.cinematic.util;

import lombok.Getter;

public class CinematicFrame {
	@Getter public int delay;
	@Getter public CinematicFunction function;
	
	public CinematicFrame(int delay, CinematicFunction function) {
		this.delay = delay;
		this.function = function;
	}
	
	public CinematicFrame[] singleArray() {
		return new CinematicFrame[] {this};
	}
}
