package com.clusterfactions.clustercore.core.inventory.impl.guide.props;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.core.inventory.impl.guide.OresGuideMenu;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.core.items.ItemRepo;
import com.clusterfactions.clustercore.core.listeners.events.updates.UpdateTickEvent;
import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.NumberUtil;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

import net.minecraft.server.v1_16_R3.Containers;

public class GuideFurnaceInventory extends InventoryBase{

	final static int tickRate = 10;
	private int lastTick = 0;
	
	private int currentProgress = 0;
	
	public GuideFurnaceInventory(Player player, ItemStack smelting, ItemStack output) {
		super(player, "FURNACE_PROP_GUIDE_MENU", "&f" + CharRepo.FURNACE_OVERRIDE_CONTAINER_27, 27);
		this.setItem(new ItemBuilder(ItemRepo.GUI_ARROW_LEFT).coloredName("&fBack").create(), e ->{new OresGuideMenu(player).openInventory(player);}, 0); 
		this.setItem(smelting, 2);
		this.setItem(new ItemStack(Material.COAL), 20);
		this.setItem(output, 5);
	}
	
	@Override
	public void updateTickEvent(UpdateTickEvent e) {
		lastTick++;
		if(lastTick != tickRate) return;		
		lastTick = 0;
		currentProgress++;
		if(currentProgress >= 22)
			currentProgress = 0;
		if(handlers.size() == 0) return;
		this.renameWindow(invInstance, Colors.parseColors("&f" + CharRepo.FURNACE_OVERRIDE_CONTAINER_27 + getFuelProgressString(14) + getProgressString(NumberUtil.clamp(currentProgress, 0, 22)) ));
	}
	
	private String getProgressString(int progress) {
		return CharRepo.fromName("FURNACE_PROGRESS_ARROW_"+progress);
	}
	
	private String getFuelProgressString(int progress) {
		return CharRepo.fromName("FURNACE_PROGRESS_FUEL_"+progress);
	}
}





















