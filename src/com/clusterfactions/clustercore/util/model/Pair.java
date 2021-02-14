package com.clusterfactions.clustercore.util.model;

import lombok.Getter;

public class Pair<L, R> {
	
	@Getter public R right;
	@Getter public L left;
	
	public Pair(L left, R right){
		this.right = right;
		this.left = left;
	}
}
