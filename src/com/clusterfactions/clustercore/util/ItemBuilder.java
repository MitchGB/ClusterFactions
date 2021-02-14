package com.clusterfactions.clustercore.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;

public class ItemBuilder {
	 private ItemStack item;
	  
	  public ItemBuilder(Material material) {
	    this.item = new ItemStack(material);	   
	    ItemMeta meta = this.item.getItemMeta();
	    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
	    item.setItemMeta(meta);
	  }
	  
	  public ItemBuilder(ItemRepo repo) {
		  this.item = new ItemStack(repo.mat);
		  ItemMeta meta = this.item.getItemMeta();
		  meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		  item.setItemMeta(meta);
	  }
	  
	  public ItemBuilder(ItemStack item) {
	    this.item = item;	    
	    ItemMeta meta = this.item.getItemMeta();
	    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
	    item.setItemMeta(meta);
	  }
	  
	  public ItemBuilder(OfflinePlayer skullOwner) {
	    this.item = (new ItemBuilder(Material.PLAYER_HEAD)).skullOwner(skullOwner).create();
	    ItemMeta meta = this.item.getItemMeta();
	    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
	    item.setItemMeta(meta);
	  }
	  
	  public ItemBuilder amount(int amount) {
	    this.item.setAmount(amount);
	    return this;
	  }
	  
	  public ItemBuilder name(String name) {
	    ItemMeta meta = this.item.getItemMeta();
	    meta.setDisplayName(name);
	    this.item.setItemMeta(meta);
	    return this;
	  }
	  
	  public ItemBuilder coloredName(String name) {
	    ItemMeta meta = this.item.getItemMeta();
	    meta.setDisplayName(Colors.parseColors(name));
	    this.item.setItemMeta(meta);
	    return this;
	  }
	  
	  public ItemBuilder lore(String... lore) {
	    ItemMeta meta = this.item.getItemMeta();
	    meta.setLore(Arrays.asList(lore));
	    this.item.setItemMeta(meta);
	    return this;
	  }
	  
	  public ItemBuilder coloredLore(String... lore) {
	    ItemMeta meta = this.item.getItemMeta();
	    meta.setLore(Colors.parseColors(Arrays.asList(lore)));
	    this.item.setItemMeta(meta);
	    return this;
	  }
	  
	  public ItemBuilder lore(List<String> lore) {
	    ItemMeta meta = this.item.getItemMeta();
	    meta.setLore(lore);
	    this.item.setItemMeta(meta);
	    return this;
	  }
	  
	  public ItemBuilder coloredLore(List<String> lore) {
	    ItemMeta meta = this.item.getItemMeta();
	    meta.setLore(Colors.parseColors(lore));
	    this.item.setItemMeta(meta);
	    return this;
	  }
	  
	  public ItemBuilder skullOwner(OfflinePlayer player) {
	    SkullMeta meta = (SkullMeta)this.item.getItemMeta();
	    meta.setOwningPlayer(player);
	    this.item.setItemMeta((ItemMeta)meta);
	    return this;
	  }
	  
	  public ItemBuilder skullTexture(String texture) {
	    NBTItem nbt = new NBTItem(this.item);
	    NBTCompound skullOwner = nbt.addCompound("SkullOwner");
	    skullOwner.setString("Id", UUID.randomUUID().toString());
	    skullOwner.addCompound("Properties").getCompoundList("textures").addCompound().setString("Value", texture);
	    this.item = nbt.getItem();
	    return this;
	  }
	  
	  public ItemBuilder leatherArmorColor(Color color) {
	    LeatherArmorMeta meta = (LeatherArmorMeta)this.item.getItemMeta();
	    meta.setColor(color);
	    this.item.setItemMeta((ItemMeta)meta);
	    return this;
	  }
	  
	  public ItemBuilder enchant(Enchantment type, int level) {
	    this.item.addEnchantment(type, level);
	    return this;
	  }
	  
	  public ItemBuilder unsafeEnchant(Enchantment type, int level) {
	    this.item.addUnsafeEnchantment(type, level);
	    return this;
	  }
	  
	  public ItemBuilder material(Material material) {
	    this.item.setType(material);
	    return this;
	  }
	  
	  public ItemBuilder type(Material type) {
	    this.item.setType(type);
	    return this;
	  }
	  
	  public ItemBuilder unbreakable() {
	    ItemMeta meta = this.item.getItemMeta();
	    meta.setUnbreakable(true);
	    this.item.setItemMeta(meta);
	    return this;
	  }
	  
	  public ItemBuilder damage(int damage) {
	    Damageable damageable = (Damageable)this.item.getItemMeta();
	    damageable.setDamage(damage);
	    this.item.setItemMeta((ItemMeta)damageable);
	    return this;
	  }
	  
	  public ItemBuilder hideFlags(int hideFlags) {
	    NBTItem nbt = new NBTItem(this.item);
	    nbt.setInteger("HideFlags", Integer.valueOf(hideFlags));
	    this.item = nbt.getItem();
	    return this;
	  }
	  
	  public ItemBuilder namePlaceholder(String key, String value) {
	    if (this.item.getItemMeta() == null || this.item.getItemMeta().getDisplayName() == null)
	      return this; 
	    return name(this.item.getItemMeta().getDisplayName().replace(key, value));
	  }
	  
	  public ItemBuilder namePlaceholders(Map<String, String> placeholders) {
	    if (this.item.getItemMeta() == null || this.item.getItemMeta().getDisplayName() == null)
	      return this; 
	    placeholders.forEach(this::namePlaceholder);
	    return this;
	  }
	  
	  public ItemBuilder namePlaceholderOptional(String key, Supplier<String> value) {
	    if (this.item.getItemMeta() == null || this.item.getItemMeta().getDisplayName() == null)
	      return this; 
	    String oldName = this.item.getItemMeta().getDisplayName();
	    if (oldName.contains(key))
	      return name(oldName.replace(key, value.get())); 
	    return this;
	  }
	  
	  public ItemBuilder namePlaceholdersOptional(Map<String, Supplier<String>> placeholders) {
	    if (this.item.getItemMeta() == null || this.item.getItemMeta().getDisplayName() == null)
	      return this; 
	    placeholders.forEach(this::namePlaceholderOptional);
	    return this;
	  }
	  
	  public ItemBuilder lorePlaceholder(String key, String value) {
	    if (this.item.getItemMeta() == null || this.item.getItemMeta().getLore() == null)
	      return this; 
	    return lore((List<String>)this.item.getItemMeta().getLore().stream().map(s -> s.replace(key, value)).collect(Collectors.toList()));
	  }
	  
	  public ItemBuilder lorePlaceholders(Map<String, String> placeholders) {
	    if (this.item.getItemMeta() == null || this.item.getItemMeta().getLore() == null)
	      return this; 
	    placeholders.forEach(this::lorePlaceholder);
	    return this;
	  }
	  
	  public ItemBuilder lorePlaceholderOptional(String key, Supplier<String> value) {
	    if (this.item.getItemMeta() == null || this.item.getItemMeta().getLore() == null)
	      return this; 
	    return lore((List<String>)this.item.getItemMeta().getLore().stream().map(s -> s.contains(key) ? s.replace(key, value.get()) : s)
	        
	        .collect(Collectors.toList()));
	  }
	  
	  public ItemBuilder lorePlaceholdersOptional(Map<String, Supplier<String>> placeholders) {
	    if (this.item.getItemMeta() == null || this.item.getItemMeta().getLore() == null)
	      return this; 
	    placeholders.forEach(this::lorePlaceholderOptional);
	    return this;
	  }
	  
	  public ItemBuilder placeholder(String key, String value) {
	    return namePlaceholder(key, value).lorePlaceholder(key, value);
	  }
	  
	  public ItemBuilder placeholders(Map<String, String> placeholders) {
	    return namePlaceholders(placeholders).lorePlaceholders(placeholders);
	  }
	  
	  public ItemBuilder placeholderOptional(String key, Supplier<String> value) {
	    return namePlaceholderOptional(key, value).lorePlaceholderOptional(key, value);
	  }
	  
	  public ItemBuilder placeholdersOptional(Map<String, Supplier<String>> placeholders) {
	    return namePlaceholdersOptional(placeholders).lorePlaceholdersOptional(placeholders);
	  }
	  
	  public ItemBuilder setCustomModelData(int data) {
		  ItemMeta meta = this.item.getItemMeta();
		  meta.setCustomModelData(data);
		  this.item.setItemMeta(meta);
		  return this;
	  }
	  
	  public ItemStack create() {
	    return this.item;
	  }
}
