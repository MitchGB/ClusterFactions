package com.clusterfactions.clustercore.core.inventory.impl.faction.perm;

import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.core.items.ItemRepo;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

public class MainPermissionMenu extends InventoryBase{

	public MainPermissionMenu(Player player) {

		super(player, "MAIN_PERM_MENU", "&f" + CharRepo.MENU_CONTAINER_27 + CharRepo.UI_PERMISSIONS_FACTIONS_BUTTON, 27);
		this.setItem(new ItemBuilder(ItemRepo.GUI_BASE.getItem()).coloredName("&7Faction Permissions").coloredLore(" ", " ", "&aClick Me").create(), e -> {new FactionPermissionMenu(player).openInventory(player);}, 0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21);
		 
	}
}
