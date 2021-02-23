package com.clusterfactions.clustercore.core.combat;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.listeners.events.player.PlayerCombatTagEvent;
import com.clusterfactions.clustercore.listeners.events.updates.UpdateSecondEvent;

public class CombatManager implements Listener{
	HashMap<UUID, Long> combatLog = new HashMap<>();
	final int combatTagTimer = 5000;
	
	public CombatManager(){
		ClusterCore.getInstance().registerListener(this);
	}
	
	public boolean isTagged(Player player) {
		return combatLog.containsKey(player.getUniqueId());
	}
	
	public boolean isTagged(PlayerData data) {
		return isTagged(data.getPlayer());
	}
	
	@EventHandler
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) return;
		if (e.getEntity() instanceof  Player) {
			Faction damagerFaction = ClusterCore.getInstance().getFactionsManager().getFaction((Player)e.getDamager());
			Faction playerFaction = ClusterCore.getInstance().getFactionsManager().getFaction((Player)e.getEntity());
			if(damagerFaction != null && playerFaction != null)
				if(damagerFaction.getFactionID().equals(playerFaction.getFactionID()) || damagerFaction.isAlly(playerFaction))
				{
					e.setCancelled(true);
					return;
				}
					
			Player p = (Player) e.getEntity();
			PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(p);
			if(!isTagged(p))
				data.sendMessage(Lang.PLAYER_BEEN_COMBAT_TAGGED, "5");
			combatLog.put(p.getUniqueId(), System.currentTimeMillis());
			Bukkit.getPluginManager().callEvent(new PlayerCombatTagEvent(p));
		}
	}
	
	@EventHandler
	public void UpdateSecondEvent(UpdateSecondEvent e) {
		if(!combatLog.isEmpty())
		for(Entry<UUID, Long> map : combatLog.entrySet())
		{
			if(System.currentTimeMillis() - map.getValue() >= combatTagTimer)
			{
				PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(map.getKey());
				data.sendMessage(Lang.PLAYER_COMBAT_TIMER_ENDED);
				combatLog.remove(map.getKey());
			}
		}
	}
	
}
