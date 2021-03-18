package com.clusterfactions.clustercore.core.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;

public class PlayerAnimationEventListener implements Listener{

    @EventHandler
    public void PlayerAnimationEvent(PlayerAnimationEvent e) {
        Player p = e.getPlayer();

    }
 
}
