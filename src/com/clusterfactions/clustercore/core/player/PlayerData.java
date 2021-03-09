package com.clusterfactions.clustercore.core.player;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.chat.ChatMessageMode;
import com.clusterfactions.clustercore.core.factions.map.MapColour;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.listeners.events.updates.UpdateSecondEvent;
import com.clusterfactions.clustercore.core.permission.PermissionGroup;
import com.clusterfactions.clustercore.persistence.serialization.LocaleSerializer;
import com.clusterfactions.clustercore.persistence.serialization.MapColourSerializer;
import com.clusterfactions.clustercore.persistence.serialization.PermissionGroupSerializer;
import com.clusterfactions.clustercore.persistence.serialization.UUIDSerializer;
import com.clusterfactions.clustercore.persistence.serialization.VariableSerializer;
import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.NumberUtil;
import com.clusterfactions.clustercore.util.annotation.AlternateSerializable;
import com.clusterfactions.clustercore.util.annotation.DoNotSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class PlayerData{

	@DoNotSerialize public static final int maxPower = 10;
	@DoNotSerialize public static final int minPower = -10;
	@DoNotSerialize public static final int powerGainMillis = 1200000; // 20 minutes
	private long lastPower = 0;
	
	@Getter @Setter @AlternateSerializable(LocaleSerializer.class) private Locale locale = Locale.ENGLISH;
	
	@Getter @DoNotSerialize UUID playerUUID;
	
	@Getter @Setter private int power = 1;
	@Getter @Setter private boolean adminOverrideMode = false;
	
	@Getter @Setter @AlternateSerializable(PermissionGroupSerializer.class) private PermissionGroup group = PermissionGroup.MEMBER;
	@Getter @Setter @AlternateSerializable(UUIDSerializer.class) private UUID faction;
	
	@Getter @Setter @DoNotSerialize ChatMessageMode chatMode = ChatMessageMode.GLOBAL;
	
	@Getter @Setter @AlternateSerializable(MapColourSerializer.class) private MapColour mapEnemyColour = MapColour.RED;
	@Getter @Setter @AlternateSerializable(MapColourSerializer.class) private MapColour mapAllyColour = MapColour.GRAY_1;
	@Getter @Setter @AlternateSerializable(MapColourSerializer.class) private MapColour mapNeutralColour = MapColour.WHITE;
	@Getter @Setter @AlternateSerializable(MapColourSerializer.class) private MapColour mapEmptyColour = MapColour.TRANSPARENT;
	@Getter @Setter @AlternateSerializable(MapColourSerializer.class) private MapColour mapFactionColour = MapColour.LIGHT_GREEN;
	
	
	public void saveData() {
		ClusterCore.getInstance().getMongoHook().saveData(playerUUID.toString(), this, "players");
	}
	
	@SuppressWarnings("unchecked")
	public void saveData(String fieldName) {
		try {
			Object data = null;
			Field[] allFields = this.getClass().getDeclaredFields(); 
			for(Field field : allFields) {
				field.setAccessible(true);
				if(field.getName().equalsIgnoreCase(fieldName))
				{
					data = field.get(this);
					if(field.getAnnotation(AlternateSerializable.class) != null) 
						data = ((VariableSerializer<Object>)field.getAnnotation(AlternateSerializable.class).value().getDeclaredConstructor().newInstance()).serialize(field.get(this));
					break;
				}
			}
			if(data == null) return;
			ClusterCore.getInstance().getMongoHook().saveValue(this.playerUUID.toString(), fieldName, data, "players");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message, Object... args) {
		getPlayer().sendMessage(Colors.parseColors(String.format(message, args)));
	}
	
	public void sendMessage(Lang lang, Object... args) {
		getPlayer().sendMessage(Colors.parseColors(String.format(ClusterCore.getInstance().getLanguageManager().getString(locale, lang), args)));
	}
	
	/*
	 * Called each time player joins 
	 */
	public void init(Player player) {
		this.playerUUID = player.getUniqueId();
		saveData();
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(playerUUID);
	}
	
	public boolean isInFaction() {
		return faction != null;
	}
	
	public void secondUpdater(UpdateSecondEvent e) {
		powerGainTimer();
		setDisplayPrefix();
	}
	
	public boolean isCombatTagged() {
		return ClusterCore.getInstance().getCombatManager().isTagged(getPlayer());
	}
	
	private void powerGainTimer() {
		if(lastPower == 0)
		{
			if(power == 10)
			{
				lastPower = powerGainMillis;
				return;
			}
			power = NumberUtil.clamp(power + 1, -10, 10);
			lastPower = powerGainMillis;
			sendMessage(Lang.PLAYER_POWER_GAINED, 1);
			saveData("power");
			saveData("lastPower");
		}
		lastPower-= 1000;
	}
	
	private void setDisplayPrefix() {
		try {
			if(!Bukkit.getOnlinePlayers().contains(getPlayer())) return;
			Scoreboard scoreboard = ClusterCore.getInstance().getServer().getScoreboardManager().getMainScoreboard();
			Team team = scoreboard.getTeam(getPlayer().getName());
		
			if(team == null)
				team = scoreboard.registerNewTeam(getPlayer().getName());
        
			team.setPrefix(Colors.parseColors(getGroup().getGroupPrefix() != null ? getGroup().getGroupPrefix() + " ": ""));
			team.addEntry(getPlayer().getName());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
