package dk.tohjuler.mcutils.hooks;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class VaultHook {
    private static Economy economy;
    private static Permission permission;
    private static Chat chat;

    public static boolean init() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) return false;

        economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        permission = Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
        chat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        return (economy != null || permission != null);
    }

    /**
     * Check if a player can afford a certain amount
     * <br/>
     *
     * @param offlinePlayer The player
     * @param amount        The amount
     * @return If the player can afford the amount
     * @since 1.0.0
     */
    public static boolean canAfford(OfflinePlayer offlinePlayer, double amount) {
        if (economy == null) throw new NullPointerException("Economy is null");
        return (economy.getBalance(offlinePlayer) >= amount);
    }

    /**
     * Get the bank of a player
     * <br/>
     *
     * @param offlinePlayer The player
     * @return The bank of the player
     * @since 1.0.0
     */
    public static double getBank(OfflinePlayer offlinePlayer) {
        if (economy == null) throw new NullPointerException("Economy is null");
        return economy.getBalance(offlinePlayer);
    }

    /**
     * Remove money from a player
     * <br/>
     *
     * @param offlinePlayer The player
     * @param amount        The amount
     * @since 1.0.0
     */
    public static void remove(OfflinePlayer offlinePlayer, double amount) {
        if (economy == null) throw new NullPointerException("Economy is null");
        economy.withdrawPlayer(offlinePlayer, amount);
    }

    /**
     * Add money to a player
     * <br/>
     *
     * @param offlinePlayer The player
     * @param amount        The amount
     * @since 1.0.0
     */
    public static void add(OfflinePlayer offlinePlayer, double amount) {
        if (economy == null) throw new NullPointerException("Economy is null");
        economy.depositPlayer(offlinePlayer, amount);
    }

    /**
     * Get the primary group of a player
     * <br/>
     *
     * @param player The player
     * @return The primary group of the player
     * @since 1.0.0
     */
    public static String getPrimaryGroup(Player player) {
        if (permission == null) throw new NullPointerException("Permission is null");
        return permission.getPrimaryGroup(player);
    }

    /**
     * Get the groups of a player
     * <br/>
     *
     * @param player The player
     * @return The groups of the player
     * @since 1.0.0
     */
    public static String[] getPlayerGroups(Player player) {
        if (permission == null) throw new NullPointerException("Permission is null");
        return permission.getPlayerGroups(player);
    }

    /**
     * Get the prefix of a player
     * <br/>
     *
     * @param player The player
     * @return The prefix of the player
     * @since 1.14.0
     */
    public static String getPrefix(Player player) {
        if (chat == null) throw new NullPointerException("Chat is null");
        return chat.getPlayerPrefix(player);
    }

    /**
     * Get the suffix of a player
     * <br/>
     *
     * @param player The player
     * @return The suffix of the player
     * @since 1.14.0
     */
    public static String getSuffix(Player player) {
        if (chat == null) throw new NullPointerException("Chat is null");
        return chat.getPlayerSuffix(player);
    }

    /**
     * Get the group prefix of a player
     * <br/>
     *
     * @param player The player
     * @return The group prefix of the player
     * @since 1.14.0
     */
    public static String getGroupPrefix(Player player) {
        if (chat == null) throw new NullPointerException("Chat is null");
        return chat.getGroupPrefix(player.getWorld(), getPrimaryGroup(player));
    }

    /**
     * Get the group suffix of a player
     * <br/>
     *
     * @param player The player
     * @return The group suffix of the player
     * @since 1.14.0
     */
    public static String getGroupSuffix(Player player) {
        if (chat == null) throw new NullPointerException("Chat is null");
        return chat.getGroupSuffix(player.getWorld(), getPrimaryGroup(player));
    }
}
