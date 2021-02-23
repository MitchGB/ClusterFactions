package com.clusterfactions.clustercore.core.inventory.impl.player.settings.map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.impl.player.settings.map.types.MapAllySettingsMenu;
import com.clusterfactions.clustercore.core.inventory.impl.player.settings.map.types.MapEmptySettingsMenu;
import com.clusterfactions.clustercore.core.inventory.impl.player.settings.map.types.MapEnemySettingsMenu;
import com.clusterfactions.clustercore.core.inventory.impl.player.settings.map.types.MapFactionSettingsMenu;
import com.clusterfactions.clustercore.core.inventory.impl.player.settings.map.types.MapNeutralSettingsMenu;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

public class MapMainSettingsMenu extends InventoryBase{

	public MapMainSettingsMenu(Player player) {
		super(player, "MAP_MAIN_MENU_SETTINGS", "&f" + CharRepo.MENU_CONTAINER_27, 27);
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		
		this.addItem(new ItemBuilder(Material.DIRT).coloredName("Enemy Color").coloredLore("&7Current color: &b" + playerData.getMapEnemyColour()).create(), e ->{new MapEnemySettingsMenu(player).openInventory(player);});
		this.addItem(new ItemBuilder(Material.DIRT).coloredName("Ally Color").coloredLore("&7Current color: &b" + playerData.getMapAllyColour()).create(), e ->{new MapAllySettingsMenu(player).openInventory(player);});
		this.addItem(new ItemBuilder(Material.DIRT).coloredName("Neutral Color").coloredLore("&7Current color: &b" + playerData.getMapNeutralColour()).create(), e ->{new MapNeutralSettingsMenu(player).openInventory(player);});
		this.addItem(new ItemBuilder(Material.DIRT).coloredName("Empty Color").coloredLore("&7Current color: &b" + playerData.getMapEmptyColour()).create(), e ->{new MapEmptySettingsMenu(player).openInventory(player);});
		this.addItem(new ItemBuilder(Material.DIRT).coloredName("Own Faction Color").coloredLore("&7Current color: &b" + playerData.getMapFactionColour()).create(), e ->{new MapFactionSettingsMenu(player).openInventory(player);});
		
		 
	}
}
