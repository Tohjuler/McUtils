package dk.tohjuler.mcutils;

import com.google.gson.Gson;
import dk.tohjuler.mcutils.chat.ChatInput;
import dk.tohjuler.mcutils.hooks.VaultHook;
import dk.tohjuler.mcutils.listeners.HeadDatabaseListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to initialize the utils.
 * ChatInput and VaultHook need to be initialized.
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

        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null)
            plugin.getLogger().warning("PlaceholderAPI not found, placeholders will not work");
        return true;
    }

    /**
     * Get the instance info from the plugin.
     * If the instance info doesn't exist, null is returned.
     * The instance info is information that can be used to handle data migrations, or other things where you need the version lasted used.
     * <br/>
     *
     * @param plugin The plugin to get the instance info from
     * @return The instance info, or null if it doesn't exist
     */
    public static @Nullable InstanceInfo getInstanceInfo(JavaPlugin plugin) {
        File f = new File(plugin.getDataFolder(), "instance.lock");
        if (!f.exists()) return null;

        try {
            String str = new String(Files.readAllBytes(f.toPath()));
            str = new String(Base64.getDecoder().decode(str));

            return new Gson().fromJson(str, InstanceInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save the instance info to the plugin.
     * <br/>
     *
     * @param plugin The plugin to save the instance info to
     * @param info   The instance info to save
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveInstanceInfo(JavaPlugin plugin, InstanceInfo info) {
        File f = new File(plugin.getDataFolder(), "instance.lock");
        try {
            if (!f.exists()) f.createNewFile();

            String str = new Gson().toJson(info);
            str = Base64.getEncoder().encodeToString(str.getBytes());

            Files.write(f.toPath(), str.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The instance info class, used to store information about the plugin on the server.
     * This can be used to handle data migrations, or other things where you need the version lasted used.
     * It holds by default:
     * - The plugin version
     * - The data handler version
     * - Created at
     * - Updated at
     * You can also add extra data to the instance info.
     */
    @Getter
    @Setter
    public static class InstanceInfo {
        /// The plugin version
        private String pV;
        /// The data handler version
        private String dV;

        /// Created at
        private long cT;
        /// Updated at
        private long uT;

        ///  The extra data
        private final Map<String, Object> extra = new HashMap<>();

        public InstanceInfo(String pV, String dV) {
            this.pV = pV;
            this.dV = dV;

            cT = System.currentTimeMillis();
            uT = cT;
        }
    }
}
