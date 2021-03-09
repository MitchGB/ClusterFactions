package com.clusterfactions.clustercore.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import net.md_5.bungee.api.ChatColor;

public class TextAlignment {


	public static int getStringLength(String s) {
		String colours = "[&][0-9a-fA-Fk-oK-OrR]";
		String st = s.replaceAll(colours, "");
		return st.length();
	}
	
	public static String padText(int length, String obj1, String obj2)
	{
		int obj1Length = getStringLength(obj1);
		int obj2Length = getStringLength(obj2);
		int leftover = length - obj1Length - obj2Length;
		int sect = (int)leftover/3;
		StringBuilder sb = new StringBuilder();
		String space = Strings.repeat(" ", sect);
		
		sb.append(space + obj1 + space + obj2 + space);
		
		return sb.toString();
	}
	
	public static String padText(String title, String obj1, String obj2) {
		return padText(getStringLength(title), obj1, obj2);
	}
	
	public static String centerText(int length, String obj1, String centerObj, String obj2) {
		int obj1Length = getStringLength(obj1);
		int obj2Length = getStringLength(obj2);
		int leftover = length - obj1Length - obj2Length;
		int sect = (int)leftover/3;
		StringBuilder sb = new StringBuilder();
		String space = "";
		String spacer = "";
		if(sect > 0)
		for(int i = 0; i < sect; i++) 
			space += " ";
		sb.append(space);
		sb.append(obj1);
		
		sect -= getStringLength(centerObj);
		sect = NumberUtil.clampMin(sect, 1);
		for(int i = 0; i < sect; i++) 
			spacer += " ";
		sb.append(spacer);
		sb.append(centerObj);
		sb.append(spacer);
		
		sb.append(obj2);
		sb.append(space);
		return sb.toString();
	}
	
	public static String centerText(String title, String obj1, String centerObj, String obj2) {
		return centerText(getStringLength(title), obj1, centerObj, obj2);
	}
	
	public static String centerText(String title, String obj)
	{
		return centerText(getStringLength(title), obj);
	}
	
	public static String centerText(int length, String obj) {
		StringBuilder sb = new StringBuilder();
		length -= getStringLength(obj);
		for(int j = 0; j < 2; j++) {
			for(int i = 0; i < (int)length/2; i++)
			{
				sb.append(" ");
			}
			if(!sb.toString().contains(obj))
				sb.append(obj);
		}
		return sb.toString();
	}
	
	public static List<String> truncateText(String title, String text) {
		return truncateText(getStringLength(title), text);
	}
	
	public static List<String> truncateText(int length, String text) {
		String[] word = text.split(" ");
		List<String> conv = new ArrayList<String>();
		for(int i = 0; i < word.length; i++)
		{
			if(i == 0) 
			{
				conv.add(word[i]);
				continue;
			}
			conv.add(ChatColor.GRAY + word[i]);
			
		}
			
		List<String> ret = new ArrayList<String>();
		String add = "";
		for(String s : conv) {
			if(getStringLength(add) + s.length() > length) {
				ret.add(add);
				add = "";
			}
			add += s + " ";
			
		}
		ret.add(add);
		return ret;
	}
	
	
	public static String getLastColors(String input) {
		  String result = "";
		  int length = input.length();
		  for (int index = length - 1; index > -1; index--) {
		    char section = input.charAt(index);
		    if (section == ChatColor.COLOR_CHAR && index < length - 1) {
		      char c = input.charAt(index + 1);
		      ChatColor color = ChatColor.getByChar(c);
		      if (color != null) {
		        result = color.toString() + result;
		        if (color.equals(ChatColor.RESET)) {
		          break;
		        }
		      }
		    }
		  }
		  return result;
		}
}
