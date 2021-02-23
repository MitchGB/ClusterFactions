package com.clusterfactions.clustercore.core.inventory.impl.player.settings.lang;

import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.core.lang.LanguageManager;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

public class SettingsLanguageMenu extends InventoryBase{

	public SettingsLanguageMenu(Player player) {
		super(player, "PLAYER_SETTINGS_LANGUAGE", "&f" + CharRepo.MENU_CONTAINER_27, 27);
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		
		this.setItem(new ItemBuilder(Material.PAPER).coloredName("Current Langauge: " + playerData.getLocale().getDisplayLanguage()).create(), 3);
		
		for(int i = 9; i < LanguageManager.locales.length+9; i++)
		{
			Locale locale = LanguageManager.locales[i-9];
			this.setItem(new ItemBuilder(Material.WITHER_ROSE).coloredName(locale.getDisplayLanguage()).coloredLore("Click me to set language to " + locale.getDisplayLanguage()).create(), 
					e -> {
						playerData.setLocale(locale);
						playerData.saveData("locale");}
					,i);
		}
		
		 
	}
}
