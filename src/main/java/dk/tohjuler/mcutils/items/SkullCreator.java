package dk.tohjuler.mcutils.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.UUID;

public class SkullCreator {

    /**
     * Create a player skull from a player uuid
     *
     * @param uuid the uuid of the player
     * @return the head of the player
     */
    public static ItemStack skullFromUuid(UUID uuid) {
        ItemStack itemStack = getPlayerSkullItem();
        return skullWithUuid(itemStack, uuid);
    }

    /**
     * Set the skull owner of the item
     *
     * @param item the item
     * @param uuid the uuid of the player
     * @return the item
     */
    public static ItemStack skullWithUuid(ItemStack item, UUID uuid) {

        SkullMeta meta = (SkullMeta)item.getItemMeta();
        meta.setOwner(Bukkit.getOfflinePlayer(uuid).getName());
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Create a player skull from an url
     * @param url the url of the player skin
     * @return the head of the player
     */
    public static ItemStack skullFromUrl(String url) {
        ItemStack itemStack = getPlayerSkullItem();
        return skullWithUrl(itemStack, url);
    }

    /**
     * Set the skull owner of the item from an url
     * @param itemStack the item
     * @param url the url of the player skin
     * @return the item
     */
    public static ItemStack skullWithUrl(ItemStack itemStack, String url) {
        notNull(itemStack, "item");
        notNull(url, "url");
        return skullWithBase64(itemStack, urlToBase64(url));
    }

    /**
     * Create a player skull from a base64 string
     *
     * @param base64 the base64 string
     * @return the head of the player
     */
    public static ItemStack skullFromBase64(String base64) {
        ItemStack itemStack = getPlayerSkullItem();
        return skullWithBase64(itemStack, base64);
    }

    /**
     * Set the skull owner of the item from a base64 string
     *
     * @param itemStack the item
     * @param base64 the base64 string
     * @return the item
     */
    public static ItemStack skullWithBase64(ItemStack itemStack, String base64) {
        notNull(itemStack, "item");
        notNull(base64, "base64");
        UUID uUID = new UUID(base64.hashCode(), base64.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(itemStack, "{SkullOwner:{Id:\"" + uUID + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
    }

    /**
     * Set the skull owner of the block
     *
     * @param block the block
     * @param uuid the uuid of the player
     */
    public static void blockWithUuid(Block block, UUID uuid) {
        notNull(block, "block");
        notNull(uuid, "id");
        setBlockType(block);
        ((Skull)block.getState()).setOwner(Bukkit.getOfflinePlayer(uuid).getName());
    }

    /**
     * Set the skull owner of the block from an url
     *
     * @param block the block
     * @param url the url of the player skin
     */
    public static void blockWithUrl(Block block, String url) {
        notNull(block, "block");
        notNull(url, "url");
        blockWithBase64(block, urlToBase64(url));
    }

    /**
     * Set the skull owner of the block from a base64 string
     *
     * @param block the block
     * @param base64 the base64 string
     */
    public static void blockWithBase64(Block block, String base64) {
        notNull(block, "block");
        notNull(base64, "base64");
        UUID uUID = new UUID(base64.hashCode(), base64.hashCode());
        String str = String.format("%d %d %d %s", Integer.valueOf(block.getX()),
                Integer.valueOf(block.getY()),
                Integer.valueOf(block.getZ()), "{Owner:{Id:\"" + uUID + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
    }

    private static ItemStack getPlayerSkullItem() {
        ItemStack item;
        try {
            item = new ItemStack(Material.valueOf("PLAYER_HEAD"), 1, (short) 3);
        } catch (Exception e) {
            item = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        return item;
    }

    private static void setBlockType(Block paramBlock) {
        try {
            paramBlock.setType(Material.valueOf("PLAYER_HEAD"), false);
        } catch (IllegalArgumentException illegalArgumentException) {
            paramBlock.setType(Material.valueOf("SKULL"), false);
        }
    }

    private static void notNull(Object paramObject, String paramString) {
        if (paramObject == null)
            throw new NullPointerException(paramString + " should not be null!");
    }

    private static String urlToBase64(String paramString) {
        URI uRI;
        try {
            uRI = new URI(paramString);
        } catch (URISyntaxException uRISyntaxException) {
            throw new RuntimeException(uRISyntaxException);
        }
        String str = "{\"textures\":{\"SKIN\":{\"url\":\"" + uRI + "\"}}}";
        return Base64.getEncoder().encodeToString(str.getBytes());
    }
}
