package com.clusterfactions.clustercore.core.items.impl.ore;

import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.ItemRepo;
import com.clusterfactions.clustercore.core.items.block.CustomBlockType;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.PlaceableItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.SmeltableItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.StackableItem;
import com.clusterfactions.clustercore.util.ItemBuilder;

public class NickelOre extends CustomItem implements StackableItem, SmeltableItem, PlaceableItem{

	public NickelOre() {
		super(CustomItemType.NICKEL_ORE, 
				new ItemBuilder(ItemRepo.NICKEL_ORE).coloredName("&fNickel Ore").create());
		
	}

	@Override
	public CustomItemType outputItem() {
		return CustomItemType.NICKEL_INGOT;
	}

	@Override
	public CustomBlockType blockType() {
		return CustomBlockType.NICKEL_ORE;
	}

	@Override
	public int smeltTime() {
		return 5000;
	}
}




