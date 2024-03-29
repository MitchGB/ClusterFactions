package com.clusterfactions.clustercore;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.clusterfactions.clustercore.core.combat.CombatManager;
import com.clusterfactions.clustercore.core.command.CommandManager;
import com.clusterfactions.clustercore.core.crate.CrateManager;
import com.clusterfactions.clustercore.core.factions.FactionsManager;
import com.clusterfactions.clustercore.core.factions.TeleportQueue;
import com.clusterfactions.clustercore.core.factions.claim.FactionClaimManager;
import com.clusterfactions.clustercore.core.factions.map.FactionMapGeneratorManager;
import com.clusterfactions.clustercore.core.fx.spectator.SpectatorManager;
import com.clusterfactions.clustercore.core.inventory.util.InventoryManager;
import com.clusterfactions.clustercore.core.items.ItemManager;
import com.clusterfactions.clustercore.core.items.block.CustomBlockManager;
import com.clusterfactions.clustercore.core.lang.LanguageManager;
import com.clusterfactions.clustercore.core.listeners.block.BlockBreakEventListener;
import com.clusterfactions.clustercore.core.listeners.block.BlockPlaceEventListener;
import com.clusterfactions.clustercore.core.listeners.entity.EntityDeathEventListener;
import com.clusterfactions.clustercore.core.listeners.entity.EntityInteractEventListener;
import com.clusterfactions.clustercore.core.listeners.events.updates.UpdateSecondEvent;
import com.clusterfactions.clustercore.core.listeners.events.updates.UpdateTickEvent;
import com.clusterfactions.clustercore.core.listeners.player.AsyncPlayerChatEventListener;
import com.clusterfactions.clustercore.core.listeners.player.PlayerAnimationEventListener;
import com.clusterfactions.clustercore.core.listeners.player.PlayerBlockInteractEventListener;
import com.clusterfactions.clustercore.core.listeners.player.PlayerDeathEventListener;
import com.clusterfactions.clustercore.core.listeners.player.PlayerInteractEventListener;
import com.clusterfactions.clustercore.core.listeners.player.PlayerJoinEventListener;
import com.clusterfactions.clustercore.core.listeners.player.PlayerMoveEventListener;
import com.clusterfactions.clustercore.core.listeners.player.PlayerQuitEventListener;
import com.clusterfactions.clustercore.core.listeners.player.PlayerResourcePackStatusEventListener;
import com.clusterfactions.clustercore.core.listeners.server.ServerListPingEventListener;
import com.clusterfactions.clustercore.core.permission.PlayerPermissionManager;
import com.clusterfactions.clustercore.core.player.PlayerManager;
import com.clusterfactions.clustercore.persistence.database.MongoHook;
import com.clusterfactions.clustercore.util.annotation.Manager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import lombok.Getter;

public class ClusterCore extends JavaPlugin{

	@Manager @Getter private MongoHook mongoHook;
	@Manager @Getter private InventoryManager inventoryManager;
	@Manager @Getter private CommandManager commandManager;
	@Manager @Getter private FactionsManager factionsManager;
	@Manager @Getter private PlayerManager playerManager;
	@Manager @Getter private LanguageManager languageManager;
	@Manager @Getter private PlayerPermissionManager playerPermissionManager;
	@Manager @Getter private FactionMapGeneratorManager factionMapGeneratorManager;
	@Manager @Getter private FactionClaimManager factionClaimManager;
	@Manager @Getter private CombatManager combatManager;
	@Manager @Getter private SpectatorManager spectatorManager;
	@Manager @Getter private CrateManager crateManager;
	
	@Manager @Getter private ItemManager itemManager;
	@Manager @Getter private CustomBlockManager customBlockManager;
	
	@Manager @Getter private TeleportQueue teleportQueue;
	
	@Getter private ProtocolManager protocolManager;
	
	
	private static ClusterCore instance;
	
	public static ClusterCore getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable(){
		instance = this;
		protocolManager = ProtocolLibrary.getProtocolManager();
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
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new UpdateTickEvent());
			}
			
		}.runTaskTimer(this, 0, 1);
	}
	private void setupListeners() {
		registerListener(
				new PlayerJoinEventListener(),
				new PlayerQuitEventListener(),
				new PlayerInteractEventListener(),
				new PlayerBlockInteractEventListener(),
				new PlayerDeathEventListener(),
				new PlayerMoveEventListener(),
				new PlayerResourcePackStatusEventListener(),
				new PlayerAnimationEventListener(),
				new AsyncPlayerChatEventListener(),
				
				new BlockBreakEventListener(),
				new BlockPlaceEventListener(),
				
				new EntityInteractEventListener(),
				new EntityDeathEventListener(),
				
				new ServerListPingEventListener()
				);
	}
	
	@Override
	public void onDisable() {
		getMongoHook().disable();
		System.out.println("[ClusterFactions] disabling...");
		for(World world :  Bukkit.getWorlds()) {
			for(Entity e : world.getEntities()) {
				if(e instanceof ArmorStand || e instanceof AreaEffectCloud)
					e.remove();
				}
		}
	}
	
	public void registerListener(Listener... listeners) {
		PluginManager manager = getServer().getPluginManager();
		for(Listener l : listeners) {
			manager.registerEvents(l, this);
		}
	}
	
	public void unregisterListener(Listener... listeners) {
		for(Listener l : listeners){
			HandlerList.unregisterAll(l);	
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
	
