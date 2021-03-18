package com.clusterfactions.clustercore.core.items.impl.ore;

import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.ItemRepo;
import com.clusterfactions.clustercore.core.items.block.CustomBlockType;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.PlaceableItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.SmeltableItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.StackableItem;
import com.clusterfactions.clustercore.util.ItemBuilder;

public class TitaniumOre extends CustomItem implements StackableItem, SmeltableItem, PlaceableItem{

	public TitaniumOre() {
		super(CustomItemType.TITANIUM_ORE, 
				new ItemBuilder(ItemRepo.TITANIUM_ORE).coloredName("&fTitanium Ore").create());
		
	}

	@Override
	public CustomItemType outputItem() {
		return CustomItemType.TITANIUM_INGOT;
	}

	@Override
	public CustomBlockType blockType() {
		return CustomBlockType.TITANIUM_ORE;
	}

	@Override
	public int smeltTime() {
		return 5000;
	}

	@Override
	public float expOutput() {
		return 0;
	}
}




