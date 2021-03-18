package com.clusterfactions.clustercore.core.crate.impl;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.crate.model.Crate;
import com.clusterfactions.clustercore.core.crate.model.Prize;
import com.clusterfactions.clustercore.core.crate.model.Prize_ItemStack;
import com.clusterfactions.clustercore.core.fx.spectator.cinematic.CinematicSequence;
import com.clusterfactions.clustercore.core.fx.spectator.cinematic.util.CinematicFrame;
import com.clusterfactions.clustercore.core.items.ItemRepo;
import com.clusterfactions.clustercore.core.listeners.events.player.PlayerBlockInteractEvent;
import com.clusterfactions.clustercore.util.ActionHandler;

public class VoteCrate extends Crate{

	public VoteCrate() {
		super("VOTE_CRATE", "&a&lVote Crate");
	}

	@Override
	public HashMap<Prize, Integer> drawSet() {
		HashMap<Prize, Integer> set = new HashMap<>();
		set.put(new Prize_ItemStack("TEST_w ag twa", new ItemStack(Material.BLACKSTONE), ""), 10);
		set.put(new Prize_ItemStack("TEST_w ag twa", ItemRepo.NICKEL_INGOT.getItem(), ""), 10);
		return set;
	}

	@Override
	public ActionHandler<PlayerBlockInteractEvent> interactHandler() {
		return e -> {
			Player player = e.getPlayer();
			Location startLoc = e.getBlock().getLocation().add(.5, 0, -5);
			Location blockLoc = e.getBlock().getLocation();
			

			Entity stand = ClusterCore.getInstance().getSpectatorManager().viewLoc(player, startLoc);
			
			CinematicFrame[] move1 = new CinematicFrame[] {
					new CinematicFrame(10, z -> z.playSound(blockLoc, Sound.BLOCK_ANVIL_LAND, 1, 1)),
					new CinematicFrame(10, z -> z.playSound(blockLoc, Sound.BLOCK_ANVIL_LAND, 1, 1)),
					new CinematicFrame(10, z -> z.playSound(blockLoc, Sound.BLOCK_ANVIL_LAND, 1, 1)),
					new CinematicFrame(10, z -> z.playSound(blockLoc, Sound.BLOCK_ANVIL_LAND, 1, 1)),
					new CinematicFrame(10, z -> z.playSound(blockLoc, Sound.BLOCK_ANVIL_LAND, 1, 1)),
					new CinematicFrame(10, z -> z.playSound(blockLoc, Sound.BLOCK_ANVIL_LAND, 1, 1)),
					new CinematicFrame(10, z -> z.playSound(blockLoc, Sound.BLOCK_ANVIL_LAND, 1, 1)),
					new CinematicFrame(10, z -> z.playSound(blockLoc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1)),
					new CinematicFrame(1, z -> blockLoc.getWorld().dropItem(blockLoc.add(.5, 1, .5), this.draw(player).displayItem()).setVelocity(new Vector(0,0,0)) ) ,
					new CinematicFrame(50, z -> {}) ,
			};
			new CinematicSequence(true, CinematicSequence.mergeArrays(move1)).execute(player);
			
		};
	}

}























