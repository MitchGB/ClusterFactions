package com.clusterfactions.clustercore.core.inventory.impl.block;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.InventoryManager;
import com.clusterfactions.clustercore.core.inventory.util.model.BlockAsyncInventoryBase;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.FilteredSlots;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.InteractableSlots;
import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.ItemManager;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.SmeltableItem;
import com.clusterfactions.clustercore.core.listeners.events.updates.UpdateTickEvent;
import com.clusterfactions.clustercore.persistence.serialization.ItemStackSerializer;
import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.NumberUtil;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

import net.minecraft.server.v1_16_R3.TileEntityFurnace;

public class FurnaceInventory extends BlockAsyncInventoryBase implements InteractableSlots, FilteredSlots{

	final static int tickRate = 10;
	private int lastTick = 0;
	
	public static final Integer[] exclusionSlots = new Integer[] {2, 5, 6, 7, 8, 14, 15, 16, 17, 20, 23, 24 ,25 ,26};
	public static final Integer[] inventorySlots = new Integer[] {5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26};
	public static final Integer[] restrictedSlots = new Integer[] {2, 20};
	public static final Integer[] smeltableSlots = new Integer[] {2};
	public static final Integer[] fuelSlots = new Integer[] {20};
	
	private int currentProgress = 0;
	private int burnDuration = 0;
	private int maxBurnDuration = 0;
	private long nextDone = 0;
	
	private float storedExp = 0;
	
	private boolean bar_empty = false;
	
	public FurnaceInventory(Player player, Block block) {
		super(null, "FURNACE_OVERRIDE_MENU", "&f" + CharRepo.FURNACE_OVERRIDE_CONTAINER_27, 27, block);

	}
	
	@Override
	public void updateTickEvent(UpdateTickEvent e) {
		lastTick++;
		if(lastTick != tickRate) return;
		if(!ClusterCore.getInstance().getInventoryManager().blockCache.containsKey(this.block)) return;
		if(!(this.block.getState() instanceof TileState)) return;
		lastTick = 0;
		if(block.getRelative(BlockFace.DOWN).getType() == Material.HOPPER){

			Hopper hopper = (Hopper)(TileState)block.getRelative(BlockFace.DOWN).getState();
			ItemStack nextItem = InventoryManager.getNextItem(invInstance, inventorySlots);
			if(nextItem != null) {
				ItemStack addItem = nextItem.clone();
				addItem.setAmount(1);
			
				if(InventoryManager.canFitItem(hopper.getInventory(), nextItem, 0, 1, 2, 3)) {
					InventoryManager.addItemInto(hopper.getInventory(), nextItem, 0, 1, 2, 3);
					nextItem.add(-1);
				}
			}
		}
		
		for(BlockFace bf : new BlockFace[] {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,BlockFace.WEST}){
			if(block.getRelative(bf).getType() == Material.HOPPER){
				Hopper hopper = (Hopper)(TileState)block.getRelative(bf).getState();
				ItemStack nextItem = InventoryManager.getNextItem(hopper.getInventory(), 0, 1, 2, 3);

				if(((Directional)hopper.getBlockData()).getFacing() != bf.getOppositeFace()) continue;
				if(nextItem == null) continue;
				ItemStack addItem = nextItem.clone();
				addItem.setAmount(1);
				

				if(getSlotBelonging(nextItem) == null) continue;
				
				addItemInto(addItem, getSlotBelonging(nextItem).get(0));
				nextItem.add(-1);
			}
		}
		
		
		ItemStack smeltingItem = invInstance.getItem(2);
		ItemStack fuelItem = invInstance.getItem(20);
		SmeltableItem smeltable = null;		
		ItemManager manager = ClusterCore.getInstance().getItemManager();	
		
		if(smeltingItem != null && smeltingItem.getType() != Material.AIR){
			
			if(manager.getCustomItemHandler(manager.getCustomItemType(smeltingItem)) instanceof SmeltableItem)
					smeltable = (SmeltableItem) manager.getCustomItemHandler(manager.getCustomItemType(smeltingItem));
			
			int smeltTime = smeltable != null ? smeltable.smeltTime() : getSmeltTime(smeltingItem.getType());	
			float outputExp = smeltable != null ? smeltable.expOutput() : getSmeltExp(smeltingItem.getType());
			
			ItemStack output = smeltable != null ? smeltable.outputItem().getItem() : getFurnaceRecipe(smeltingItem.getType()).getResult();
			
			if(!canFitItem(output, inventorySlots) || burnDuration == 0){
				if(burnDuration == 0){
					if(fuelItem != null && fuelItem.getType() != Material.AIR){
						burnDuration = getVanillaBurnDuration(fuelItem);
						maxBurnDuration = burnDuration;
						if(fuelItem.getType() == Material.LAVA_BUCKET) {
							fuelItem.setType(Material.BUCKET);
							return;
						}
						fuelItem.add(-1);
						return;
					}
				}
				Furnace furn = (Furnace)((TileState) block.getState());
				furn.setBurnTime((short)0);
				furn.update();
				currentProgress = 0;
				nextDone = 0;
				return;
			}
			
			Furnace furn = (Furnace)((TileState) block.getState());
			furn.setBurnTime(Short.MAX_VALUE);
			furn.update();
			
			burnDuration -= tickRate;
			float timeDiff = (float)((System.currentTimeMillis()+smeltTime) - nextDone);
			
			currentProgress = NumberUtil.clamp(Math.round( timeDiff / (float)smeltTime*(float)22 ), 0, 23);
			if(nextDone == 0 || (currentProgress == 0 &&  timeDiff > smeltTime) || currentProgress > 22){
				if(nextDone != 0 && currentProgress > 22 && ((float)(System.currentTimeMillis()+smeltTime - nextDone) >= smeltTime)){
					smeltingItem.add(-1);
					storedExp += outputExp;
					addItemInto(output, inventorySlots);

					setPersistentData("contents", PersistentDataType.STRING, new ItemStackSerializer().serialize(invInstance.getContents()));
					setPersistentData("exp", PersistentDataType.FLOAT, storedExp);
				}
				nextDone = System.currentTimeMillis() + smeltTime;
				currentProgress = 0;
			}
		}else {
			currentProgress = 0;
			nextDone = 0;
		}
		
		if(currentProgress != 0 || !bar_empty)
		{
			int burnProgress = Math.round((float)burnDuration/(float)maxBurnDuration*14);
			renameWindow(invInstance, Colors.parseColors("&f" + CharRepo.FURNACE_OVERRIDE_CONTAINER_27 + getFuelProgressString(NumberUtil.clamp(burnProgress, 0, 14)) + getProgressString(NumberUtil.clamp(currentProgress, 0, 22)) ));
			bar_empty = currentProgress == 0;
			
		}
		
	}
	
	private String getProgressString(int progress) {
		return CharRepo.fromName("FURNACE_PROGRESS_ARROW_"+progress);
	}
	
	private String getFuelProgressString(int progress) {
		return CharRepo.fromName("FURNACE_PROGRESS_FUEL_"+progress);
	}

	@Override
	public void inventoryClickEvent(InventoryClickEvent e) {
		if(e.getAction() == InventoryAction.PICKUP_ALL || e.getAction() == InventoryAction.PICKUP_HALF 
				|| e.getAction() == InventoryAction.PICKUP_ONE || e.getAction() == InventoryAction.PICKUP_SOME) {
            ((ExperienceOrb)block.getWorld().spawn(block.getLocation(), ExperienceOrb.class)).setExperience(Math.round(this.storedExp));
			setPersistentData("exp", PersistentDataType.FLOAT, storedExp);
			this.storedExp = 0;
		}
	}
	
	@Override
	public List<Integer> getSlotBelonging(ItemStack item) {
		if(item == null) return null;
		boolean smeltable = isSmeltable(item.getType());
		CustomItemType customItemType = ClusterCore.getInstance().getItemManager().getCustomItemType(item);
		if(!smeltable && customItemType != null){
			CustomItem handler = ClusterCore.getInstance().getItemManager().getCustomItemHandler(customItemType);
			if(handler instanceof SmeltableItem) 
				smeltable = true;
		}
		if(smeltable) return Arrays.asList(smeltableSlots);
		
		if(item.getType().isFuel()) return Arrays.asList(fuelSlots);
		return null;
	}
	
	private boolean isSmeltable(Material mat) {
		return getFurnaceRecipe(mat) != null;
	}
	
	private int getSmeltTime(Material mat) {    
		return getFurnaceRecipe(mat) != null ? getFurnaceRecipe(mat).getCookingTime()/20*1000 : 0;
	}
	
	private float getSmeltExp(Material mat) {
		return getFurnaceRecipe(mat) != null ? getFurnaceRecipe(mat).getExperience() : 0;
	}
	
	private FurnaceRecipe getFurnaceRecipe(Material mat) {    
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		while (iter.hasNext()) {
			Recipe recipe = iter.next();
			if (!(recipe instanceof FurnaceRecipe)) continue;
			if (((FurnaceRecipe) recipe).getInput().getType() != mat) continue;
			return ((FurnaceRecipe) recipe);
		}
		return null;
	}
	
	public static int getVanillaBurnDuration(final ItemStack itemStack) {
		return TileEntityFurnace.f().get(CraftItemStack.asNMSCopy(itemStack).getItem());
	}

	@Override
	public List<Integer> filteredSlots() {
		return Arrays.asList(restrictedSlots);
	}

	@Override
	public boolean satisfiesFilter(int slot, ItemStack item) {
		if(slot != 2 && slot != 20) return true;
		if(item == null) return false;
		if(item.getType() == Material.AIR) return false;
		if(slot == 20 && item.getType().isFuel()) return false;
		boolean smeltable = isSmeltable(item.getType());
		CustomItemType customItemType = ClusterCore.getInstance().getItemManager().getCustomItemType(item);
		if(!smeltable && customItemType != null){
			CustomItem handler = ClusterCore.getInstance().getItemManager().getCustomItemHandler(customItemType);
			if(handler instanceof SmeltableItem) 
				smeltable = true;
		}
		return !smeltable;
	}

	@Override
	public List<Integer> interactableSlots() {
		return Arrays.asList(exclusionSlots);
	}

}





















