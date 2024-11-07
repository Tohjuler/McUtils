package dk.tohjuler.mcutils.data;

import dk.tohjuler.mcutils.strings.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Utility class for handling configuration files
 */
public class ConfigUtils {

    /**
     * Get a value from a configuration section
     * <br>
     *
     * @param cf           The configuration section
     * @param base         The base path
     * @param defaultValue The default value
     * @param keys         The keys to look for
     * @param <T>          The type of the value
     * @return The value
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(ConfigurationSection cf, String base, T defaultValue, String... keys) {
        if (!base.endsWith(".") && !base.isEmpty()) base += ".";
        for (String alias : keys)
            if (cf.isSet(base + alias))
                return (T) cf.get(base + alias);
        return defaultValue;
    }

    /**
     * Get a list from a configuration section
     * <br>
     *
     * @param cf           The configuration section
     * @param base         The base path
     * @param defaultValue The default value
     * @param keys         The keys to look for
     * @param <T>          The type of the list
     * @return The list
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getList(ConfigurationSection cf, String base, List<T> defaultValue, String... keys) {
        if (!base.endsWith(".") && !base.isEmpty()) base += ".";
        for (String alias : keys)
            if (cf.isSet(base + alias)) {
                if (cf.isList(base + alias))
                    return (List<T>) cf.getList(base + alias);
                return (List<T>) Collections.singletonList(cf.get(base + alias, defaultValue));
            }
        return defaultValue;
    }

    /**
     * Get a value from a configuration section, and execute a callback if the value is present
     * <br>
     *
     * @param cf       The configuration section
     * @param base     The base path
     * @param callback The callback to execute
     * @param keys     The keys to look for
     * @param <T>      The type of the value
     */
    public static <T> void ifPresent(ConfigurationSection cf, String base, Consumer<T> callback, String... keys) {
        T value = get(cf, base, null, keys);
        if (value != null) callback.accept(value);
    }

    /**
     * Get a list from a configuration section, and execute a callback if the list is present
     * <br>
     *
     * @param cf       The configuration section
     * @param base     The base path
     * @param callback The callback to execute
     * @param keys     The keys to look for
     * @param <T>      The type of the list
     */
    public static <T> void ifPresentList(ConfigurationSection cf, String base, Consumer<List<T>> callback, String... keys) {
        List<T> value = getList(cf, base, null, keys);
        if (value != null) callback.accept(value);
    }

    /**
     * Parse a location from a configuration section
     * <br>
     *
     * @param cf   The configuration section
     * @param keys The keys
     * @return The parsed location
     */
    public static Location parseLocation(ConfigurationSection cf, String... keys) {
        for (String key : keys) {
            Location loc = parseLocation(cf, key);
            if (loc != null) return loc;
        }
        return null;
    }

    /**
     * Parse a location from a configuration section
     * <br>
     *
     * @param cf  The configuration section
     * @param key The key
     * @return The parsed location
     */
    public static Location parseLocation(ConfigurationSection cf, String key) {
        if (cf.isString(key)) return LocationUtils.parseLocation(cf.getString(key));

        ConfigurationSection section = cf.getConfigurationSection(key);
        if (section == null) return null;
        Location loc = new Location(
                Bukkit.getWorld(section.getString("world")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z")
        );
        if (section.isSet("yaw")) loc.setYaw((float) section.getDouble("yaw"));
        if (section.isSet("pitch")) loc.setPitch((float) section.getDouble("pitch"));
        return loc;
    }

}
