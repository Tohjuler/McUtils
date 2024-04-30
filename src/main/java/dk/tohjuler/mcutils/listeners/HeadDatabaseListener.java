package dk.tohjuler.mcutils.listeners;

import dk.tohjuler.mcutils.items.HeadFonts;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.arcaniax.hdb.enums.CategoryEnum;
import me.arcaniax.hdb.object.head.Head;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class HeadDatabaseListener implements Listener {

    /**
     * Special characters to chars map
     */
    private static final Map<String, Character> charNameMap = new HashMap<>();

    static {
        charNameMap.put("blank", ' ');
        charNameMap.put("quote", '"');
        charNameMap.put("question mark", '?');
        charNameMap.put("plus", '+');
        charNameMap.put("percent", '%');
        charNameMap.put("percent sign", '%');
        charNameMap.put("octothorpe", '#');
        charNameMap.put("exclamation mark", '!');
        charNameMap.put("equals", '=');
        charNameMap.put("dot", '.');
        charNameMap.put("minus", '-');
        charNameMap.put("colon", ':');
        charNameMap.put("slash", '/');
        charNameMap.put("underscore", '_');
        charNameMap.put("semicolon", ';');
        charNameMap.put("comma", ',');
        charNameMap.put("backslash", '\\');
        charNameMap.put("apostrophe", '\'');
        charNameMap.put("ampersand", '&');
        charNameMap.put("dollar", '$');
        charNameMap.put("square bracket (open)", '[');
        charNameMap.put("square bracket (closed)", ']');
        charNameMap.put("curly bracket (open)", '{');
        charNameMap.put("curly bracket (closed)", '}');
        charNameMap.put("round bracket (open)", '(');
        charNameMap.put("round bracket (closed)", ')');
    }

    /**
     * Supported fonts
     */
    private static final List<String> fonts = Arrays.asList(
            "Blue",
            "Yellow",
            "White",
            "Red",
            "Purple",
            "Cyan",
            "Lime",
            "Pink",
            "Orange",
            "Dirt",
            "Chat",
            "Oak",
            "Mangrove Planks",
            "Smooth Sandstone",
            "Redstone Block"
    );

    /**
     * Used to replace names with spaces so the char can be found.
     */
    private static final List<String> nameReplacer = Arrays.asList(
            "Oak Wood",
            "Mangrove Plank",
            "Smooth Sandstone",
            "Redstone Block"
    );

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent paramDatabaseLoadEvent) {
        int addedChars = 0;
        HeadDatabaseAPI api = new HeadDatabaseAPI();
        List<String> heads = new ArrayList<>();
        for (Head head : api.getHeads(CategoryEnum.ALPHABET)) {
            for (String str : head.tags) {
                try {
                    String type = str.substring(6, str.length() - 1);
                    if (str.toLowerCase().contains("font") && !heads.contains(str) && fonts.contains(type))
                        heads.add(str);
                } catch (Exception ignored) {
                }
            }
        }
        for (String head : heads) {
            String type = head.substring(6, head.length() - 1);
            for (Head head2 : api.getHeads(CategoryEnum.ALPHABET)) {
                if (head2.tags.contains(head) && !head2.tags.contains("Exclusive"))
                    try {
                        addedChars++;
                        String name = head2.name;
                        for (String replacer : nameReplacer)
                            name = name.replace(replacer, "here");
                        String charr = name.split(" ", 2)[1].toLowerCase();
                        if (charr.length() == 1) {
                            HeadFonts.addChar(type, charr.charAt(0), head2.getHead(), head2.b64);
                            continue;
                        }
                        if (charNameMap.containsKey(charr))
                            HeadFonts.addChar(type, charNameMap.get(charr), head2.getHead(), head2.b64);

                        int number = Integer.parseInt(charr);
                        HeadFonts.addNumber(type, number, head2.getHead(), head2.b64);
                    } catch (Exception ignored) {
                    }
            }
        }
        System.out.println("Loaded " + addedChars + " chars from HeadDatabase");
    }
}
