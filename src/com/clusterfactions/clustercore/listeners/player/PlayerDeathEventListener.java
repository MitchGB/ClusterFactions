package com.clusterfactions.clustercore.listeners.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.NumberUtil;

public class PlayerDeathEventListener implements Listener{
	
	public final static int powerLostOnDeath = 4;
	
	@EventHandler
	public void PlayerDeathEvent(PlayerDeathEvent e) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(e.getEntity());
		playerData.setPower(NumberUtil.clamp(playerData.getPower() - powerLostOnDeath, PlayerData.minPower, PlayerData.maxPower));
		playerData.sendMessage(Lang.PLAYER_DIED_LOST_POWER, powerLostOnDeath);
	}
}