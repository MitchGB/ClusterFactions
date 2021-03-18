package com.clusterfactions.clustercore.core.items.types.interfaces;

import com.clusterfactions.clustercore.core.items.CustomItemType;

public interface SmeltableItem {
	public CustomItemType outputItem();
	public int smeltTime();
	public float expOutput();
}
