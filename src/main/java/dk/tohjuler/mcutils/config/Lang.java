package dk.tohjuler.mcutils.config;

import dk.tohjuler.mcutils.strings.ColorUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class Lang {
    @Getter
    private final ConfigurationFile langFile;
    private final ConfigurationFile defaultLang;

    /**
     * Load a lang file, create it if it doesn't exist
     * Check if all keys are present in the lang file
     *
     * @param plugin       The plugin which should own this file
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
     * Replace the placeholders from the config
     *
     * @param str the string to replace in
     * @return the replaced string
     */
    public String replaceVars(String str) {
        str = replaceFromCf(str, "vars");
        str = replaceFromCf(str, "placeholders");
        return str;
    }

    /**
     * Replace from a map from the config
     *
     * @param str the string replace in
     * @param key the key of the map
     * @return the replaced string
     */
    public String replaceFromCf(String str, String key) {
        if (!langFile.cf().contains(key)) return str;
        for (String cfKey : langFile.cf().getConfigurationSection(key).getKeys(false))
            str = str.replace(cfKey, langFile.cf().getString(cfKey));
        return str;
    }

    /**
     * Send a message to a CommandSender
     *
     * @param sender  The CommandSender to send the message to
     * @param key     The key to get
     * @param replace Map of placeholders to be replaced
     */
    public void send(CommandSender sender, String key, @NotNull Map<String, String> replace) {
        String str = get(key);
        for (Map.Entry<String, String> e : replace.entrySet())
            str = str.replace(e.getKey(), e.getValue());

        sender.sendMessage(
                replaceVars(str)
        );
    }

    /**
     * Send a message to a CommandSender
     *
     * @param sender The CommandSender to send the message to
     * @param key    The key to get
     */
    public void send(CommandSender sender, String key) {
        send(sender, key, Collections.emptyMap());
    }

}
