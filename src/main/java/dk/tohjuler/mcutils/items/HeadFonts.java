package dk.tohjuler.mcutils.items;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class HeadFonts {
    private static final Map<String, ItemStack> chars = new HashMap<>();
    private static final Map<String, String> charsBase64 = new HashMap<>();

    public static void addChar(String type, char c, ItemStack value, String base64) {
        chars.put(type+" "+c, value);
        charsBase64.put(type+" "+c, base64);
    }

    public static ItemStack getChar(String type, char c) {
        return chars.get(type+" "+c);
    }

    public static String getCharBase64(String type, char c) {
        return charsBase64.get(type+" "+c);
    }
}
