package dk.tohjuler.mcutils.hooks;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class VaultHook {
    private static Economy economy;

    private static Permission permission;

    public static boolean init() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) return false;

        economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        permission = Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
        return (economy != null || permission != null);
    }

    public static boolean canAfford(OfflinePlayer offlinePlayer, double amount) {
        if (economy == null) throw new NullPointerException("Economy is null");
        return (economy.getBalance(offlinePlayer) >= amount);
    }

    public static double getBank(OfflinePlayer offlinePlayer) {
        if (economy == null) throw new NullPointerException("Economy is null");
        return economy.getBalance(offlinePlayer);
    }

    public static void remove(OfflinePlayer offlinePlayer, double amount) {
        if (economy == null) throw new NullPointerException("Economy is null");
        economy.withdrawPlayer(offlinePlayer, amount);
    }

    public static void add(OfflinePlayer offlinePlayer, double amount) {
        if (economy == null) throw new NullPointerException("Economy is null");
        economy.depositPlayer(offlinePlayer, amount);
    }

    public static String getPrimaryGroup(Player player) {
        if (permission == null) throw new NullPointerException("Permission is null");
        return permission.getPrimaryGroup(player);
    }

    public static String[] getPlayerGroups(Player player) {
        if (permission == null) throw new NullPointerException("Permission is null");
        return permission.getPlayerGroups(player);
    }
}
