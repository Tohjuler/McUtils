package dk.tohjuler.mcutils.flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class FlagParser {
    private final Map<String, Flag> flags = new HashMap<>();

    public FlagParser newFlag(String fullName, String shortAlias, String description) {
        flags.put(shortAlias, new Flag(fullName, shortAlias, description));
        return this;
    }

    public FlagParser addFlag(Flag... flags) {
        for (Flag flag : flags)
            this.flags.put(flag.getShortAlias(), flag);
        return this;
    }

    public void parse(String toParse, BiConsumer<String, Map<String, Flag>> callback) {
        callback.accept(directParse(toParse), flags);
    }
    public String directParse(String toParse) {
        List<String> foundFlags = new ArrayList<>();
        StringBuilder flag = new StringBuilder();
        char lastChar = ' ';
        String flagPrefix = "";
        for (char c : toParse.toCharArray()) {
            if (c == ' ' && flag.length() > 0) {
                foundFlags.add(flagPrefix + flag);
                flags.values().forEach(f -> f.parse(flag.toString()));
                flag.delete(0, flag.length() - 1);
                lastChar = ' ';
                continue;
            }
            if (c == '-' && lastChar == '-') {
                flagPrefix += "-";
                continue;
            }
            if (lastChar == '-' || flag.length() > 0)
                flag.append(c);

            if (c == '-' && flag.length() == 0) flagPrefix = "-";
            lastChar = c;
        }

        for (String f : foundFlags) {
            toParse = toParse.replace(f, "");
        }

        return toParse.trim();
    }

    // Static methods
    // ---

    public static String singleFlag(Flag flag, String toParse) {
        return new FlagParser()
                .addFlag(flag)
                .directParse(toParse);
    }
}
