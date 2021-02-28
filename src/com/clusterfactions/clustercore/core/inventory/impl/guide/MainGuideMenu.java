package com.clusterfactions.clustercore.core.inventory.impl.guide;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

public class MainGuideMenu extends InventoryBase{

	public MainGuideMenu(Player player) {

		super(player, "MAIN_GUIDE_MENU", "&f" + CharRepo.MENU_CONTAINER_27, 27);
		this.addItem(new ItemBuilder(Material.DIAMOND_ORE).coloredName("&fOres").create(), e -> { new OresGuideMenu(player).openInventory(player);});
		
	}
}
