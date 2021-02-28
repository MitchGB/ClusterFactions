package com.clusterfactions.clustercore.core.items.block.breakhandler;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.clusterfactions.clustercore.ClusterCore;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MobEffect;
import net.minecraft.server.v1_16_R3.MobEffects;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEffect;

public class BlockBreakHandler {

	private BlockBreakAnimationTask task;

	public BlockBreakHandler() {
		registerProtocolListener();
		
		task = new BlockBreakAnimationTask();
		task.runTaskTimer(ClusterCore.getInstance(), 1, 1);
		
	}
	
	private void registerProtocolListener() {
		ClusterCore.getInstance().getProtocolManager().addPacketListener(new PacketAdapter(ClusterCore.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {

			@Override
			public void onPacketReceiving(PacketEvent event) {
				PacketContainer container = event.getPacket();
				PlayerDigType type = container.getPlayerDigTypes().read(0);
				com.comphenix.protocol.wrappers.BlockPosition packetPos = container.getBlockPositionModifier().read(0);
				BlockPosition pos = new BlockPosition(packetPos.getX(), packetPos.getY(), packetPos.getZ());
				EntityPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();

				if (type == PlayerDigType.START_DESTROY_BLOCK && getLocation(pos, player.getBukkitEntity()).getBlock().getType() == Material.NOTE_BLOCK ) {
					if(player.getBukkitEntity().getGameMode() == GameMode.CREATIVE) return;
					task.addEntry(player, pos);
					player.playerConnection.sendPacket(new PacketPlayOutEntityEffect(player.getId(),
							new MobEffect(MobEffects.SLOWER_DIG, Integer.MAX_VALUE, -1, true, false)));
				} else if (type == PlayerDigType.ABORT_DESTROY_BLOCK || type == PlayerDigType.STOP_DESTROY_BLOCK) {
					task.removeEntry(player);
					for(Player p : Bukkit.getOnlinePlayers())
					{
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockBreakAnimation(123, pos, -1));
					}
				}
			}

		});
	}
	
	public static Location getLocation(BlockPosition pos, Player player) {
		return new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ());
	}
}




























