package com.clusterfactions.clustercore.core.items.block.breakhandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumDirection;
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

	@Override
	public void run() {
		for (Entry<EntityPlayer, BlockAnimationContext> entry : blockMap.entrySet()) {
			EntityPlayer player = entry.getKey();
			BlockAnimationContext context = entry.getValue();
			IBlockAccess world = player.getWorld();

			IBlockData data = world.getType(context.getBlockPos());
			
			float f = 0;
			try {
				f = (float) BREAK_STATUS.invoke(player.playerInteractManager, data, context.getBlockPos(), LAST_DIG_TICK.get(player.playerInteractManager));
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}

			float durationMultiplier = 3;
			f -= durationMultiplier;
			
			if (f >= 1.0f) {
				player.playerInteractManager.a(context.getBlockPos(),
						PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK, "destroyed");
				return;
			}

			System.out.println(f);
			System.out.println((int) (f * 10.0f));
			int stage = (int) (f * 10.0f);

			if (stage != context.getStage()) {

				player.playerConnection
						.sendPacket(new PacketPlayOutBlockBreakAnimation(123, context.getBlockPos(), stage));
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