package com.clusterfactions.clustercore.listeners.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.chat.ChatMessageMode;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

import net.md_5.bungee.api.ChatColor;

public class AsyncPlayerChatEventListener implements Listener{

	@EventHandler
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		e.setCancelled(true);				
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(e.getPlayer());
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction());
		if(data.getChatMode() != ChatMessageMode.GLOBAL && faction == null) data.setChatMode(ChatMessageMode.GLOBAL);
		switch(data.getChatMode())
		{
			case GLOBAL:
			{
				for(Player pl : Bukkit.getOnlinePlayers())
				{
					PlayerData recipient = ClusterCore.getInstance().getPlayerManager().getPlayerData(pl);
					Faction recipientFaction = ClusterCore.getInstance().getFactionsManager().getFaction(recipient.getFaction());
					
					String facColor = "";
					if(faction != null && recipientFaction != null) {
						if(recipientFaction.isAlly(faction))
							facColor = "&d";
						if(recipientFaction.isEnemy(faction))
							facColor = "&c";
						if(recipientFaction.isSame(faction))
							facColor = "&a";
					}
					
					String format = (data.getGroup() != null ? data.getGroup().getGroupPrefix() : " ") + " " + facColor + (faction == null ? "" : faction.getFactionTag() + " ") + "&f" +player.getName()	 + ChatColor.GRAY + " ";
					pl.sendMessage(ChatColor.translateAlternateColorCodes('&', format + e.getMessage()));
				}
			
				break;
			}
			case ALLY:
			{
				String format = CharRepo.ALLY_CHAT_TAG + " " + (data.getGroup() != null ? data.getGroup().getGroupPrefix() : " ")+ " " + player.getName()	 + ChatColor.GRAY + " ";
				faction.messageAll(ChatColor.translateAlternateColorCodes('&', format + e.getMessage()));
				if(faction.getAllies() != null)
				for(UUID ally : faction.getAllies())
				{
					Faction allyFaction = ClusterCore.getInstance().getFactionsManager().getFaction(ally);
					allyFaction.messageAll(ChatColor.translateAlternateColorCodes('&', format + e.getMessage()));
				}
				break;
			}
			case FACTION:
			{
				String format = CharRepo.FACTION_CHAT_TAG + " " + (data.getGroup() != null ? data.getGroup().getGroupPrefix() : " ") + " " + player.getName() + ChatColor.GRAY + " ";
				faction.messageAll(ChatColor.translateAlternateColorCodes('&', format + e.getMessage()));
				
				break;
			}
		}
	}
	
}
















