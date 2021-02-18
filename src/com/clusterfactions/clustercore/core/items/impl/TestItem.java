package com.clusterfactions.clustercore.core.items.impl;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.InteractableItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.StackableItem;
import com.clusterfactions.clustercore.util.ItemBuilder;

public class TestItem extends CustomItem implements InteractableItem, StackableItem{

	public TestItem() {
		super(CustomItemType.TEST_ITEM, new ItemBuilder(Material.STICK).create(), "&dA Very Useful Stick", "&7This stick seems to be very useful", "&7When right clicked, a bolt of lightning is struck");
		
	}

	@Override
	public void leftClick(PlayerInteractEvent e) {
		
	}

	@Override
	public void rightClick(PlayerInteractEvent e) {
		if(!isApplicableItem(e.getItem())) return;
	
	}

}




