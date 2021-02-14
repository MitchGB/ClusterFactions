package com.clusterfactions.clustercore.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Colors {
	  public static List<String> parseColors(List<String> list) {
		  return (List<String>)list.stream().map(Colors::parseColors).collect(Collectors.toList());
	  }
		  
	  public static List<String> parseColors(String... list) {
		    return (List<String>)Arrays.asList(list).stream().map(Colors::parseColors).collect(Collectors.toList());
	  }
	  
	  public static String parseColors(String string) {
		  return ChatColor.translateAlternateColorCodes('&', string);
	  }
		  
	  public static BaseComponent[] toComponent(String string) {
		  return TextComponent.fromLegacyText(parseColors(string));
	  }
		  
	  public static String stripColors(String string) {
		  return ChatColor.stripColor(parseColors(string));
	  }
}
