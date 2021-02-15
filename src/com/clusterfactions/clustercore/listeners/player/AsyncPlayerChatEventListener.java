package com.clusterfactions.clustercore.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

import net.md_5.bungee.api.ChatColor;

public class AsyncPlayerChatEventListener implements Listener{

	@EventHandler
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		switch(data.getChatMode())
		{
			case GLOBAL:
			{
				if(data.getGroup() != null) 
					e.setFormat(data.getGroup().getGroupPrefix() + " " + player.getName() + ChatColor.GRAY + " %2$s");
			
				break;
			}
			case ALLY:
			{
				String format = CharRepo.ALLY_CHAT_TAG + " " + (data.getGroup() != null ? data.getGroup().getGroupPrefix() : " ")+ " " + player.getName()	 + ChatColor.GRAY + " ";
				
				e.setCancelled(true);
				break;
			}
			case FACTION:
			{
				String format = CharRepo.FACTION_CHAT_TAG + " " + (data.getGroup() != null ? data.getGroup().getGroupPrefix() : " ") + " " + player.getName() + ChatColor.GRAY + " ";

				PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(e.getPlayer());
				Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(playerData.getFaction().getFactionID());
				faction.messageAll(ChatColor.translateAlternateColorCodes('&', format + e.getMessage()));
				
				e.setCancelled(true);
				break;
			}
		}
	}
	
}
