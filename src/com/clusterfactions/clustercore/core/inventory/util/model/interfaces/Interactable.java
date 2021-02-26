package com.clusterfactions.clustercore.core.inventory.util.model.interfaces;

import java.util.List;
import java.util.Set;

public interface Interactable {
	List<Integer> excludeSlot();
	
	public default boolean isExcluded(Set<Integer> slots) {
		for(Integer i : slots) {
			if(!excludeSlot().contains(i))
				return false;
		}
		return true;
	}
	
	public default boolean isExcluded(int slot) {
		return excludeSlot().contains(slot);
	}
}
