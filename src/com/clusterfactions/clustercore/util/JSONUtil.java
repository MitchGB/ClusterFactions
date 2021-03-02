package com.clusterfactions.clustercore.util;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("deprecation")
public class JSONUtil {
	public static TextComponent getItemToJSON(ItemStack item) {
		TextComponent msg = new TextComponent(item.getItemMeta().getDisplayName());
		
		StringBuilder toAdd = new StringBuilder();
		toAdd.append(item.getItemMeta().getDisplayName() + "\n");
		item.getItemMeta().getLore().forEach(e -> toAdd.append(e + "\n"));
		String add = toAdd.toString();
		add = add.substring(0, add.length() -1);
		BaseComponent[] components = new ComponentBuilder(add).create();
		msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, components));

		return msg;
	}
	
	public static TextComponent getJSON(String text, @Nullable String cmd, boolean hover, List<String> desc) {
		return getJSON(text, cmd, hover, desc.toArray(new String[desc.size()]));
	}
	
	public static TextComponent getJSON(String text, @Nullable String cmd, boolean hover, String... desc) {
		TextComponent msg = new TextComponent(text);
		
		StringBuilder toAdd = new StringBuilder();
		Arrays.asList(desc).forEach(e -> toAdd.append(e + "\n"));
		String add = toAdd.toString();
		add = add.substring(0, add.length() -1);
		BaseComponent[] components = new ComponentBuilder(add).create();
		
		if(hover)
			msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, components));

		if(cmd != null)
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + cmd ));
		return msg;
	}
	
	public static TextComponent getJSONURL(String text, @Nullable String url, String... desc) {
		TextComponent msg = new TextComponent(text);
		
		StringBuilder toAdd = new StringBuilder();
		Arrays.asList(desc).forEach(e -> toAdd.append(e + "\n"));
		String add = toAdd.toString();
		add = add.substring(0, add.length() -1);
		BaseComponent[] components = new ComponentBuilder(add).create();
		
		msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, components));

		if(url != null)
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url ));
		return msg;
	}
}
