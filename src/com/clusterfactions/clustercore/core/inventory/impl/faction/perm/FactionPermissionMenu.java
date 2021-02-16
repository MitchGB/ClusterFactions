package com.clusterfactions.clustercore.core.inventory.impl.faction.perm;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.factions.Faction;
import com.clusterfactions.clustercore.core.factions.util.FactionPerm;
import com.clusterfactions.clustercore.core.factions.util.FactionRole;
import com.clusterfactions.clustercore.core.inventory.util.model.InventoryBase;
import com.clusterfactions.clustercore.core.player.PlayerData;
import com.clusterfactions.clustercore.util.ItemBuilder;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

public class FactionPermissionMenu extends InventoryBase{

	public FactionPermissionMenu(Player player) {

		super(player, "FACTION_PERM_MENU", "&f" + CharRepo.MENU_CONTAINER_54, 54);
		PlayerData data = ClusterCore.getInstance().getPlayerManager().getPlayerData(player);
		Faction faction = ClusterCore.getInstance().getFactionsManager().getFaction(data.getFaction());
		
		for(FactionPerm perm : FactionPerm.values())
		{
			this.addItem(getDisplayItem(perm, faction),
					e ->{faction.cyclePermWeight(perm); 
					ItemMeta meta = e.getItem().getItemMeta();
					List<String> desc = meta.getLore();
					desc.set(1, FactionRole.getRoleByWeight(faction.getPermWeight(perm)).toString());
					meta.setLore(desc);
					e.getItem().setItemMeta(meta); 
					e.getPlayer().updateInventory(); });
		}
	}
	
	private ItemStack getDisplayItem(FactionPerm perm, Faction faction) {
		ItemBuilder builder = new ItemBuilder(perm.getDisplayMat());
		builder.coloredName(perm.getName());
		builder.coloredLore(
				" ",
				FactionRole.getRoleByWeight(faction.getPermWeight(perm)).toString(),
				" ",
				perm.getDescription()
				);
		return builder.create();
	}
}
