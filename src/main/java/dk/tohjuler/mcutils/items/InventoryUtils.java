package dk.tohjuler.mcutils.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

/**
 * Utility class for inventory related operations.
 */
public class InventoryUtils {
    /**
     * Get the amount of empty slots in a player's inventory.
     * <br>
     *
     * @param p Player to check
     * @return The amount of empty slots in the player's inventory
     */
    public static int getEmptySlots(Player p) {
        int count = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                count++;
            }
        }
        return count;
    }

    /**
     * Removes the items of type from an inventory.
     *
     * @param inventory Inventory to modify
     * @param type      The type of Material to remove
     * @param amount    The amount to remove, or {@link Integer#MAX_VALUE} to remove all
     * @return The amount of items that could not be removed, 0 for success, or -1 for failures
     */
    public static int removeItems(Inventory inventory, Material type, int amount) {

        if (type == null || inventory == null)
            return -1;
        if (amount <= 0)
            return -1;

        if (amount == Integer.MAX_VALUE) {
            inventory.remove(type);
            return 0;
        }

        HashMap<Integer, ItemStack> retVal = inventory.removeItem(new ItemStack(type, amount));

        int notRemoved = 0;
        for (ItemStack item : retVal.values()) {
            notRemoved += item.getAmount();
        }
        return notRemoved;
    }

    /**
     * Removes a certain amount of the specified itemStack from an inventory.
     * <br>
     *
     * @param inventory Inventory to remove from
     * @param item      The item to remove
     * @param amount    The amount to remove, or {@link Integer#MAX_VALUE} to remove all
     * @return The amount of items that could not be removed, 0 for success, or -1 for failures
     * @since 1.23.0
     */
    public static int removeItems(Inventory inventory, ItemStack item, int amount) {
        if (item == null || inventory == null)
            return -1;
        if (amount <= 0)
            return -1;

        item.setAmount(amount);

        if (amount == Integer.MAX_VALUE) {
            inventory.remove(item);
            return 0;
        }

        HashMap<Integer, ItemStack> retVal = inventory.removeItem(item);

        int notRemoved = 0;
        for (ItemStack i : retVal.values()) {
            notRemoved += i.getAmount();
        }
        return notRemoved;
    }

    /**
     * Get the amount of items of a certain type in an inventory.
     * <br>
     *
     * @param inventory Inventory to check
     * @param type      The type of Material to check for
     * @return The amount of items of the type in the inventory
     */
    public static int getAmountOf(Inventory inventory, Material type) {
        int amount = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == type) {
                amount += item.getAmount();
            }
        }
        return amount;
    }

    /**
     * Save a player's inventory to a string.
     * Format: [inventory]:[armor] in base64 from {@link ItemStackBase64}.
     * <br>
     *
     * @param inventory PlayerInventory to save
     * @return The inventory as a string
     */
    public static String playerInventoryToString(PlayerInventory inventory) {
        String inv = ItemStackBase64.itemStacksToBase64(inventory.getContents());
        String armor = ItemStackBase64.itemStacksToBase64(inventory.getArmorContents());

        return inv + ":" + armor;
    }

    /**
     * Restore a player's inventory from a string.
     * Format: [inventory]:[armor] in base64 from {@link ItemStackBase64}.
     * <br>
     *
     * @param inventory PlayerInventory to restore
     * @param data      The data to restore from
     */
    public static void restorePlayerInventory(PlayerInventory inventory, String data) {
        String[] parts = data.split(":");
        ItemStack[] items = ItemStackBase64.itemStacksFromBase64(parts[0]);
        ItemStack[] armor = ItemStackBase64.itemStacksFromBase64(parts[2]);

        inventory.setContents(items);
        inventory.setArmorContents(armor);
    }
}
