package com.clusterfactions.clustercore.persistence.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

//Base64 Encoder
public class ItemStackSerializer extends VariableSerializer<ItemStack[]>{

	@Override
	public String serialize(ItemStack[] obj) {    	
		try {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        
        dataOutput.writeInt(obj.length);
        
        for (int i = 0; i < obj.length; i++) {
            dataOutput.writeObject(obj[i]);
        }
        
        dataOutput.close();
        return Base64Coder.encodeLines(outputStream.toByteArray());
    } catch (Exception e) {
        throw new IllegalStateException("Unable to save item stacks.", e);
    }
	}

	@Override
	public ItemStack[] deserialize(String str) {
    	try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(str));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
    
            for (int i = 0; i < items.length; i++) {
            	items[i] = (ItemStack) dataInput.readObject();
            }
            
            dataInput.close();
            return items;
        } catch (Exception e) {
        	return new ItemStack[0];
        }
	}
    

}