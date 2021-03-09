package com.clusterfactions.clustercore.core.listeners.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.HexUtil;
import com.clusterfactions.clustercore.util.TextAlignment;

public class ServerListPingEventListener implements Listener{

	@SuppressWarnings("deprecation")
	@EventHandler
	public void ServerListPingEvent(ServerListPingEvent e) {
		//"------------------------------------------------------------"
		
		final String motdTop = " &8mc.&#34a4eb&lCluster Factions&8.com";
		String top = TextAlignment.centerText(75, Colors.parseColors(HexUtil.translateHexColorCodes("&#", "", motdTop)));
		final String motdBot = "&7Factions Reinvented&f [1.16.5] ";
		String bot = TextAlignment.centerText(64, Colors.parseColors(HexUtil.translateHexColorCodes("&#", "", motdBot)));
		
		e.setMotd(Colors.parseColors(HexUtil.translateHexColorCodes("&#", "", top + "\n" + bot)));
		e.setMaxPlayers(100);
	}
	
}