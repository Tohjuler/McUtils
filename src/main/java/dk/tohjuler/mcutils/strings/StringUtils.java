package dk.tohjuler.mcutils.strings;

/**
 * A utility class for working with strings.
 */
public class StringUtils {

    /**
     * Generates a random string of the given length.
     * Characters are picked from the set [A-Z][a-z][0-9].
     * <br>
     *
     * @param length The length of the string to generate.
     * @return A random string of the given length.
     */
    public static String randomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt((int) (Math.random() * characters.length())));
        }
        return result.toString();
    }

    /**
     * Repeats a string a given number of times.
     * <br>
     *
     * @param str   The string to repeat.
     * @param times The number of times to repeat the string.
     * @return The repeated string.
     */
    public static String repeat(String str, int times) {
        return new String(new char[times]).replace("\0", str);
    }
}
