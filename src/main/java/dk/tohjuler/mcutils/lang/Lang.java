package dk.tohjuler.mcutils.lang;

import dk.tohjuler.mcutils.other.ColorUtils;
import dk.tohjuler.mcutils.other.ConfigurationFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Lang {
    @Getter
    private final ConfigurationFile langFile;
    private final ConfigurationFile defaultLang;

    /**
     * Load a lang file, create it if it doesn't exist
     * Check if all keys are present in the lang file
     *
     * @param plugin The plugin which should own this file
     * @param resourcePath The path to the file
     */
    public Lang(JavaPlugin plugin, String resourcePath) {
        // Yes, I know that it will still run the check even if the file is just created
        langFile = new ConfigurationFile(plugin, resourcePath);

        defaultLang = new ConfigurationFile(plugin.getResource(resourcePath));

        for (String key : defaultLang.cf().getKeys(false))
            if (!langFile.cf().contains(key)) {
                langFile.cf().set(key, defaultLang.cf().get(key));
            }

        langFile.save();
    }

    /**
     * Get a string from the lang file
     * If the key doesn't exist, it will be created with the default value
     *
     * @param key The key to get
     * @return The value of the key
     */
    public String get(String key) {
        if (!langFile.cf().contains(key)) {
            langFile.cf().set(key, defaultLang.cf().get(key));
            langFile.save();
            Bukkit.getServer().getLogger().warning("Missing key in lang file, setting it with the default. Key: " + key);
        }
        return ColorUtils.colorize(langFile.cf().getString(key));
    }

    /**
     * Send a message to a CommandSender
     *
     * @param sender The CommandSender to send the message to
     * @param key The key to get
     */
    public void send(CommandSender sender, String key) {
        sender.sendMessage(get(key));
    }
}
