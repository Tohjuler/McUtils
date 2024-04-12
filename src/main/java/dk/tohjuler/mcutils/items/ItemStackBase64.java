package dk.tohjuler.mcutils.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemStackBase64 {
    /**
     * A method to serialize a single {@link ItemStack} to Base64 String.
     *
     * @param item to serialize
     * @return Base64 string of the item
     */
    public static String itemStackToBase64(ItemStack item) {
        return itemStacksToBase64(new ItemStack[] {item});
    }

    /**
     * A method to serialize an array of {@link ItemStack} to Base64 String.
     *
     * @param items to serialize
     * @return Base64 string of the items
     */
    public static String itemStacksToBase64(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (ItemStack item2 : items) {
                dataOutput.writeObject(item2);
            }

            // Serialize that array
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets a single ItemStack from Base64 string.
     *
     * @param data Base64 string of the item
     * @return ItemStack created from the Base64 string
     */
    public static ItemStack itemStackFromBase64(String data) {
        ItemStack[] items = itemStacksFromBase64(data);
        if (items == null) return null;
        return items[0];
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     *
     * @param data Base64 string of the items
     * @return Array of ItemStacks created from the Base64 string
     */
    public static ItemStack[] itemStacksFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
