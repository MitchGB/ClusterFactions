package com.clusterfactions.clustercore.core.items.block.breakhandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.items.ItemManager;
import com.clusterfactions.clustercore.core.items.block.CustomBlockManager;
import com.clusterfactions.clustercore.core.items.block.CustomBlockType;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.IBlockAccess;
import net.minecraft.server.v1_16_R3.IBlockData;
import net.minecraft.server.v1_16_R3.MobEffects;
import net.minecraft.server.v1_16_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_16_R3.PacketPlayOutRemoveEntityEffect;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;

public class BlockBreakAnimationTask extends BukkitRunnable {

	private static Method BREAK_STATUS;
	private static Field LAST_DIG_TICK;

	static {
		try {
			BREAK_STATUS = PlayerInteractManager.class.getDeclaredMethod("a", IBlockData.class, BlockPosition.class,
					int.class);
			BREAK_STATUS.setAccessible(true);
			LAST_DIG_TICK = PlayerInteractManager.class.getDeclaredField("lastDigTick");
			LAST_DIG_TICK.setAccessible(true);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	private final Map<EntityPlayer, BlockAnimationContext> blockMap = new HashMap<>();

	ItemManager itemManager = ClusterCore.getInstance().getItemManager();
	CustomBlockManager blockManager = ClusterCore.getInstance().getCustomBlockManager();
	
	@Override
	public void run() {
		for (Entry<EntityPlayer, BlockAnimationContext> entry : blockMap.entrySet()) {
			EntityPlayer player = entry.getKey();
			BlockAnimationContext context = entry.getValue();
			IBlockAccess world = player.getWorld();

			IBlockData data = world.getType(context.getBlockPos());
			
			Location bukkitLoc = BlockBreakHandler.getLocation(entry.getValue().getBlockPos(), player.getBukkitEntity());
			ItemStack toolUsed = player.getBukkitEntity().getInventory().getItemInMainHand();
			CustomBlockType type = CustomBlockType.getType(bukkitLoc.getBlock());
			if(type == null) return;
			int breakSpeed = type.getAttributes().getBreakSpeed(toolUsed);

			context.setTicksTaken(context.getTicksTaken()+1);
			float f = ((float)context.getTicksTaken() / (float)breakSpeed) * (float)1;
			
			if (f >= 1.0f) {
				if(!ClusterCore.getInstance().getFactionClaimManager().canManipulateBlock(bukkitLoc, player.getBukkitEntity())) {
					f = 0;
					return;
				}
				player.playerInteractManager.a(context.getBlockPos(), PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK, "destroyed");
				
				bukkitLoc.getWorld().spawnParticle(Particle.BLOCK_CRACK, bukkitLoc, 10, 1, 1, 1, 1, type.getBlockBreakParticle().createBlockData());
				player.getBukkitEntity().playSound(bukkitLoc, Sound.BLOCK_STONE_BREAK, 10, 10);
				return;
			}

			int stage = (int) (f * 10.0f);
			if (stage != context.getStage()) {
				for(Player p : Bukkit.getOnlinePlayers())
				{
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockBreakAnimation(123, context.getBlockPos(), stage));
				}
				context.setStage(stage);
			}

		}
	}

	public void addEntry(EntityPlayer player, BlockPosition pos) {
		blockMap.put(player, new BlockAnimationContext(pos));
	}

	public void removeEntry(EntityPlayer player) {
		BlockAnimationContext context = blockMap.get(player);
		if (context != null) {
			blockMap.remove(player);
			player.playerConnection
					.sendPacket(new PacketPlayOutBlockBreakAnimation(123, context.getBlockPos(), -1));
			player.playerConnection
					.sendPacket(new PacketPlayOutRemoveEntityEffect(player.getId(), MobEffects.SLOWER_DIG));
		}
	}

}