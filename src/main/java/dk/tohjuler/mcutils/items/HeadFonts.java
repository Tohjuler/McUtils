package dk.tohjuler.mcutils.items;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class HeadFonts {
    private static final Map<String, ItemStack> chars = new HashMap<>();
    private static final Map<String, String> charsBase64 = new HashMap<>();

    /**
     * Add a char to the font
     * <p>
     * @param type The type of the char
     * @param c The char
     * @param value The itemstack
     * @param base64 The base64 of the itemstack
     * @since 1.8.0
     */
    public static void addChar(String type, char c, ItemStack value, String base64) {
        chars.put(type.toLowerCase()+" "+c, value);
        charsBase64.put(type.toLowerCase()+" "+c, base64);
    }

    /**
     * Get a char from the font
     * <p>
     * @param type The type of the char
     * @param c The char
     * @return The itemstack
     * @since 1.8.0
     */
    public static ItemStack getChar(String type, char c) {
        return chars.get(type.toLowerCase()+" "+c);
    }

    /**
     * Get a char from the font
     * <p>
     * @param type The type of the char
     * @param c The char
     * @return The base64 of the itemstack
     */
    public static String getCharBase64(String type, char c) {
        return charsBase64.get(type.toLowerCase()+" "+c);
    }

    /**
     * Add a number to the font
     * <p>
     * @param type The type of the char
     * @param number The number
     * @param value The itemstack
     * @param base64 The base64 of the itemstack
     * @since 1.8.0
     */
    public static void addNumber(String type, int number, ItemStack value, String base64) {
        chars.put(type.toLowerCase()+" "+number, value);
        charsBase64.put(type.toLowerCase()+" "+number, base64);
    }

    /**
     * Get a number from the font
     * <p>
     * @param type The type of the char
     * @param number The number
     * @return The itemstack
     * @since 1.8.0
     */
    public static ItemStack getNumber(String type, int number) {
        return chars.get(type.toLowerCase()+" "+number);
    }

    /**
     * Get a number from the font
     * <p>
     * @param type The type of the char
     * @param number The number
     * @return The base64 of the itemstack
     */
    public static String getNumberBase64(String type, int number) {
        return charsBase64.get(type.toLowerCase()+" "+number);
    }
}
