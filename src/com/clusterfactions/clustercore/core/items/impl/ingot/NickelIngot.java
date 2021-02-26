package com.clusterfactions.clustercore.core.items.impl.ingot;

import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.ItemRepo;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.StackableItem;
import com.clusterfactions.clustercore.util.ItemBuilder;

public class NickelIngot extends CustomItem implements StackableItem{

	public NickelIngot() {
		super(CustomItemType.NICKEL_INGOT, 
				new ItemBuilder(ItemRepo.NICKEL_INGOT).coloredName("&fNickel Ingot").create());
		
	}
}




