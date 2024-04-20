package dk.tohjuler.mcutils;

import dk.tohjuler.mcutils.chat.ChatInput;
import dk.tohjuler.mcutils.hooks.VaultHook;
import dk.tohjuler.mcutils.listeners.HeadDatabaseListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is used to initialize the utils.
 * ChatInput and VaultHook needs to be initialized.
 */
public class Init {
    /**
     * Initialize the utils.
     *
     * @param plugin The plugin to initialize the utils for
     * @return True if the initialization was successful, false otherwise
     */
    public static boolean init(JavaPlugin plugin) {
        if (!VaultHook.init()) return false;
        if (plugin.getServer().getPluginManager().getPlugin("HeadDatabase") != null)
            plugin.getServer().getPluginManager().registerEvents(new HeadDatabaseListener(), plugin);
        else plugin.getLogger().warning("HeadDatabase not found, HeadFonts will not work");
        plugin.getServer().getPluginManager().registerEvents(new ChatInput(plugin), plugin);
        return true;
    }
}
