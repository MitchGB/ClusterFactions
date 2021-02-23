package com.clusterfactions.clustercore.core.inventory.impl.player.settings;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.core.inventory.impl.player.settings.lang.SettingsLanguageMenu;
import com.clusterfactions.clustercore.core.inventory.impl.player.settings.map.MapMainSettingsMenu;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

public class MainSettingsMenu extends InventoryBase{

	public MainSettingsMenu(Player player) {

		super(player, "PLAYER_SETTINGS", "&f" + CharRepo.MENU_CONTAINER_27, 27);
		this.addItem(new ItemBuilder(Material.ACACIA_BOAT).coloredName("Language Settings").create(), e_-> { new SettingsLanguageMenu(player).openInventory(player);});
		this.addItem(new ItemBuilder(Material.FILLED_MAP).coloredName("Map Settings").create(), e_-> { new MapMainSettingsMenu(player).openInventory(player);});
		 
	}
}
