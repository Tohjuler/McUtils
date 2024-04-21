package dk.tohjuler.mcutils.config;

import dk.tohjuler.mcutils.strings.ColorUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
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
     * @since 1.0.0
     */
    public Lang(JavaPlugin plugin, String resourcePath) {
        // Yes, I know that it will still run the check even if the file is just created
        langFile = new ConfigurationFile(plugin, resourcePath);

        defaultLang = new ConfigurationFile(plugin.getResource(resourcePath));

        boolean needSave = false;
        for (String key : defaultLang.cf().getKeys(false))
            if (!langFile.cf().contains(key)) {
                langFile.cf().set(key, defaultLang.cf().get(key));
                needSave = true;
            }

        if (needSave) langFile.save();
    }

    /**
     * Reload the lang file
     *
     * @since 1.6.0
     */
    public void reload() {
        langFile.load();

        boolean needSave = false;
        for (String key : defaultLang.cf().getKeys(false))
            if (!langFile.cf().contains(key)) {
                langFile.cf().set(key, defaultLang.cf().get(key));
                needSave = true;
            }
        if (needSave) langFile.save();
    }

    /**
     * Get a string from the lang file
     * If the key doesn't exist, it will be created with the default value
     *
     * @param key The key to get
     * @param replace Map of placeholders to be replaced
     * @return The value of the key
     * @since 1.9.0
     */
    public String get(String key, Map<String, String> replace) {
        if (!langFile.cf().contains(key)) {
            langFile.cf().set(key, defaultLang.cf().get(key));
            langFile.save();
            Bukkit.getServer().getLogger().warning("Missing key in lang file, setting it with the default. Key: " + key);
        }

        String str = langFile.cf().getString(key);
        for (Map.Entry<String, String> e : replace.entrySet())
            str = str.replace(e.getKey(), e.getValue());

        return ColorUtils.colorize(
                replaceVars(
                        str
                )
        );
    }

    /**
     * Get a string from the lang file
     * Replace with string pairs. Ex: get("key", "placeholder1", "value1", "placeholder2", "value2")
     * <p>
     * @param key The key to get
     * @param replace The placeholders to replace
     * @return The value of the key
     * @since 1.0.0
     */
    public String get(String key, String... replace) {
        if (replace.length == 0) return get(key, Collections.emptyMap());

        if (replace.length % 2 != 0) throw new IllegalArgumentException("replace must be a multiple of 2");
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < replace.length; i += 2) map.put(replace[i], replace[i + 1]);
        return get(key, map);
    }

    /**
     * Replace the placeholders from the config
     *
     * @param str the string to replace in
     * @return the replaced string
     * @since 1.0.0
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
     * @since 1.0.0
     */
    public String replaceFromCf(String str, String key) {
        if (str == null) return str;
        if (!langFile.cf().isSet(key)) return str;
        for (String cfKey : langFile.cf().getConfigurationSection(key).getKeys(false))
            str = str.replace(cfKey, langFile.cf().getString(cfKey, "NULL"));
        return str;
    }

    /**
     * Send a message to a CommandSender
     *
     * @param sender  The CommandSender to send the message to
     * @param key     The key to get
     * @param replace Map of placeholders to be replaced
     * @since 1.0.0
     */
    public void send(CommandSender sender, String key, @NotNull Map<String, String> replace) {
        sender.sendMessage(
                get(key, replace)
        );
    }

    /**
     * Send a message to a CommandSender
     *
     * @param sender The CommandSender to send the message to
     * @param key    The key to get
     * @since 1.0.0
     */
    public void send(CommandSender sender, String key) {
        send(sender, key, Collections.emptyMap());
    }

    /**
     * Send a message to a CommandSender
     * Replace with string pairs. Ex: send(sender, "key", "placeholder1", "value1", "placeholder2", "value2")
     * <p>
     * @param sender The CommandSender to send the message to
     * @param key The key to get
     * @param replace The placeholders to replace
     * @since 1.9.0
     */
    public void send(CommandSender sender, String key, String... replace) {
        sender.sendMessage(
                get(key, replace)
        );
    }

}
