package dk.tohjuler.mcutils.flags;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class FlagParser {
    protected final Map<String, Flag> flags = new HashMap<>();

    /**
     * Create a new flag
     * <p>
     *
     * @param fullName    The full name of the flag
     * @param shortAlias  The short alias of the flag
     * @param description The description of the flag
     * @return The parser
     * @since 1.20.0
     */
    public FlagParser newFlag(String fullName, String shortAlias, String description) {
        flags.put(shortAlias, new Flag(fullName, shortAlias, description));
        return this;
    }

    /**
     * Add a flag to the parser
     * <p>
     *
     * @param flags The flags to add
     * @return The parser
     * @since 1.20.0
     */
    public FlagParser addFlag(Flag... flags) {
        for (Flag flag : flags)
            this.flags.put(flag.getShortAlias(), flag);
        return this;
    }

    /**
     * Get all flags declared in the class
     * <p>
     *
     * @return A list of all flags
     * @since 1.20.0
     */
    private List<Flag> getFlags() {
        return Arrays.stream(getClass().getDeclaredFields())
                .filter(field -> field.getType().isAssignableFrom(Flag.class))
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        return (Flag) f.get(this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Parse the string
     * <p>
     *
     * @param toParse The string to parse
     * @return The string without the flags
     * @since 1.20.0
     */
    public String parse(String toParse) {
        List<Flag> flags = new ArrayList<>(getFlags());
        flags.addAll(this.flags.values());

        List<String> foundFlags = new ArrayList<>();
        StringBuilder flag = new StringBuilder();
        char lastChar = ' ';
        String flagPrefix = "";
        for (char c : toParse.toCharArray()) {
            if (c == ' ' && flag.length() > 0) {
                foundFlags.add(flagPrefix + flag);
                flags.forEach(f -> f.parse(flag.toString()));
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

        onParsed(toParse.trim());
        return toParse.trim();
    }

    /**
     * Called when the string has been parsed
     * <p>
     *
     * @param parsed The parsed string
     * @since 1.20.0
     */
    abstract void onParsed(String parsed);

    // Static methods
    // ---

    /**
     * Create a new parser
     * <p>
     *
     * @param callback The callback to call when the string has been parsed
     * @return The parser
     * @since 1.20.0
     */
    public static FlagParser newParser(BiConsumer<String, Map<String, Flag>> callback) {
        return new FlagParser() {
            @Override
            public void onParsed(String parsed) {
                callback.accept(parsed, flags);
            }
        };
    }

    /**
     * Parse a string with a single flag
     * <p>
     *
     * @param flag    The flag to parse
     * @param toParse The string to parse
     * @return The string without the flag
     */
    public static String singleFlag(Flag flag, String toParse) {
        return new FlagParser() {
            @Override
            void onParsed(String parsed) {

            }
        }.addFlag(flag)
                .parse(toParse);
    }
}
