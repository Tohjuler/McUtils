package dk.tohjuler.mcutils;

import dk.tohjuler.mcutils.chat.ChatInput;
import dk.tohjuler.mcutils.hooks.VaultHook;
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
        plugin.getServer().getPluginManager().registerEvents(new ChatInput(plugin), plugin);
        return true;
    }
}
