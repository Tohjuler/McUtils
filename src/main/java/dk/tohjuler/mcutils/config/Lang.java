package dk.tohjuler.mcutils.config;

import com.cryptomorin.xseries.XSound;
import dk.tohjuler.mcutils.strings.ColorUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Lang {
    @Getter
    private final ConfigurationFile langFile;
    private final ConfigurationFile defaultLang;

    // Sounds
    private final Map<String, Sound> sounds = new HashMap<>();
    private String defaultSound = null;

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

        reload(false);
    }

    /**
     * Reload the lang file
     *
     * @since 1.6.0
     */
    public void reload() {
        reload(true);
    }

    /**
     * Reload the lang file
     * <br/>
     *
     * @param reloadFile If the file should be reloaded
     * @since 1.19.0
     */
    public void reload(boolean reloadFile) {
        if (reloadFile)
            langFile.load();

        // Load sounds

        sounds.clear();
        if (langFile.cf().isSet("sounds"))
            for (String key : langFile.cf().getConfigurationSection("sounds").getKeys(false))
                sounds.put(key, new Sound(langFile, "sounds." + key));
        if (langFile.cf().isSet("defaultSound"))
            defaultSound = langFile.cf().getString("defaultSound");

        // Check if all keys are present

        boolean needSave = false;
        for (String key : defaultLang.cf().getKeys(false))
            if (!langFile.cf().contains(key)) {
                langFile.cf().set(key, defaultLang.cf().get(key));
                needSave = true;
            }
        if (needSave) langFile.save();
    }

    /**
     * Check if a key is present in the lang file
     * This will only check the current file, not the default.
     * <br/>
     *
     * @param key The key to check
     * @return If the key is present
     */
    public boolean isPresent(String key) {
        return langFile.cf().contains(key);
    }

    /**
     * Get a string from the lang file
     * If the key doesn't exist, it will be created with the default value
     *
     * @param key     The key to get
     * @param replace Map of placeholders to be replaced
     * @return The value of the key
     * @since 1.9.0
     */
    public String get(String key, Map<String, String> replace) {
        if (!langFile.cf().contains(key)) {
            if (!defaultLang.cf().contains(key)) {
                Bukkit.getServer().getLogger().warning("Missing key in lang file and default lang file. Key: " + key);
                return "NULL";
            }

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
     * <br/>
     *
     * @param key     The key to get
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
            str = str.replace(cfKey, langFile.cf().getString(key + "." + cfKey, "NULL"));
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
        if (sender instanceof Player) playSoundForKey(key, (Player) sender);
        String msg = get(key, replace);
        for (String s : msg.split("\n\\|%nl%"))
            sender.sendMessage(s);
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
     * <br/>
     *
     * @param sender  The CommandSender to send the message to
     * @param key     The key to get
     * @param replace The placeholders to replace
     * @since 1.9.0
     */
    public void send(CommandSender sender, String key, String... replace) {
        if (sender instanceof Player) playSoundForKey(key, (Player) sender);
        String msg = get(key, replace);
        for (String s : msg.split("\n\\|%nl%"))
            sender.sendMessage(s);
    }

    // Sounds
    // ---

    /**
     * Play the set sound if the key has a sound set, or the default sound if it exists.
     * The sound order:
     * - Key sound
     * - Sound applyIf
     * - Default sound
     * If the key sound is null, then it will stop there.
     * <br/>
     *
     * @param key     The key to get
     * @param players The players to play the sound for
     * @since 1.19.0
     */
    public void playSoundForKey(String key, Player... players) {
        if (langFile.cf().isSet(key + ".sound")) {
            if (langFile.cf().get(key + ".sound") == null) return;
            parseSoundFromCf(key + ".sound").play(players);
            return;
        }

        for (Sound sound : sounds.values())
            if (sound.applyTo(key)) {
                sound.play(players);
                return;
            }

        if (defaultSound != null)
            parseSound(defaultSound).play(players);
    }

    /**
     * Get a sound from the lang file.
     * <br/>
     *
     * @param id The key to get
     * @return The sound
     * @since 1.19.0
     */
    public @Nullable Sound getSound(String id) {
        return sounds.get(id);
    }

    private Sound parseSound(String sound) {
        if (sounds.containsKey(sound))
            return sounds.get(sound);

        return new Sound(sound);
    }

    private Sound parseSoundFromCf(String key) {
        if (langFile.cf().isString(key)) return parseSound(langFile.cf().getString(key));

        return new Sound(langFile, key);
    }

    public static class Sound {
        private final XSound.Record sound;
        private final String applyIf;

        public Sound(ConfigurationFile cf, String basePath) {
            if (cf.cf().isString(basePath)) {
                sound = XSound.matchXSound(cf.cf().getString(basePath)).orElse(XSound.BLOCK_STONE_BREAK).record();
                applyIf = null;
                return;
            }

            sound = XSound.matchXSound(cf.cf().getString(basePath + ".sound")).orElse(XSound.BLOCK_STONE_BREAK).record();
            sound.withVolume((float) cf.cf().getDouble(basePath + ".volume"));
            sound.withPitch((float) cf.cf().getDouble(basePath + ".pitch"));
            applyIf = cf.cf().getString(basePath + ".applyIf");
        }

        public Sound(String sound) {
            this.sound = XSound.matchXSound(sound).orElse(XSound.BLOCK_STONE_BREAK).record();
            this.applyIf = null;
        }

        public boolean applyTo(String key) {
            if (applyIf == null) return true;

            if (applyIf.contains("|")) {
                for (String condition : applyIf.split("\\|"))
                    if (handleCondition(condition, key)) return true;
                return false;
            } else if (applyIf.contains("&")) {
                int count = 0;
                for (String condition : applyIf.split("&"))
                    if (handleCondition(condition, key)) count++;
                return count == applyIf.split("&").length;
            }

            return handleCondition(applyIf, key);
        }

        private boolean handleCondition(String condition, String key) {
            boolean not = condition.startsWith("!");
            boolean res;
            if (not) condition = condition.substring(1);

            if (condition.startsWith("*"))
                res = key.endsWith(condition.substring(1));
            else if (condition.endsWith("*"))
                res = key.startsWith(condition.substring(0, condition.length() - 1));
            else
                res = key.equals(condition);

            return not != res;
        }

        public void play(Player... players) {
            try {
                sound.soundPlayer().forPlayers(players).play();
            } catch (Exception e) {
                new RuntimeException("Failed to play sound: " + sound.toString(), e).printStackTrace();
            }
        }
    }
}
