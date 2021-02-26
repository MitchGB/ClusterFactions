package com.clusterfactions.clustercore.core.items.impl;

import org.bukkit.Material;

import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.StackableItem;
import com.clusterfactions.clustercore.util.ItemBuilder;

public class TestCookedItem extends CustomItem implements StackableItem{

	public TestCookedItem() {
		super(CustomItemType.TEST_COOKED_ITEM, 
				new ItemBuilder(Material.IRON_INGOT).setCustomModelData(1).coloredName("&dA Very Useful Ore").create());
		
	}
}




