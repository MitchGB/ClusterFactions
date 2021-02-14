package com.clusterfactions.clustercore;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.clusterfactions.clustercore.core.command.CommandManager;
import com.clusterfactions.clustercore.core.factions.FactionsManager;
import com.clusterfactions.clustercore.core.inventory.util.InventoryManager;
import com.clusterfactions.clustercore.core.lang.LanguageManager;
import com.clusterfactions.clustercore.core.permission.PlayerPermissionManager;
import com.clusterfactions.clustercore.core.player.PlayerManager;
import com.clusterfactions.clustercore.listeners.player.AsyncPlayerChatEventListener;
import com.clusterfactions.clustercore.listeners.player.PlayerJoinEventListener;
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
	
	private static ClusterCore instance;
	
	public static ClusterCore getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable(){
		instance = this;
		setupManagers();
		setupListeners();
	}
	
	private void setupListeners() {
		registerListener(
				new PlayerJoinEventListener(),
				new AsyncPlayerChatEventListener()
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
	
