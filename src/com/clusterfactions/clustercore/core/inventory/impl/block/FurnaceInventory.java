package com.clusterfactions.clustercore.core.inventory.impl.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;

import com.clusterfactions.clustercore.ClusterCore;
import com.clusterfactions.clustercore.core.inventory.util.model.BlockAsyncInventory;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.FilteredSlots;
import com.clusterfactions.clustercore.core.inventory.util.model.interfaces.Interactable;
import com.clusterfactions.clustercore.core.items.CustomItemType;
import com.clusterfactions.clustercore.core.items.ItemManager;
import com.clusterfactions.clustercore.core.items.types.CustomItem;
import com.clusterfactions.clustercore.core.items.types.interfaces.SmeltableItem;
import com.clusterfactions.clustercore.util.Colors;
import com.clusterfactions.clustercore.util.NumberUtil;
import com.clusterfactions.clustercore.util.unicode.CharRepo;

import net.minecraft.server.v1_16_R3.TileEntityFurnace;

public class FurnaceInventory extends BlockAsyncInventory implements Interactable, FilteredSlots{

	final static int tickRate = 10;
	
	public static final Integer[] exclusionSlots = new Integer[] {2, 5, 6, 7, 8, 14, 15, 16, 17, 20, 23, 24 ,25 ,26};
	public static final Integer[] inventorySlots = new Integer[] {5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26};
	public static final Integer[] restrictedSlots = new Integer[] {2, 20};
	
	private int currentProgress = 0;
	private int burnDuration = 0;
	private int maxBurnDuration = 0;
	private long nextDone = 0;
	private boolean bar_empty = false;
	
	public FurnaceInventory(Player player, Block block) {
		super(player, "FURNACE_OVERRIDE_MENU", "&f" + CharRepo.FURNACE_OVERRIDE_CONTAINER_27, 27, block);
		new BukkitRunnable() {
			@Override
			public void run() {
				update();
			}
			
		}.runTaskTimer(ClusterCore.getInstance(), 0, tickRate);
	}
	
	public void update() {
		if(!ClusterCore.getInstance().getInventoryManager().blockCache.containsKey(this.block)) return;
		if(!(this.block.getState() instanceof TileState)) return;
		
		
		if(block.getRelative(BlockFace.DOWN).getType() == Material.HOPPER){
			ItemStack nextItem = getNextItem(getItemsFromSlot(invInstance, inventorySlots));
			if(nextItem != null){
				Hopper hopper = (Hopper)(TileState)block.getRelative(BlockFace.DOWN).getState();
				if(canFitItem(getItemsFromSlot(hopper.getInventory(), 0, 1, 2, 3 ,4), nextItem))
				{
					ItemStack addItem = nextItem.clone();
					addItem.setAmount(1);
					hopper.getInventory().addItem(addItem);
					nextItem.add(-1);
				}
			}
		}
		for(BlockFace bf : new BlockFace[] {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,BlockFace.WEST}){
			if(block.getRelative(bf).getType() == Material.HOPPER){
				Hopper hopper = (Hopper)(TileState)block.getRelative(bf).getState();
				ItemStack nextItem = getNextItem(getItemsFromSlot(hopper.getInventory(), 0, 1, 2, 3, 4));
				if(((Directional)hopper.getBlockData()).getFacing() != bf.getOppositeFace()) continue;
				if(nextItem == null) continue;
				if(getSlotBelonging(nextItem) == -1) continue;

				ItemStack addItem = nextItem.clone();
				addItem.setAmount(1);
				
				if(!canFitItem(getItemsFromSlot(invInstance, getSlotBelonging(nextItem)), addItem)) continue;
				addItemInto(addItem, getSlotBelonging(nextItem));
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
			ItemStack output = smeltable != null ? smeltable.outputItem().getItem() : getFurnaceRecipe(smeltingItem.getType()).getResult();
			
			if(!canFitItem(getItemsFromSlot(invInstance, inventorySlots), output ) || burnDuration == 0){
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
					addItemInto(output, inventorySlots);
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
			for(Player p : this.getHandlers()) {
				int burnProgress = Math.round((float)burnDuration/(float)maxBurnDuration*14);
				renameWindow(p, invInstance, Colors.parseColors("&f" + CharRepo.FURNACE_OVERRIDE_CONTAINER_27 + getFuelProgressString(NumberUtil.clamp(burnProgress, 0, 14)) + getProgressString(NumberUtil.clamp(currentProgress, 0, 22)) ));
				bar_empty = currentProgress == 0;
			}
		}
		
	}
	
	private ItemStack getNextItem(ItemStack[] items){
		for(ItemStack i : items) {
			if(i == null) continue;
			return i;
		}
		return null;
	}
	
	public boolean canFitItem(ItemStack[] items, ItemStack item) {
		for(ItemStack i : items) {
			if(i == null) return true;
			if(i.isSimilar(item))
				if(i.getAmount() < i.getMaxStackSize())
					return true;
		}
		return false;
	}
	
	public void addItemInto(ItemStack item, Integer... slots) {
		for(int i : slots) {
			ItemStack it = invInstance.getItem(i);
			if(it == null)
			{
				invInstance.setItem(i, item);
				return;
			}
			if(it.isSimilar(item))
				if(it.getAmount() < it.getMaxStackSize())
				{
					it.add(item.getAmount());
					return;
				}
		}
	}
	
	private String getProgressString(int progress) {
		return CharRepo.fromName("FURNACE_PROGRESS_ARROW_"+progress);
	}
	
	private String getFuelProgressString(int progress) {
		return CharRepo.fromName("FURNACE_PROGRESS_FUEL_"+progress);
	}
	
	@Override
	public List<Integer> excludeSlot() {
		return Arrays.asList(exclusionSlots);
	}

	@Override
	public boolean isRestricted(int slot, ItemStack item) {
		if(slot != 2 && slot != 20) return true;
		if(item == null) return false;
		
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
	public int getSlotBelonging(ItemStack item) {
		if(item == null) return -1;
		
		boolean smeltable = isSmeltable(item.getType());
		CustomItemType customItemType = ClusterCore.getInstance().getItemManager().getCustomItemType(item);
		if(!smeltable && customItemType != null){
			CustomItem handler = ClusterCore.getInstance().getItemManager().getCustomItemHandler(customItemType);
			if(handler instanceof SmeltableItem) 
				smeltable = true;
		}
		if(smeltable) return 2;
		
		if(item.getType().isFuel()) return 20;
		return -1;
	}
	
	@Override
	public List<Integer> restrictedSlots() {
		return Arrays.asList(restrictedSlots);
	}
	
	public ItemStack[] getItemsFromSlot(Inventory inv, Integer... slots) {
		List<ItemStack> items = new ArrayList<>();
		for(int i : slots) {
			items.add(inv.getItem(i));
		}
		return items.toArray(new ItemStack[items.size()]);
	}
	
	private boolean isSmeltable(Material mat) {
		return getFurnaceRecipe(mat) != null;
	}
	
	private int getSmeltTime(Material mat) {    
		return getFurnaceRecipe(mat) != null ? getFurnaceRecipe(mat).getCookingTime()/20*1000 : 0;
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
}





















