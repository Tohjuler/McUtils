package dk.tohjuler.mcutils.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryUtils {
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
     * @param inventory Inventory to modify
     * @param type The type of Material to remove
     * @param amount The amount to remove, or {@link Integer#MAX_VALUE} to remove all
     * @return The amount of items that could not be removed, 0 for success, or -1 for failures
     */
    public static int removeItems(Inventory inventory, Material type, int amount) {

        if(type == null || inventory == null)
            return -1;
        if (amount <= 0)
            return -1;

        if (amount == Integer.MAX_VALUE) {
            inventory.remove(type);
            return 0;
        }

        HashMap<Integer,ItemStack> retVal = inventory.removeItem(new ItemStack(type,amount));

        int notRemoved = 0;
        for(ItemStack item: retVal.values()) {
            notRemoved+=item.getAmount();
        }
        return notRemoved;
    }

    public static int getAmountOf(Inventory inventory, Material type) {
        int amount = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == type) {
                amount += item.getAmount();
            }
        }
        return amount;
    }
}
