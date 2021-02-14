package com.clusterfactions.clustercore.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ItemRepo {
		
	GUI_BASE(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100000).create()),
	GUI_UPPER_OVERLAY(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(120000).create()),
	TEST_BUTTON0(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100001).create()),
	TEST_BUTTON1(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100002).create()),
	TEST_BUTTON2(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100003).create()),
	TEST_BUTTON3(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100004).create()),
	TEST_BUTTON4(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100005).create()),
	TEST_BUTTON5(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100006).create()),
	TEST_BUTTON6(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100007).create()),
	TEST_BUTTON7(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100008).create()),
	TEST_BUTTON8(new ItemBuilder(Material.PAPER).name(" ").setCustomModelData(100009).create());
	
	private ItemRepo(ItemStack mat)
	{
		this.mat = mat;
	}
	
	public ItemStack mat;
	
}
