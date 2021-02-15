package com.clusterfactions.clustercore.core.inventory.impl;

import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.ItemRepo;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

public class TestMenu extends InventoryBase{

	public TestMenu(Player player) {

		super(player, "TEST_MENU", "&f" + CharRepo.MENU_CONTAINER + CharRepo.TEST_BUTTON, 54);
		
		this.setItem(new ItemBuilder(ItemRepo.GUI_BASE).coloredName("&6This is a test button!").create(), 0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21);
		 
	}
}
