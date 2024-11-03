package dk.tohjuler.mcutils.data;

import dk.tohjuler.mcutils.strings.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;

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
