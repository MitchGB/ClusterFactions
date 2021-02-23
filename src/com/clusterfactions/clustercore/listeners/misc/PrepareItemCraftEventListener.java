package com.clusterfactions.clustercore.listeners.misc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import com.clusterfactions.clustercore.ClusterCore;

public class PrepareItemCraftEventListener implements Listener{
	
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        int xIndex = 0;
        int zIndex = 0;
        ItemStack[][] map = new ItemStack[3][3];
        
    	for (ItemStack itemStack : e.getInventory().getMatrix()) {
            if(xIndex == 3) {
            	xIndex = 0;
            	zIndex++;
            }
            map[zIndex][xIndex] = itemStack;
            xIndex++;
        }
    	if(ClusterCore.getInstance().getItemManager().isMaterialsApplicable(map)) {
    		if(!ClusterCore.getInstance().getItemManager().isRecipeApplicable(map))
    			e.getInventory().setResult(null);
    	}
    	
    }
}
 