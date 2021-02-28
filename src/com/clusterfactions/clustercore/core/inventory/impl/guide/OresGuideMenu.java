package com.clusterfactions.clustercore.core.inventory.impl.guide;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.core.inventory.impl.guide.props.GuideFurnaceInventory;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.ItemRepo;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

public class OresGuideMenu extends InventoryBase{

	public OresGuideMenu(Player player) {

		super(player, "ORES_GUIDE_MENU", "&f" + CharRepo.MENU_CONTAINER_27, 27);
		this.setItem(new ItemBuilder(ItemRepo.GUI_ARROW_LEFT).coloredName("&fBack").create(), e ->{new MainGuideMenu(player).openInventory(player);}, 0); 
		
		this.setItem(new ItemBuilder(Material.COAL_ORE).coloredName("&fCoal Ore").create(), e -> { new GuideFurnaceInventory(player, new ItemBuilder(Material.COAL_ORE).create(), new ItemBuilder(Material.COAL).create()).openInventory(player);}, 9);
		
		this.setItem(new ItemBuilder(Material.IRON_ORE).coloredName("&fIron Ore").create(), e -> { new GuideFurnaceInventory(player, new ItemBuilder(Material.IRON_ORE).create(), new ItemBuilder(Material.IRON_INGOT).create()).openInventory(player);}, 10);
		
		this.setItem(new ItemBuilder(Material.GOLD_ORE).coloredName("&fGold Ore").create(), e -> { new GuideFurnaceInventory(player, new ItemBuilder(Material.GOLD_ORE).create(), new ItemBuilder(Material.GOLD_INGOT).create()).openInventory(player);}, 11);
		
		this.setItem(new ItemBuilder(CustomItemType.NICKEL_ORE).coloredName("&fNickel Ore").create(), e -> { new GuideFurnaceInventory(player, new ItemBuilder(CustomItemType.NICKEL_ORE).create(), new ItemBuilder(CustomItemType.NICKEL_INGOT).create()).openInventory(player);}, 11);
		
		this.setItem(new ItemBuilder(Material.REDSTONE_ORE).coloredName("&fRedstone Ore").create(), e -> { new GuideFurnaceInventory(player, new ItemBuilder(Material.REDSTONE_ORE).create(), new ItemBuilder(Material.REDSTONE).create()).openInventory(player);}, 12);
		
		this.setItem(new ItemBuilder(Material.LAPIS_ORE).coloredName("&fLapis Ore").create(), e -> { new GuideFurnaceInventory(player, new ItemBuilder(Material.LAPIS_ORE).create(), new ItemBuilder(Material.LAPIS_LAZULI).create()).openInventory(player);}, 13);
		
		this.setItem(new ItemBuilder(Material.DIAMOND_ORE).coloredName("&fDiamond Ore").create(), e -> { new GuideFurnaceInventory(player, new ItemBuilder(Material.DIAMOND_ORE).create(), new ItemBuilder(Material.DIAMOND).create()).openInventory(player);}, 14);
		
		this.setItem(new ItemBuilder(Material.EMERALD_ORE).coloredName("&fEmerald Ore").create(), e -> { new GuideFurnaceInventory(player, new ItemBuilder(Material.EMERALD_ORE).create(), new ItemBuilder(Material.EMERALD).create()).openInventory(player);}, 15);
		
		
	}
}
