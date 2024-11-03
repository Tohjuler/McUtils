package dk.tohjuler.mcutils.strings;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Utility class for handling locations
 */
public class LocationUtils {

    /**
     * Stringify a location
     * With the format: world,x,y,z,yaw,pitch
     * <br>
     *
     * @param location The location to stringify
     * @return The stringified location
     */
    public static String locationToString(Location location) {
        String loc = location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
        loc = loc.replace(".0", "");
        return loc;
    }

    /**
     * Stringify a location
     * With the format: world,x,y,z
     * <br>
     *
     * @param location The location to stringify
     * @return The stringified location
     */
    public static String locationToStringNoYawPitch(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    /**
     * Parse a location from a string
     * With the format: world,x,y,z,[yaw,pitch]
     * <br>
     *
     * @param location The stringified location
     * @return The parsed location
     */
    public static Location parseLocation(String location) {
        String[] parts = location.split(",");
        if (parts.length < 4) return null;
        Location loc = new Location(null, 0, 0, 0);
        loc.setWorld(Bukkit.getWorld(parts[0]));
        loc.setX(Double.parseDouble(parts[1]));
        loc.setY(Double.parseDouble(parts[2]));
        loc.setZ(Double.parseDouble(parts[3]));
        if (parts.length > 4) loc.setYaw(Float.parseFloat(parts[4]));
        if (parts.length > 5) loc.setPitch(Float.parseFloat(parts[5]));
        return loc;
    }

}
