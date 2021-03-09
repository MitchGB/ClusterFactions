package com.clusterfactions.clustercore.core.command.impl.admin;

import java.util.Locale;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.fx.spectator.SpectatorManager;
import com.clusterfactions.clustercore.core.fx.spectator.cinematic.CinematicSequence;
import com.clusterfactions.clustercore.core.fx.spectator.cinematic.util.CinematicFrame;
import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.ItemManager;
import com.clusterfactions.clustercore.core.lang.Lang;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.location.LocationUtil;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

@CommandAlias("admin|adminstrator|administration")
@CommandPermission("clustercore.admin")
public class AdminCommand extends BaseCommand{

	@HelpCommand
	public void help(final CommandSender sender) {
	}
	
	@Subcommand("save|savedata")
	public void save(final CommandSender sender, OnlinePlayer player) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
		playerData.saveData();
	}
	
	@Subcommand("override|overridemode")
	public void overrideMode(final CommandSender sender) {
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		playerData.setAdminOverrideMode(!playerData.isAdminOverrideMode());
		playerData.sendMessage("&7Admin override mode: &f" + (playerData.isAdminOverrideMode() ? "ENABLED" : "DISABLED") );
		playerData.saveData("adminOverrideMode");
	}
	
	@Subcommand("langtest")
	@CommandCompletion("@langs")
	public void langtest(final CommandSender sender, Lang lang){
		PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData((Player)sender);
		playerData.sendMessage(ClusterCore.getInstance().getLanguageManager().getString(Locale.ENGLISH, lang));
	}
	
	@Subcommand("fix-spectator")
	@CommandCompletion("@players")
	public void fixSpectator(final CommandSender sender, OnlinePlayer player){
		Player p = player.getPlayer();
		p.setGameMode(GameMode.SPECTATOR);
		p.setSpectatorTarget(null);
		p.setGameMode(GameMode.SURVIVAL);
		p.getInventory().setHelmet(null);
		ClusterCore.getInstance().getSpectatorManager().stopSpectatorMode(p);
		
	}
	
	@Subcommand("debug-armorstands")
	public void debugArmorStand(final CommandSender sender){
		if(!(sender instanceof Player)) return;
		for(Entity e : ((Player)sender).getWorld().getEntities()) {
			if(e instanceof ArmorStand)
				e.remove();
		}
	}
	
	@Subcommand("cinematic")
	@CommandCompletion("@players")
	public void runCinematic(final CommandSender sender, OnlinePlayer op) {
		Player player = op.getPlayer();
		Location startLoc = new Location(player.getWorld(), 133.5, 63, -77.5, 0, 0);
		Entity stand = ClusterCore.getInstance().getSpectatorManager().viewLoc(player, startLoc);
		
		CinematicFrame[] move1 = LocationUtil.lerp(startLoc, startLoc.clone().add(0, 0, 10), 100, stand);
		CinematicFrame move2 = new CinematicFrame(1, e -> stand.teleport(new Location(startLoc.getWorld(), 136.5, 66, -70.5, 25, 25)));
		CinematicFrame[] move3 = new CinematicFrame[] {
				new CinematicFrame(10, e -> player.sendBlockChange(new Location(startLoc.getWorld(), 134, 64, -65), Material.TNT.createBlockData())),
				new CinematicFrame(1, e -> player.playSound(new Location(startLoc.getWorld(), 134, 64, -65), Sound.BLOCK_GRASS_PLACE, 10, 10)),
				new CinematicFrame(10, e -> player.sendBlockChange(new Location(startLoc.getWorld(), 132, 66, -64), Material.TNT.createBlockData())),
				new CinematicFrame(1, e -> player.playSound(new Location(startLoc.getWorld(), 132, 66, -64), Sound.BLOCK_GRASS_PLACE, 10, 10)),

				new CinematicFrame(30, e -> player.playSound(new Location(startLoc.getWorld(), 132, 66, -64), Sound.ENTITY_TNT_PRIMED, 10, 10)),
				new CinematicFrame(60, e -> player.playSound(new Location(startLoc.getWorld(), 132, 66, -64), Sound.ENTITY_GENERIC_EXPLODE, 10, 10)),
				new CinematicFrame(1, e -> player.spawnParticle(Particle.EXPLOSION_NORMAL, new Location(startLoc.getWorld(), 132, 66, -64), 10)),
				new CinematicFrame(1, e -> SpectatorManager.changeAllBlocks(player, Material.AIR.createBlockData(), new Location(startLoc.getWorld(), 134, 64, -65), new Location(startLoc.getWorld(), 132, 65, -64), new Location(startLoc.getWorld(), 132, 66, -64), new Location(startLoc.getWorld(), 133, 64, -65), new Location(startLoc.getWorld(), 132, 64, -65), 
						new Location(startLoc.getWorld(), 134, 64, -64), new Location(startLoc.getWorld(), 134, 65, -64), new Location(startLoc.getWorld(), 133, 65, -64), new Location(startLoc.getWorld(), 133, 66, -64), new Location(startLoc.getWorld(), 131, 64, -64))),
				new CinematicFrame(60, e ->{}),
				
		};
		new CinematicSequence(true, CinematicSequence.mergeArrays(move1, move2.singleArray(), move3)).execute(player);
	}
	@Subcommand("item")
	public class item extends BaseCommand{
		
		private void giveItem(Player player, CustomItemType type, int amount){
			ItemManager itemManager = ClusterCore.getInstance().getItemManager();
			player.getInventory().addItem(itemManager.getCustomItemHandler(type).getNewStack(amount));
		}
		
		@Subcommand("give")
		@CommandCompletion("@players @custom-items")
		public void give(final CommandSender sender, OnlinePlayer player, CustomItemType type, @Default("1") int amount) {
			giveItem(player.getPlayer(), type, amount);
		}
		
		@Subcommand("give")
		@CommandCompletion("@cluster-perm-groups")
		public void give(final CommandSender sender, CustomItemType type, @Default("1") int amount) {
			giveItem((Player)sender, type, amount);
		}
	}
	
	@Subcommand("power")
	public class power extends BaseCommand{
		
		@Subcommand("give")
		@CommandCompletion("@players")
		public void give(final CommandSender sender, OnlinePlayer player, int amount) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
			playerData.setPower(playerData.getPower() + amount);
			playerData.saveData("power");
		}
		
		@Subcommand("remove")
		@CommandCompletion("@players")
		public void remove(final CommandSender sender, OnlinePlayer player, int amount) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
			playerData.setPower(playerData.getPower() - amount);
			playerData.saveData("power");
		}
		
		@Subcommand("set")
		@CommandCompletion("@players")
		public void set(final CommandSender sender, OnlinePlayer player, int amount) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
			playerData.setPower(amount);
			playerData.saveData("power");
		}
		
		@Subcommand("reset")
		@CommandCompletion("@players")
		public void reset(final CommandSender sender, OnlinePlayer player) {
			PlayerData playerData = ClusterCore.getInstance().getPlayerManager().getPlayerData(player.getPlayer());
			playerData.setPower(0);
			playerData.saveData("power");
		}
		
	}
}
	
	
	
	

















