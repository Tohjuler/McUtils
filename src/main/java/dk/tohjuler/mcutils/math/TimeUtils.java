package dk.tohjuler.mcutils.math;

/**
 * A utility class for working with time.
 */
public class TimeUtils {

    /**
     * Convert a formatted time string to milliseconds.
     * Formatted time strings are in the format [0-9]+[smhdwMy].
     * <br>
     *
     * @param formattedTime The formatted time string to convert.
     * @return The time in milliseconds.
     */
    public static long formattedTimeToMillis(String formattedTime) {
        char unit = formattedTime.charAt(formattedTime.length() - 1);
        long time = Long.parseLong(formattedTime.substring(0, formattedTime.length() - 1));
        switch (unit) {
            case 's':
                return time * 1000;
            case 'm':
                return time * 1000 * 60;
            case 'h':
                return time * 1000 * 60 * 60;
            case 'd':
                return time * 1000 * 60 * 60 * 24;
            case 'w':
                return time * 1000 * 60 * 60 * 24 * 7;
            case 'M':
                return time * 1000 * 60 * 60 * 24 * 30;
            case 'y':
                return time * 1000 * 60 * 60 * 24 * 365;
            default:
                return -1;
        }
    }

    /**
     * Convert a formatted time string to ticks.
     * Formatted time strings are in the format [0-9]+[smhdwMy].
     * <br>
     * @param formattedTime The formatted time string to convert.
     * @return The time in ticks.
     */
    public static long formattedTimeToTicks(String formattedTime) {
        return (formattedTimeToMillis(formattedTime) / 1000) * 20;
    }

    /**
     * Format a time in milliseconds to a formatted time string.
     * Formatted time strings are in the format [0-9]+[smhdwMy].
     * Fx. 10000 => 10s, 650000 => 10m 50s
     * <br>
     *
     * @param time The time in milliseconds to format.
     * @return The formatted time string.
     */
    public static String formatTime(long time) {
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = weeks / 4;
        long years = months / 12;
        if (years > 0) {
            return years + "y " + (months % 12) + "M " + (weeks % 4) + "w " + (days % 7) + "d " + (hours % 24) + "h " + (minutes % 60) + "m " + (seconds % 60) + "s";
        } else if (months > 0) {
            return months + "M " + (weeks % 4) + "w " + (days % 7) + "d " + (hours % 24) + "h " + (minutes % 60) + "m " + (seconds % 60) + "s";
        } else if (weeks > 0) {
            return weeks + "w " + (days % 7) + "d " + (hours % 24) + "h " + (minutes % 60) + "m " + (seconds % 60) + "s";
        } else if (days > 0) {
            return days + "d " + (hours % 24) + "h " + (minutes % 60) + "m " + (seconds % 60) + "s";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m " + (seconds % 60) + "s";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }

}
