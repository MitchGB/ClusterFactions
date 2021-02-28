package com.clusterfactions.clustercore.core.items.impl.ingot;

import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.ItemRepo;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.StackableItem;
import com.clusterfactions.clustercore.util.ItemBuilder;

public class TitaniumIngot extends CustomItem implements StackableItem{

	public TitaniumIngot() {
		super(CustomItemType.TITANIUM_INGOT, 
				new ItemBuilder(ItemRepo.TITANIUM_INGOT).coloredName("&fTitanium Ingot").create());
		
	}
}




