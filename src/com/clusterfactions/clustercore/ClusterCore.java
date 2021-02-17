package com.clusterfactions.clustercore;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.clusterfactions.clustercore.core.combat.CombatManager;
import com.clusterfactions.clustercore.core.command.CommandManager;
import com.clusterfactions.clustercore.core.factions.FactionsManager;
import com.clusterfactions.clustercore.core.factions.TeleportQueue;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.factions.map.FactionMapGeneratorManager;
import com.clusterfactions.clustercore.core.inventory.util.InventoryManager;
import com.clusterfactions.clustercore.core.lang.LanguageManager;
import com.clusterfactions.clustercore.core.permission.PlayerPermissionManager;
import com.clusterfactions.clustercore.core.player.PlayerManager;
import com.clusterfactions.clustercore.listeners.block.BlockBreakEventListener;
import com.clusterfactions.clustercore.listeners.block.BlockPlaceEventListener;
import com.clusterfactions.clustercore.listeners.entity.EntityInteractEventListener;
import com.clusterfactions.clustercore.listeners.events.updates.UpdateSecondEvent;
import com.clusterfactions.clustercore.listeners.player.AsyncPlayerChatEventListener;
import com.clusterfactions.clustercore.listeners.player.PlayerInteractEventListener;
import com.clusterfactions.clustercore.listeners.player.PlayerJoinEventListener;
import com.clusterfactions.clustercore.listeners.player.PlayerMoveEventListener;
import com.clusterfactions.clustercore.listeners.player.PlayerQuitEventListener;
import com.clusterfactions.clustercore.persistence.database.MongoHook;
import com.clusterfactions.clustercore.util.annotation.Manager;

import lombok.Getter;

public class ClusterCore extends JavaPlugin{
	
	@Manager @Getter private InventoryManager inventoryManager;
	@Manager @Getter private CommandManager commandManager;
	@Manager @Getter private FactionsManager factionsManager;
	@Manager @Getter private PlayerManager playerManager;
	@Manager @Getter private MongoHook mongoHook;
	@Manager @Getter private LanguageManager languageManager;
	@Manager @Getter private PlayerPermissionManager playerPermissionManager;
	@Manager @Getter private FactionMapGeneratorManager factionMapGeneratorManager;
	@Manager @Getter private FactionClaimManager factionClaimManager;
	@Manager @Getter private CombatManager combatManager;
	
	@Manager @Getter private TeleportQueue teleportQueue;
	
	private static ClusterCore instance;
	
	public static ClusterCore getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable(){
		instance = this;
		setupManagers();
		setupListeners();
		setupTimers();
	}
	
	private void setupTimers() {
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new UpdateSecondEvent());
			}
			
		}.runTaskTimer(this, 0, 20);
	}
	
	private void setupListeners() {
		registerListener(
				new PlayerJoinEventListener(),
				new PlayerQuitEventListener(),
				new PlayerInteractEventListener(),
				new AsyncPlayerChatEventListener(),
				new PlayerMoveEventListener(),
				
				new BlockBreakEventListener(),
				new BlockPlaceEventListener(),
				
				new EntityInteractEventListener()
				);
	}
	
	public void registerListener(Listener... listeners) {
		PluginManager manager = getServer().getPluginManager();
		for(Listener l : listeners) {
			manager.registerEvents(l, this);
		}
	}
	
    private void setupManagers(){
    	try {
    		for(Field field : this.getClass().getDeclaredFields()) {
    			field.setAccessible(true);

    			if(field.getAnnotation(Manager.class) == null) continue;
    			Constructor<?> constructor = field.getType().getDeclaredConstructor();
    			field.set(this, constructor.newInstance());
        	}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
	
